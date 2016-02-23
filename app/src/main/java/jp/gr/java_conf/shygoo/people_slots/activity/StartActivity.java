package jp.gr.java_conf.shygoo.people_slots.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.State;
import jp.gr.java_conf.shygoo.people_slots.R;
import jp.gr.java_conf.shygoo.people_slots.fragment.dialog.ChoiceDialogFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.dialog.NameInputDialogFragment;

/**
 * 初期画面
 */
public class StartActivity extends BaseActivity implements ChoiceDialogFragment.OnSelectListener,
        NameInputDialogFragment.OnFinishInputListener {

    private static final String LOG_TAG = OcrActivity.class.getSimpleName();

    private static final int REQUEST_CODE_PERMISSION = 1;

    @State
    Uri rawImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        requestStoragePermission();
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
                //TODO
                break;
            case R.string.capture_method_manual:
                //TODO
                break;
            case R.string.input_method_ocr:
                //TODO
                break;
            case R.string.input_method_manual:
                requestInputName();
                break;
            default:
                // nop
                break;
        }
    }

    @Override
    public void onFinishInput(String name) {
    }

    public void onFinishOcr(String[] roster) {
        //TODO
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
     * 外部アプリからの結果を受け取る
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            // 画像要求の結果
            case R.string.request_group_photo:
            case R.string.request_roster_photo:
                if (resultCode == RESULT_OK) {

                    // トリミング実行
                    if (data == null || data.getData() == null) {
                        //requestCrop(rawImageUri);
                    } else {
                        //requestCrop(data.getData());
                    }
                } else {
                    deleteRawImage();
                    Toast.makeText(this, R.string.error_raw_photo, Toast.LENGTH_SHORT).show();
                }
                break;

//            // トリミング要求の結果
//            case REQUEST_CROP:
//                deletePhotoImage();
//                if (resultCode == RESULT_OK) {
//
//                    // OCR実行
//                    requestOcr();
//                } else {
//                    deleteCroppedImage();
//                    Log.w(LOG_TAG, "crop image failed.");
//                }
//                break;

            // デフォルト処理
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
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
     * 顔認識用画像（写真）の要求
     */
    private void requestFrImage() {
        requestRawImage(R.string.request_group_photo);
    }

    /**
     * 顔画像の要求
     */
    private void requestFaceImage() {
        requestRawImage(R.string.request_roster_photo);
    }

    /**
     * OCR用画像（名簿）の要求
     */
    private void requestOcrImage() {
        requestRawImage(R.string.request_roster_photo);
    }

    /**
     * 生画像要求
     */
    private void requestRawImage(@StringRes int messageId) {

        // カメラ起動Intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "raw" + UUID.randomUUID().toString());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        rawImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, rawImageUri);

        // 画像選択Intent
        Intent fileIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fileIntent.setType("image/*");

        // どちらの方法でも良いので、画像を要求
        Intent chooSerIntent = Intent.createChooser(cameraIntent, getString(messageId));
        chooSerIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{fileIntent});
        startActivityForResult(chooSerIntent, messageId);
    }

    /**
     * 生画像を削除
     */
    private void deleteRawImage() {
        deleteImage(rawImageUri);
        rawImageUri = null;
    }

    /**
     * 画像削除（汎用）
     */
    private void deleteImage(Uri uri) {
        if (uri != null) {
            getContentResolver().delete(uri, null, null);
        }
    }

    /**
     * 名前入力要求
     */
    private void requestInputName() {
        new NameInputDialogFragment().show(getFragmentManager(), "request_input_name");
    }
}
