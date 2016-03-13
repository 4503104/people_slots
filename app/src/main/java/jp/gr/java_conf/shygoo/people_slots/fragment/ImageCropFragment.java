package jp.gr.java_conf.shygoo.people_slots.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.Calendar;

/**
 * 画像の一部を切り出す機能
 */
public class ImageCropFragment extends BaseFragment {

    private static final String LOG_TAG = ImageCropFragment.class.getSimpleName();

    private static final String ARG_TARGET_IMAGE_URI = "targetImageUri";
    private static final String ARG_MESSAGE_ID = "messageId";
    private static final String ARG_CROPPED_IMAGE_NAME = "croppedImageName";

    /**
     * インスタンス生成
     *
     * @param targetImageUri 対象の画像
     * @param messageId      ユーザに表示されるメッセージ
     * @return 新規Fragment
     */
    public static ImageCropFragment newInstance(@NonNull Uri targetImageUri, @StringRes int messageId) {
        ImageCropFragment fragment = new ImageCropFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TARGET_IMAGE_URI, targetImageUri);
        args.putInt(ARG_MESSAGE_ID, messageId);
        args.putString(ARG_CROPPED_IMAGE_NAME, generateOutputFilename());// この時点でファイル名を決めておく（Activity破棄対策）
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 出力先ファイル名生成
     *
     * @return 日時をベースにした新規画像ファイル名
     */
    private static String generateOutputFilename() {
        CharSequence date = DateFormat.format("yyyyMMdd_kkmmss", Calendar.getInstance());
        return String.format("cropped_%s", date);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestCrop();
    }

    /**
     * 画像切り出し要求
     */
    private void requestCrop() {

        // 引数を取得
        Bundle args = getArguments();
        Uri targetImageUri = args.getParcelable(ARG_TARGET_IMAGE_URI);
        int messageId = args.getInt(ARG_MESSAGE_ID);
        String croppedImageName = args.getString(ARG_CROPPED_IMAGE_NAME, "");

        // 既にファイルがある（＝切り出し済み）ならスキップ（Activity破棄対策）
        File dir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File croppedImageFile = new File(dir, croppedImageName);
        Uri croppedImageUri = Uri.fromFile(croppedImageFile);
        if (croppedImageFile.exists()) {
            notifyResult(croppedImageUri);
            return;
        }

        // 切り出しライブラリの画面を開く
        Crop.of(targetImageUri, croppedImageUri).start(getActivity(), this);

        // 何をすれば良いか分かりにくいのでガイドメッセージを表示
        Toast.makeText(getActivity(), messageId, Toast.LENGTH_SHORT).show();// TODO: Toastでいいのか？
    }

    /**
     * 結果受信
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {

                // 成功
                notifyResult(Crop.getOutput(data));
            } else {

                // 失敗
                String errorMessage;
                if (resultCode == Crop.RESULT_ERROR) {
                    // noinspection ThrowableResultOfMethodCallIgnored
                    errorMessage = Crop.getError(data).getMessage();
                } else {
                    errorMessage = "Unknown cropping error!";
                }
                Log.w(LOG_TAG, errorMessage);
                notifyResult(null);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 結果通知
     *
     * @param croppedImageUri 切り出した画像のURI
     */
    private void notifyResult(Uri croppedImageUri) {

        // ListenerがあればURIを渡す
        OnCropListener listener = null;
        Activity activity = getActivity();
        if (activity instanceof OnCropListener) {
            listener = (OnCropListener) activity;
        } else {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment instanceof OnCropListener) {
                listener = (OnCropListener) targetFragment;
            }
        }
        if (listener != null) {
            listener.onCropImage(getTag(), croppedImageUri);
        }
    }

    /**
     * 結果通知用Listener
     */
    public interface OnCropListener {

        /**
         * 画像切り出しイベント
         *
         * @param tag Fragment識別子
         * @param croppedImageUri 切り出した画像
         */
        void onCropImage(String tag, Uri croppedImageUri);
    }
}
