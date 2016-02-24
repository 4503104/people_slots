package jp.gr.java_conf.shygoo.people_slots.task;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 画像に顔検出をかけて顔部分を切り出し
 */
public class FaceDetectTaskLoader extends AsyncTaskLoader<List<Uri>> {

    private static final String LOG_TAG = FaceDetectTaskLoader.class.getSimpleName();

    // 顔検出対象の画像
    private Uri targetImageUri;

    /**
     * コンストラクタ
     *
     * @param context
     * @param targetImageUri
     */
    public FaceDetectTaskLoader(Context context, Uri targetImageUri) {
        super(context);
        this.targetImageUri = targetImageUri;
    }

    @Override
    public List<Uri> loadInBackground() {
        List<Uri> result = new ArrayList<>();
        FaceDetector detector = null;
        Bitmap targetBitmap = null;
        try {

            // 顔検出準備
            detector = new FaceDetector.Builder(getContext())
                    .setTrackingEnabled(false)
                    .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                    .setMode(FaceDetector.ACCURATE_MODE)
                    .build();

            // 初回はダウンロードが走るので準備できてない場合があるらしい
            if (!detector.isOperational()) {
                throw new IllegalStateException("Detector is not operational.");
            }

            // ファイル保存準備 TODO: 保存場所ここでいいの？
            File outputDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            CharSequence date = DateFormat.format("yyyyMMdd_kkmmss", Calendar.getInstance());

            // 検出対象の画像を読み取り
            targetBitmap = Media.getBitmap(getContext().getContentResolver(), targetImageUri);

            // 顔検出実行
            Frame frame = new Frame.Builder().setBitmap(targetBitmap).build();
            SparseArray<Face> faces = detector.detect(frame);

            // 顔部分を切り出して保存
            for (int i = 0, size = faces.size(); i < size; i++) {
                Face face = faces.valueAt(i);
                Bitmap faceBmp = cropFace(targetBitmap, face);
                String filename = String.format("face_%s_%d.png", date, i);
                File outputFile = new File(outputDir, filename);
                Uri savedFileUri = saveFace(faceBmp, outputFile);
                result.add(savedFileUri);
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "Failed to crop faces..", e);

            // 保存済みのファイルは消せたら消しとく
            for (Uri uri : result) {
                new File(uri.getPath()).deleteOnExit();
            }
            result.clear();
        } finally {
            if (detector != null) {
                detector.release();
            }
            if (targetBitmap != null) {
                targetBitmap.recycle();
            }
        }

        // 保存した顔画像のURIのList（もしくは空）を返す
        return result;
    }

    /**
     * 顔部分の切り出し
     *
     * @param srcBmp 元画像
     * @param face   検出された顔情報
     * @return 切り出した顔
     */
    private Bitmap cropFace(Bitmap srcBmp, Face face) {
        PointF facePosition = face.getPosition();
        int x = (int) facePosition.x;
        int y = (int) facePosition.y;
        int width = (int) face.getWidth();
        int height = (int) face.getHeight();
        return Bitmap.createBitmap(srcBmp, x, y, width, height, null, false);
    }

    /**
     * 顔画像の保存
     *
     * @param faceBmp
     * @param outputFile
     * @return 保存したファイルのUri
     * @throws FileNotFoundException
     */
    private Uri saveFace(Bitmap faceBmp, File outputFile) throws FileNotFoundException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
            faceBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } finally {
            if (faceBmp != null) {
                faceBmp.recycle();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return Uri.fromFile(outputFile);
    }
}
