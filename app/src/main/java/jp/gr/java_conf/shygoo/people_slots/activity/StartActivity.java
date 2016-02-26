package jp.gr.java_conf.shygoo.people_slots.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

    // permission取得用
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    // フラグメント呼び出し用
    private static final String TAG_ASK_SLOT_TYPE = "askSlotType";

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
    private void requestStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
    }

    /**
     * 許可要求の結果を受け取る
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
     * 選択結果（スロットの種類）を受け取る
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
     */
    @Override
    public void onDetectFaces(List<Uri> faces) {
        removeFragment(TAG_REQUEST_DETECT_FACES);
        if (faces.isEmpty()) {
            Toast.makeText(this, R.string.error_detect_face, Toast.LENGTH_SHORT).show();
        } else {
            startFacePreview(faces);
        }
    }

    /**
     * 画像（顔）切り出し完了
     */
    @Override
    public void onCropImage(String tag, Uri croppedImageUri) {
        if (TAG_REQUEST_CROP_FACE.equals(tag)) {
            removeFragment(TAG_REQUEST_CROP_FACE);
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
     * 名簿取り込み完了
     */
    @Override
    public void onFinishOcr(List<String> roster) {
        removeFragment(TAG_REQUEST_OCR);
        if (roster.isEmpty()) {
            Toast.makeText(this, R.string.error_ocr, Toast.LENGTH_SHORT).show();
        } else {
            startNamePreview(roster);
        }
    }

    /**
     * 名前入力完了
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
