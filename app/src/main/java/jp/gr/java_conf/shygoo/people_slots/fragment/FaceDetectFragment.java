package jp.gr.java_conf.shygoo.people_slots.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import jp.gr.java_conf.shygoo.people_slots.R;
import jp.gr.java_conf.shygoo.people_slots.fragment.dialog.ProgressDialogFragment;
import jp.gr.java_conf.shygoo.people_slots.task.FaceDetectTaskLoader;

/**
 * 顔検出機能
 */
public class FaceDetectFragment extends BaseFragment {

    private static final String LOG_TAG = FaceDetectFragment.class.getSimpleName();

    private static final int SELF_TASK_ID = 3;

    private static final String TAG_PROGRESS_DIALOG = "progressFaceDetect";

    private static final String ARG_TARGET_IMAGE_URI = "targetImageUri";

    /**
     * インスタンス生成
     *
     * @param targetImageUri 対象画像
     * @return 新規Fragment
     */
    public static FaceDetectFragment newInstance(@NonNull Uri targetImageUri) {
        FaceDetectFragment fragment = new FaceDetectFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TARGET_IMAGE_URI, targetImageUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgress();
        requestDetect();
    }

    /**
     * 進捗表示
     */
    private void showProgress() {
        new ProgressDialogFragment.Builder()
                .setMessage(R.string.progress_detect)
                .create()
                .show(getFragmentManager(), TAG_PROGRESS_DIALOG);
    }

    /**
     * 顔検出要求
     */
    private void requestDetect() {

        // バックグラウンドタスクを実行
        Bundle args = new Bundle(getArguments());
        getLoaderManager().initLoader(SELF_TASK_ID, args, new LoaderManager.LoaderCallbacks<List<Uri>>() {

            @Override
            public Loader<List<Uri>> onCreateLoader(int id, Bundle args) {
                Uri targetImageUri = args.getParcelable(ARG_TARGET_IMAGE_URI);
                return new FaceDetectTaskLoader(getActivity(), targetImageUri);
            }

            @Override
            public void onLoadFinished(Loader<List<Uri>> loader, List<Uri> data) {

                // 結果通知
                notifyResult(data);
            }

            @Override
            public void onLoaderReset(Loader<List<Uri>> loader) {
                Log.i(LOG_TAG, "detect canceld.");
            }
        }).forceLoad();
    }

    /**
     * 結果通知
     *
     * @param faces 画像から切り出した顔画像URIのリスト
     */
    private void notifyResult(List<Uri> faces) {

        // Listenerがあれば顔データを渡す
        OnDetectListener listener = null;
        Activity activity = getActivity();
        if (activity instanceof OnDetectListener) {
            listener = (OnDetectListener) activity;
        } else {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment instanceof OnDetectListener) {
                listener = (OnDetectListener) targetFragment;
            }
        }
        if (listener != null) {
            listener.onDetectFaces(faces);
        }

        // ぐるぐる閉じる
        dismissProgress();
    }

    /**
     * 進捗閉じる
     */
    private void dismissProgress() {
        ProgressDialogFragment fragment = (ProgressDialogFragment) getFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG);
        if (fragment != null) {
            fragment.getDialog().dismiss();
        }
    }

    /**
     * 結果通知用Listener
     */
    public interface OnDetectListener {

        /**
         * 顔検出イベント
         *
         * @param faces 検出された顔
         */
        void onDetectFaces(List<Uri> faces);
    }
}
