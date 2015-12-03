package jp.gr.java_conf.shygoo.people_slots;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.State;

/**
 * 光学文字認識による名簿取り込み画面
 */
public class OcrActivity extends BaseActivity {

    private static final String LOG_TAG = OcrActivity.class.getSimpleName();

    // 画像要求
    private static final int REQUEST_IMAGE = 1;

    // トリミング要求
    private static final int REQUEST_CROP = 2;

    // OCR要求
    private static final int REQUEST_OCR = 3;

    // 画像データ管理用
    @State
    Uri photoImageUri;
    @State
    Uri croppedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        ButterKnife.bind(this);

        // とりあえず画像を要求
        requestImage();
    }

    /**
     * 文字認識の対象となる画像の要求
     */
    @OnClick(R.id.retry)
    public void requestImage() {

        // カメラ起動Intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Media.TITLE, "raw" + UUID.randomUUID().toString());
        contentValues.put(Media.MIME_TYPE, "image/*");
        photoImageUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, contentValues);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoImageUri);

        // 画像選択Intent
        Intent fileIntent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
        fileIntent.setType("image/*");

        // どちらの方法でも良いので、画像を要求
        Intent chooSerIntent = Intent.createChooser(cameraIntent, getString(R.string.request_roster));
        chooSerIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{fileIntent});
        startActivityForResult(chooSerIntent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            // 画像要求の結果
            case REQUEST_IMAGE:
                if (resultCode == RESULT_OK) {

                    // トリミング実行
                    if (data == null || data.getData() == null) {
                        requestCrop(photoImageUri);
                    } else {
                        requestCrop(data.getData());
                    }
                } else {
                    deletePhotoImage();
                    Log.w(LOG_TAG, "get image failed.");
                }
                break;

            // トリミング要求の結果
            case REQUEST_CROP:
                deletePhotoImage();
                if (resultCode == RESULT_OK) {

                    // OCR実行
                    requestOcr();
                } else {
                    deleteCroppedImage();
                    Log.w(LOG_TAG, "crop image failed.");
                }
                break;

            // デフォルト処理
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // 画像削除
    private void deleteImage(Uri uri) {
        if (uri != null) {
            getContentResolver().delete(uri, null, null);
        }
    }

    // 写真画像を削除
    private void deletePhotoImage() {
        deleteImage(photoImageUri);
        photoImageUri = null;
    }

    // トリミング済み画像を削除
    private void deleteCroppedImage() {
        deleteImage(croppedImageUri);
        croppedImageUri = null;
    }

    // 画像のトリミング要求
    private void requestCrop(Uri imageUri) {

        // TODO: 使い勝手の良いCROPライブラリの導入
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");

        ContentValues contentValues = new ContentValues();
        contentValues.put(Media.TITLE, "cropped" + UUID.randomUUID().toString());
        contentValues.put(Media.MIME_TYPE, "image/*");
        croppedImageUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, contentValues);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri);

        startActivityForResult(intent, REQUEST_CROP);
    }

    // OCR要求
    private void requestOcr() {

        // 引数を準備
        final String ARGS_IMAGE_URI = "croppedImageUri";
        Bundle args = new Bundle();
        args.putParcelable(ARGS_IMAGE_URI, croppedImageUri);

        // バックグラウンドタスクを実行
        getLoaderManager().initLoader(REQUEST_OCR, args, new LoaderManager.LoaderCallbacks<List<String>>() {
            // TODO: 待ってる間の表示
            // TODO: タスク実行中の操作を禁止

            @Override
            public Loader<List<String>> onCreateLoader(int id, Bundle args) {
                Uri imageUri = args.getParcelable(ARGS_IMAGE_URI);
                return new OcrTaskLoader(OcrActivity.this, imageUri);
            }

            @Override
            public void onLoadFinished(Loader<List<String>> loader, List<String> data) {

                // 読み取り済み画像を削除
                deleteCroppedImage();

                // 有効なデータを取得できた場合
                if (data.size() > 0) {

                    // スロットを表示
                    showSlot(data);
                } else {
                    Log.w(LOG_TAG, "ocr failed.");
                }

                // TODO: ミススペルの校正機能

                // TODO: 追加読み込み機能

            }

            @Override
            public void onLoaderReset(Loader<List<String>> loader) {
                Log.i(LOG_TAG, "ocr canceld.");
            }
        }).forceLoad();
    }

    // スロット画面へ移動
    private void showSlot(List<String> items) {

        // 読み取ったデータを渡して起動
        Intent intent = new Intent(this, SlotActivity.class);
        intent.putStringArrayListExtra(SlotActivity.EXTRA_ITEMS, new ArrayList<>(items));
        startActivity(intent);
    }
}
