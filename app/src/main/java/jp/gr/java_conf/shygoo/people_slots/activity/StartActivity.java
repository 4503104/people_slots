package jp.gr.java_conf.shygoo.people_slots.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.gr.java_conf.shygoo.people_slots.R;
import jp.gr.java_conf.shygoo.people_slots.fragment.ImageCaptureFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.ImageCropFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.OcrFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.dialog.ChoiceDialogFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.dialog.NameInputDialogFragment;

/**
 * 初期画面
 */
public class StartActivity extends BaseActivity implements ChoiceDialogFragment.OnSelectListener,
        ImageCaptureFragment.OnCaptureListener, NameInputDialogFragment.OnFinishInputListener,
        ImageCropFragment.OnCropListener, OcrFragment.OnFinishOcrListener {

    private static final String LOG_TAG = StartActivity.class.getSimpleName();

    private static final int REQUEST_CODE_PERMISSION = 1;

    /**
     * 初期処理
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        requestStoragePermission();
    }

    /**
     * ストレージ利用許可要求
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
    }

    /**
     * 許可要求の結果を受け取る
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            // ストレージ書き込み許可がなければ使わせない
            case REQUEST_CODE_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.error_storage_permission, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    /**
     * スロットの種類を尋ねる
     */
    @OnClick(R.id.create_slot)
    public void askType() {
        new ChoiceDialogFragment.Builder()
                .setTitle(R.string.ask_type)
                .setItems(R.array.slot_types)
                .create()
                .show(getFragmentManager(), "ask_type");
    }

    /**
     * 顔取り込みの手段を尋ねる
     */
    private void askCaptureMethod() {
        new ChoiceDialogFragment.Builder()
                .setTitle(R.string.ask_capture_method)
                .setItems(R.array.capture_methods)
                .create()
                .show(getFragmentManager(), "ask_capture_method");
    }

    /**
     * 名前取り込みの手段を尋ねる
     */
    private void askInputMethod() {
        new ChoiceDialogFragment.Builder()
                .setTitle(R.string.ask_input_method)
                .setItems(R.array.input_methods)
                .create()
                .show(getFragmentManager(), "ask_input_method");
    }

    /**
     * （ダイアログの）選択結果を受け取る
     *
     * @param itemId 選択肢のID
     */
    @Override
    public void onSelectItem(int itemId) {
        switch (itemId) {
            case R.string.type_face:
                askCaptureMethod();
                break;
            case R.string.type_name:
                askInputMethod();
                break;
            case R.string.capture_method_fr:
                requestFrImage();
                break;
            case R.string.capture_method_manual:
                requestFaceImage();
                break;
            case R.string.input_method_ocr:
                requestOcrImage();
                break;
            case R.string.input_method_manual:
                requestInputName();
                break;
        }
    }

    /**
     * 顔認識用画像（写真）の要求
     */
    private void requestFrImage() {
        requestImage(R.string.request_group_photo, "request_group_photo");
    }

    /**
     * 顔画像（単独）の要求
     */
    private void requestFaceImage() {
        requestImage(R.string.request_portrait_photo, "request_portrait_photo");
    }

    /**
     * OCR用画像（名簿）の要求
     */
    private void requestOcrImage() {
        requestImage(R.string.request_roster_photo, "request_roster_photo");
    }

    /**
     * 画像要求（汎用）
     *
     * @param messageId
     * @param tag
     */
    private void requestImage(@StringRes int messageId, @NonNull String tag) {
        Fragment fragment = ImageCaptureFragment.newInstance(messageId);
        getFragmentManager().beginTransaction().add(fragment, tag).commit();
    }

    /**
     * 画像取得完了
     *
     * @param tag
     * @param imageUri
     */
    @Override
    public void onCaptureImage(String tag, Uri imageUri) {
        if (imageUri == null) {

            // 失敗
            Toast.makeText(this, R.string.error_capture_image, Toast.LENGTH_SHORT).show();
        } else {
            Log.d(LOG_TAG, "capturedImage: " + imageUri);

            // 成功
            Fragment fragment = getFragmentManager().findFragmentByTag(tag);
            getFragmentManager().beginTransaction().remove(fragment).commit();

            // 次の処理を開始
            switch (tag) {
                case "request_group_photo":
                    //TODO
                    break;
                case "request_portrait_photo":
                    requestCropFace(imageUri);
                    break;
                case "request_roster_photo":
                    requestCropOcr(imageUri);
                    break;
            }
        }
    }

    /**
     * 顔画像（単独）の切り出し要求
     */
    private void requestCropFace(Uri targetImageUri) {
        requestCrop(targetImageUri, R.string.request_crop_portrait, "request_crop_portrait");
    }

    /**
     * OCR用画像（名簿）の切り出し要求
     */
    private void requestCropOcr(Uri targetImageUri) {
        requestCrop(targetImageUri, R.string.request_crop_roster, "request_crop_roster");
    }

    /**
     * 切り出し要求（汎用）
     *
     * @param targetImageUri
     * @param messageId
     * @param tag
     */
    private void requestCrop(@NonNull Uri targetImageUri, @StringRes int messageId, @NonNull String tag) {
        Fragment fragment = ImageCropFragment.newInstance(targetImageUri, messageId);
        getFragmentManager().beginTransaction().add(fragment, tag).commit();
    }

    /**
     * 画像切り出し完了
     *
     * @param tag
     * @param croppedImageUri
     */
    @Override
    public void onCropImage(String tag, Uri croppedImageUri) {
        if (croppedImageUri == null) {

            // 失敗
            Toast.makeText(this, R.string.error_crop_image, Toast.LENGTH_SHORT).show();
        } else {
            Log.d(LOG_TAG, "croppedImage: " + croppedImageUri);

            // 成功
            Fragment fragment = getFragmentManager().findFragmentByTag(tag);
            getFragmentManager().beginTransaction().remove(fragment).commit();

            // 次の処理を開始
            switch (tag) {
                case "request_crop_portrait":
                    //TODO
                    break;
                case "request_crop_roster":
                    requestOcr(croppedImageUri);
                    break;
            }
        }
    }

    /**
     * OCR要求
     *
     * @param targetImageUri
     */
    private void requestOcr(Uri targetImageUri) {
        OcrFragment.newInstance(targetImageUri);
        Fragment fragment = OcrFragment.newInstance(targetImageUri);
        getFragmentManager().beginTransaction().add(fragment, "requestOcr").commit();
    }

    /**
     * OCR完了
     *
     * @param roster
     */
    @Override
    public void onFinishOcr(List<String> roster) {
        //TODO
        Log.d(LOG_TAG, roster.toString());
    }

    /**
     * 名前入力要求
     */
    private void requestInputName() {
        new NameInputDialogFragment().show(getFragmentManager(), "request_input_name");
    }

    /**
     * 名前入力完了
     *
     * @param name
     */
    @Override
    public void onFinishInput(String name) {
        //TODO
    }

    public void onFinishOcr(String[] roster) {
        //TODO
    }
}
