package jp.gr.java_conf.shygoo.people_slots;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;

import java.util.UUID;

import icepick.State;

/**
 * 光学文字認識による名簿取り込み画面
 */
public class OcrActivity extends BaseActivity {

    // 画像要求
    private static final int REQUEST_CODE_IMAGE = 1;

    // トリミング要求
    private static final int REQUEST_CODE_CROP = 2;

    // 画像データ管理用
    @State
    Uri requestImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        // とりあえず画像を要求
        requestImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            // 画像要求の結果
            case REQUEST_CODE_IMAGE:
                if (resultCode == RESULT_OK) {

                    // トリミング実行
                    if (data == null || data.getData() == null) {
                        requestCrop(requestImageUri);
                    } else {
                        requestCrop(data.getData());
                    }
                } else {
                    getContentResolver().delete(requestImageUri, null, null);
                    requestImageUri = null;
                }
                break;

            // トリミング要求の結果
            case REQUEST_CODE_CROP:
                if (resultCode == RESULT_OK) {
                    //TODO
                }
                break;

            // デフォルト処理
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // 文字認識の対象となる画像の要求
    private void requestImage() {

        // カメラ起動Intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Media.TITLE, "raw" + UUID.randomUUID().toString());
        contentValues.put(Media.MIME_TYPE, "image/*");
        requestImageUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, contentValues);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, requestImageUri);

        // 画像選択Intent
        Intent fileIntent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
        fileIntent.setType("image/*");

        // どちらの方法でも良いので、画像を要求
        Intent chooSerIntent = Intent.createChooser(cameraIntent, getString(R.string.request_roster));
        chooSerIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{fileIntent});
        startActivityForResult(chooSerIntent, REQUEST_CODE_IMAGE);
    }

    // 画像のトリミング要求
    private void requestCrop(Uri imageUri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");

        ContentValues contentValues = new ContentValues();
        contentValues.put(Media.TITLE, "cropped" + UUID.randomUUID().toString());
        contentValues.put(Media.MIME_TYPE, "image/*");
        Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, contentValues);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        startActivityForResult(intent, REQUEST_CODE_CROP);
    }
}
