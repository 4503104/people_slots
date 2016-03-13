package jp.gr.java_conf.shygoo.people_slots.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.text.format.DateFormat;

import java.util.Calendar;

import icepick.State;

/**
 * 画像取り込み機能
 */
public class ImageCaptureFragment extends BaseFragment {

    private static final int SELF_REQUEST_CODE = 1;

    private static final String ARG_MESSAGE_ID = "messageId";

    @State
    Uri cameraImageUri;

    /**
     * インスタンス生成
     *
     * @param messageId ユーザに表示されるメッセージ
     * @return 新規Fragment
     */
    public static ImageCaptureFragment newInstance(@StringRes int messageId) {
        ImageCaptureFragment fragment = new ImageCaptureFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MESSAGE_ID, messageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestImage();
    }

    /**
     * 画像要求
     */
    private void requestImage() {

        // カメラ起動Intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, generateImageName());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        cameraImageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);

        // 画像選択Intent
        Intent fileIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fileIntent.setType("image/*");

        // どちらの方法でも良いので、画像を要求
        Intent chooserIntent = Intent.createChooser(cameraIntent, getString(getArguments().getInt(ARG_MESSAGE_ID)));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{fileIntent});
        startActivityForResult(chooserIntent, SELF_REQUEST_CODE);
    }

    /**
     * 画像ファイル名生成
     *
     * @return 日時をベースにした新規画像ファイル名
     */
    private String generateImageName() {
        CharSequence date = DateFormat.format("yyyyMMdd_kkmmss", Calendar.getInstance());
        return String.format("captured_%s.jpg", date);
    }

    /**
     * 結果受信
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELF_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data == null || data.getData() == null) {

                    // カメラ撮影成功
                    notifyResult(cameraImageUri);
                } else {

                    // 画像選択成功
                    notifyResult(data.getData());
                }
            } else {

                // 失敗
                notifyResult(null);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 結果通知
     *
     * @param imageUri 取得した画像のURI
     */
    private void notifyResult(Uri imageUri) {

        // ListenerがあればURIを渡す
        OnCaptureListener listener = null;
        Activity activity = getActivity();
        if (activity instanceof OnCaptureListener) {
            listener = (OnCaptureListener) activity;
        } else {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment instanceof OnCaptureListener) {
                listener = (OnCaptureListener) targetFragment;
            }
        }
        if (listener != null) {
            listener.onCaptureImage(getTag(), imageUri);
        }
    }

    /**
     * 結果通知用Listener
     */
    public interface OnCaptureListener {

        /**
         * 画像取り込みイベント
         *
         * @param tag Fragment識別子
         * @param imageUri 取り込んだ画像
         */
        void onCaptureImage(String tag, Uri imageUri);
    }
}
