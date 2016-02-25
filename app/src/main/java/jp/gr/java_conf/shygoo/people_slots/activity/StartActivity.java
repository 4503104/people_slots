package jp.gr.java_conf.shygoo.people_slots.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.gr.java_conf.shygoo.people_slots.R;
import jp.gr.java_conf.shygoo.people_slots.fragment.dialog.ChoiceDialogFragment;

/**
 * 初期画面
 */
public class StartActivity extends BaseActivity {

    // 権限取得用
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    // フラグメント呼び出し用
    private static final String TAG_ASK_SLOT_TYPE = "askSlotType";

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
            default:
                super.onSelectItem(itemId);
        }
    }

    /**
     * 顔検出完了
     *
     * @param faces
     */
    @Override
    public void onDetectFaces(List<Uri> faces) {
        if (faces.isEmpty()) {
            Toast.makeText(this, R.string.error_detect_face, Toast.LENGTH_SHORT).show();
        } else {
            startFacePreview(faces);
        }
    }

    /**
     * 画像切り出し完了
     *
     * @param tag
     * @param croppedImageUri
     */
    @Override
    public void onCropImage(String tag, Uri croppedImageUri) {
        if (TAG_REQUEST_CROP_FACE.equals(tag)) {
            if (croppedImageUri == null) {
                Toast.makeText(this, R.string.error_crop_image, Toast.LENGTH_SHORT).show();
            } else {
                startFacePreview(Collections.singletonList(croppedImageUri));
            }
        } else {
            super.onCropImage(tag, croppedImageUri);
        }
    }

    /**
     * 顔プレビュー開始
     */
    private void startFacePreview(List<Uri> faces) {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra(PreviewActivity.EXTRA_SLOT_TYPE, PreviewActivity.SLOT_TYPE_FACE);
        intent.putParcelableArrayListExtra(PreviewActivity.EXTRA_SLOT_ITEMS, new ArrayList<>(faces));
        startActivity(intent);
    }

    /**
     * OCR完了
     *
     * @param roster
     */
    @Override
    public void onFinishOcr(List<String> roster) {
        if (roster.isEmpty()) {
            Toast.makeText(this, R.string.error_ocr, Toast.LENGTH_SHORT).show();
        } else {
            startNamePreview(roster);
        }
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
        intent.putStringArrayListExtra(PreviewActivity.EXTRA_SLOT_ITEMS, new ArrayList<>(names));
        startActivity(intent);
    }
}
