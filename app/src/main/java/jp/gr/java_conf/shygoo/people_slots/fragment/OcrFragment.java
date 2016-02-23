package jp.gr.java_conf.shygoo.people_slots.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.List;

import icepick.State;
import jp.gr.java_conf.shygoo.people_slots.R;
import jp.gr.java_conf.shygoo.people_slots.fragment.dialog.ProgressDialogFragment;
import jp.gr.java_conf.shygoo.people_slots.task.OcrTaskLoader;

/**
 * OCR（文字認識）機能
 */
public class OcrFragment extends BaseFragment {

    private static final String LOG_TAG = OcrFragment.class.getSimpleName();

    private static final int SELF_TASK_ID = 4;

    private static final String TAG_PROGRESS_DIALOG = "progressOCR";

    private static final String ARG_TARGET_IMAGE_URI = "targetImageUri";

    @State
    Uri cameraImageUri;

    /**
     * インスタンス生成
     *
     * @param targetImageUri 対象画像
     * @return
     */
    public static OcrFragment newInstance(@NonNull Uri targetImageUri) {
        OcrFragment fragment = new OcrFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TARGET_IMAGE_URI, targetImageUri);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 生成時の処理
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgress();
        requestOcr();
    }

    /**
     * 進捗表示
     */
    private void showProgress() {
        new ProgressDialogFragment.Builder()
                .setMessage(R.string.progress_ocr)
                .create()
                .show(getFragmentManager(), TAG_PROGRESS_DIALOG);
    }

    /**
     * OCR要求
     */
    private void requestOcr() {

        // バックグラウンドタスクを実行
        Bundle args = new Bundle(getArguments());
        getLoaderManager().initLoader(SELF_TASK_ID, args, new LoaderManager.LoaderCallbacks<List<String>>() {

            @Override
            public Loader<List<String>> onCreateLoader(int id, Bundle args) {
                Uri targetImageUri = args.getParcelable(ARG_TARGET_IMAGE_URI);
                return new OcrTaskLoader(getActivity(), targetImageUri);
            }

            @Override
            public void onLoadFinished(Loader<List<String>> loader, List<String> data) {

                // 結果通知
                notifyResult(data);
            }

            @Override
            public void onLoaderReset(Loader<List<String>> loader) {
                Log.i(LOG_TAG, "ocr canceld.");
            }
        }).forceLoad();
    }

    /**
     * 結果通知
     *
     * @param roster 画像から読み取った名前のリスト
     */
    private void notifyResult(List<String> roster) {

        // ListenerがあればURIを渡す
        OnFinishOcrListener listener = null;
        Activity activity = getActivity();
        if (activity instanceof OnFinishOcrListener) {
            listener = (OnFinishOcrListener) activity;
        } else {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment instanceof OnFinishOcrListener) {
                listener = (OnFinishOcrListener) targetFragment;
            }
        }
        if (listener != null) {
            listener.onFinishOcr(roster);
        }

        // 元画像はいらないので削除
        deleteTargetImage();

        // ぐるぐる閉じる
        dissmissProgress();
    }

    /**
     * 画像削除
     */
    private void deleteTargetImage() {
        Uri targetImageUri = getArguments().getParcelable(ARG_TARGET_IMAGE_URI);
        new File(targetImageUri.getPath()).deleteOnExit();
    }

    /**
     * 進捗閉じる
     */
    private void dissmissProgress() {
        ProgressDialogFragment fragment = (ProgressDialogFragment) getFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG);
        if (fragment != null) {
            fragment.getDialog().dismiss();
        }
    }

    /**
     * 結果通知用Listener
     */
    public interface OnFinishOcrListener {
        void onFinishOcr(List<String> roster);
    }
}
