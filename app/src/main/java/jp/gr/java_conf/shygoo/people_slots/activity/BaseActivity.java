package jp.gr.java_conf.shygoo.people_slots.activity;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import icepick.Icepick;
import icepick.State;
import jp.gr.java_conf.shygoo.people_slots.R;
import jp.gr.java_conf.shygoo.people_slots.fragment.FaceDetectFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.ImageCaptureFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.ImageCropFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.OcrFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.dialog.ChoiceDialogFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.dialog.NameInputDialogFragment;

/**
 * 基底Activity
 * TODO: 画面によって使わないメソッドもあるので整理
 */
public class BaseActivity extends AppCompatActivity
        implements ChoiceDialogFragment.OnSelectListener,
        ImageCaptureFragment.OnCaptureListener,
        ImageCropFragment.OnCropListener,
        FaceDetectFragment.OnDetectListener,
        OcrFragment.OnFinishOcrListener,
        NameInputDialogFragment.OnFinishInputListener {

    // 引数関連
    protected static final String EXTRA_SLOT_TYPE = "itemType";
    protected static final String EXTRA_SLOT_ITEMS = "items";
    protected static final int SLOT_TYPE_NAME = 1;
    protected static final int SLOT_TYPE_FACE = 2;

    // フラグメント呼び出し用
    protected static final String TAG_ASK_CAPTURE_METHOD = "askCaptureMethod";
    protected static final String TAG_ASK_INPUT_METHOD = "askInputMethod";
    protected static final String TAG_REQUEST_GROUP_PHOTO = "requestGroupPhoto";
    protected static final String TAG_REQUEST_PORTRAIT_PHOTO = "requestPortraitPhoto";
    protected static final String TAG_REQUEST_ROSTER_PHOTO = "requestRosterPhoto";
    protected static final String TAG_REQUEST_DETECT_FACES = "requestDetectFaces";
    protected static final String TAG_REQUEST_CROP_FACE = "requestCropFace";
    protected static final String TAG_REQUEST_CROP_ROSTER = "requestCropRoster";
    protected static final String TAG_REQUEST_OCR = "requestOcr";
    protected static final String TAG_REQUEST_INPUT_NAME = "requestInputName";

    @State
    protected int slotType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        slotType = getIntent().getIntExtra(EXTRA_SLOT_TYPE, 0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Icepick.saveInstanceState(this, outState);
    }

    /**
     * Intentで渡された顔情報を返す
     *
     * @return 顔情報
     */
    protected ArrayList<Uri> getPresetFaces() {
        return getIntent().getParcelableArrayListExtra(EXTRA_SLOT_ITEMS);
    }

    /**
     * Intentで渡された名前情報を返す
     *
     * @return 名前情報
     */
    protected ArrayList<String> getPresetNames() {
        return getIntent().getStringArrayListExtra(EXTRA_SLOT_ITEMS);
    }

    /**
     * 顔取り込みの手段を尋ねる
     */
    protected void askCaptureMethod() {
        new ChoiceDialogFragment.Builder()
                .setTitle(R.string.ask_capture_method)
                .setItems(R.array.capture_methods)
                .create()
                .show(getFragmentManager(), TAG_ASK_CAPTURE_METHOD);
    }

    /**
     * 名前取り込みの手段を尋ねる
     */
    protected void askInputMethod() {
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
    protected void requestGroupPhoto() {
        requestImage(R.string.request_group_photo, TAG_REQUEST_GROUP_PHOTO);
    }

    /**
     * 顔画像（肖像写真）の要求
     */
    protected void requestPortraitPhoto() {
        requestImage(R.string.request_portrait_photo, TAG_REQUEST_PORTRAIT_PHOTO);
    }

    /**
     * OCR用画像（名簿写真）の要求
     */
    protected void requestRosterPhoto() {
        requestImage(R.string.request_roster_photo, TAG_REQUEST_ROSTER_PHOTO);
    }

    /**
     * 画像要求（汎用）
     *
     * @param messageId
     * @param tag
     */
    protected void requestImage(@StringRes int messageId, @NonNull String tag) {
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

        // お掃除
        removeFragment(tag);

        // 成否判定
        if (imageUri == null) {

            // 失敗
            Toast.makeText(this, R.string.error_capture_image, Toast.LENGTH_SHORT).show();
        } else {

            // 成功時は次の処理
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
     * 指定されたFragmentを削除
     *
     * @param tag
     */
    protected void removeFragment(String tag) {
        Fragment fragment = getFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    /**
     * 顔検出の要求
     *
     * @param targetImageUri
     */
    protected void requestDetectFaces(Uri targetImageUri) {
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
        // nop
    }

    /**
     * 顔画像（単独）の切り出し要求
     */
    protected void requestCropFace(Uri targetImageUri) {
        requestCrop(targetImageUri, R.string.request_crop_portrait, TAG_REQUEST_CROP_FACE);
    }

    /**
     * OCR用画像（名簿）の切り出し要求
     */
    protected void requestCropRoster(Uri targetImageUri) {
        requestCrop(targetImageUri, R.string.request_crop_roster, TAG_REQUEST_CROP_ROSTER);
    }

    /**
     * 切り出し要求（汎用）
     *
     * @param targetImageUri
     * @param messageId
     * @param tag
     */
    protected void requestCrop(@NonNull Uri targetImageUri, @StringRes int messageId, @NonNull String tag) {
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

        // お掃除
        removeFragment(tag);

        // 名簿を切り出した場合
        if (TAG_REQUEST_CROP_ROSTER.equals(tag)) {
            if (croppedImageUri == null) {

                // 失敗
                Toast.makeText(this, R.string.error_crop_image, Toast.LENGTH_SHORT).show();
            } else {

                // 成功
                requestOcr(croppedImageUri);
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
        getFragmentManager().beginTransaction().add(fragment, TAG_REQUEST_OCR).commit();
    }

    /**
     * OCR完了
     *
     * @param roster
     */
    @Override
    public void onFinishOcr(List<String> roster) {
        // nop
    }

    /**
     * 名前入力要求
     */
    protected void requestInputName() {
        new NameInputDialogFragment().show(getFragmentManager(), TAG_REQUEST_INPUT_NAME);
    }

    /**
     * 名前入力完了
     *
     * @param text
     */
    @Override
    public void onFinishInput(String text) {
        // nop
    }
}
