package jp.gr.java_conf.shygoo.people_slots.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.gr.java_conf.shygoo.people_slots.R;
import jp.gr.java_conf.shygoo.people_slots.fragment.FaceDetectFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.ImageCaptureFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.ImageCropFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.OcrFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.dialog.ChoiceDialogFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.dialog.NameInputDialogFragment;

/**
 * 初期画面
 * TODO: 処理多過ぎ。クラス分けたい。
 */
public class StartActivity extends BaseActivity
        implements ChoiceDialogFragment.OnSelectListener,
        ImageCaptureFragment.OnCaptureListener,
        ImageCropFragment.OnCropListener,
        FaceDetectFragment.OnDetectListener,
        OcrFragment.OnFinishOcrListener,
        NameInputDialogFragment.OnFinishInputListener {

    private static final String LOG_TAG = StartActivity.class.getSimpleName();

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    // フラグメント呼び出し用
    private static final String TAG_ASK_SLOT_TYPE = "askSlotType";
    private static final String TAG_ASK_CAPTURE_METHOD = "askCaptureMethod";
    private static final String TAG_ASK_INPUT_METHOD = "askInputMethod";
    private static final String TAG_REQUEST_GROUP_PHOTO = "requestGroupPhoto";
    private static final String TAG_REQUEST_PORTRAIT_PHOTO = "requestPortraitPhoto";
    private static final String TAG_REQUEST_ROSTER_PHOTO = "requestRosterPhoto";
    private static final String TAG_REQUEST_DETECT_FACES = "requestDetectFaces";
    private static final String TAG_REQUEST_CROP_FACE = "requestCropFace";
    private static final String TAG_REQUEST_CROP_ROSTER = "requestCropRoster";
    private static final String TAG_REQUEST_OCR = "requestOcr";
    private static final String TAG_REQUEST_INPUT_NAME = "requestInputName";

    @StringDef({TAG_REQUEST_GROUP_PHOTO, TAG_REQUEST_PORTRAIT_PHOTO, TAG_REQUEST_ROSTER_PHOTO})
    public @interface RequestImageTag {
    }

    @StringDef({TAG_REQUEST_CROP_FACE, TAG_REQUEST_CROP_ROSTER})
    public @interface RequestCropTag {
    }

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
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
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
            case REQUEST_CODE_STORAGE_PERMISSION:
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
    public void askSlotType() {
        new ChoiceDialogFragment.Builder()
                .setTitle(R.string.ask_type)
                .setItems(R.array.slot_types)
                .create()
                .show(getFragmentManager(), TAG_ASK_SLOT_TYPE);
    }

    /**
     * 顔取り込みの手段を尋ねる
     */
    private void askCaptureMethod() {
        new ChoiceDialogFragment.Builder()
                .setTitle(R.string.ask_capture_method)
                .setItems(R.array.capture_methods)
                .create()
                .show(getFragmentManager(), TAG_ASK_CAPTURE_METHOD);
    }

    /**
     * 名前取り込みの手段を尋ねる
     */
    private void askInputMethod() {
        new ChoiceDialogFragment.Builder()
                .setTitle(R.string.ask_input_method)
                .setItems(R.array.input_methods)
                .create()
                .show(getFragmentManager(), TAG_ASK_INPUT_METHOD);
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
            case R.string.capture_method_detect:
                requestGroupPhoto();
                break;
            case R.string.capture_method_manual:
                requestPortraitPhoto();
                break;
            case R.string.input_method_ocr:
                requestRosterPhoto();
                break;
            case R.string.input_method_manual:
                requestInputName();
                break;
        }
    }

    /**
     * 顔検出用画像（集合写真）の要求
     */
    private void requestGroupPhoto() {
        requestImage(R.string.request_group_photo, TAG_REQUEST_GROUP_PHOTO);
    }

    /**
     * 顔画像（肖像写真）の要求
     */
    private void requestPortraitPhoto() {
        requestImage(R.string.request_portrait_photo, TAG_REQUEST_PORTRAIT_PHOTO);
    }

    /**
     * OCR用画像（名簿写真）の要求
     */
    private void requestRosterPhoto() {
        requestImage(R.string.request_roster_photo, TAG_REQUEST_ROSTER_PHOTO);
    }

    /**
     * 画像要求（汎用）
     *
     * @param messageId
     * @param tag
     */
    private void requestImage(@StringRes int messageId, @RequestImageTag String tag) {
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
                case TAG_REQUEST_GROUP_PHOTO:
                    requestDetectFaces(imageUri);
                    break;
                case TAG_REQUEST_PORTRAIT_PHOTO:
                    requestCropFace(imageUri);
                    break;
                case TAG_REQUEST_ROSTER_PHOTO:
                    requestCropRoster(imageUri);
                    break;
            }
        }
    }

    /**
     * 顔検出の要求
     *
     * @param targetImageUri
     */
    private void requestDetectFaces(Uri targetImageUri) {
        Fragment fragment = FaceDetectFragment.newInstance(targetImageUri);
        getFragmentManager().beginTransaction().add(fragment, TAG_REQUEST_DETECT_FACES).commit();
    }

    /**
     * 顔検出完了
     *
     * @param faces
     */
    @Override
    public void onDetectFaces(List<Uri> faces) {
        startFacePreview(faces);
    }

    /**
     * 顔画像（単独）の切り出し要求
     */
    private void requestCropFace(Uri targetImageUri) {
        requestCrop(targetImageUri, R.string.request_crop_portrait, TAG_REQUEST_CROP_FACE);
    }

    /**
     * OCR用画像（名簿）の切り出し要求
     */
    private void requestCropRoster(Uri targetImageUri) {
        requestCrop(targetImageUri, R.string.request_crop_roster, TAG_REQUEST_CROP_ROSTER);
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
    public void onCropImage(@RequestCropTag String tag, Uri croppedImageUri) {
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
                case TAG_REQUEST_CROP_FACE:
                    startFacePreview(Collections.singletonList(croppedImageUri));
                    break;
                case TAG_REQUEST_CROP_ROSTER:
                    requestOcr(croppedImageUri);
                    break;
            }
        }
    }

    /**
     * 顔プレビュー開始
     */
    private void startFacePreview(List<Uri> faces) {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra(PreviewActivity.EXTRA_SLOT_TYPE, PreviewActivity.SLOT_TYPE_FACE);
        intent.putParcelableArrayListExtra(PreviewActivity.EXTRA_ITEMS, new ArrayList<>(faces));
        startActivity(intent);
    }

    /**
     * OCR要求
     *
     * @param targetImageUri
     */
    private void requestOcr(Uri targetImageUri) {
        OcrFragment.newInstance(targetImageUri);
        Fragment fragment = OcrFragment.newInstance(targetImageUri);
        getFragmentManager().beginTransaction().add(fragment, TAG_REQUEST_OCR).commit();
    }

    /**
     * OCR完了
     *
     * @param roster
     */
    @Override
    public void onFinishOcr(List<String> roster) {
        startNamePreview(roster);
    }

    /**
     * 名前入力要求
     */
    private void requestInputName() {
        new NameInputDialogFragment().show(getFragmentManager(), TAG_REQUEST_INPUT_NAME);
    }

    /**
     * 名前入力完了
     *
     * @param name
     */
    @Override
    public void onFinishInput(String name) {
        startNamePreview(Collections.singletonList(name));
    }

    /**
     * 名前プレビュー開始
     */
    private void startNamePreview(List<String> names) {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra(PreviewActivity.EXTRA_SLOT_TYPE, PreviewActivity.SLOT_TYPE_NAME);
        intent.putStringArrayListExtra(PreviewActivity.EXTRA_ITEMS, new ArrayList<>(names));
        startActivity(intent);
    }
}
