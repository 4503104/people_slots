package jp.gr.java_conf.shygoo.people_slots.task;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 光学文字認識で画像から文字列を抽出
 */
public class OcrTaskLoader extends AsyncTaskLoader<List<String>> {

    private static final String LOG_TAG = OcrTaskLoader.class.getSimpleName();
    private static String TRAINED_DATA_FILE_DIRNAME = "tessdata";

    // OCR対象の画像
    private Uri imageUri;

    /**
     * コンストラクタ
     *
     * @param context
     * @param imageUri
     */
    public OcrTaskLoader(Context context, Uri imageUri) {
        super(context);
        this.imageUri = imageUri;
    }

    @Override
    public List<String> loadInBackground() {

        // 戻り値
        List<String> result = new ArrayList<>();

        // データ言語を決定
        String dataLang = getTrainedDataLang();

        // データ保存先を決定
        File appDir = getContext().getFilesDir();
        File dataDir = new File(appDir, TRAINED_DATA_FILE_DIRNAME);

        try {
            // データファイルがなければ作成
            prepareTrainedData(dataDir, dataLang);

            // テキスト抽出
            String rawText = readTextFromImage(getBitmap(), appDir, dataLang);

            // 区切り文字で分割して格納
            result.addAll(splitText(rawText));

        } catch (IOException e) {
            Log.e(LOG_TAG, "ocr failed.", e);
        }

        // 分割済み文字列を返す
        return result;
    }

    // OCR言語取得
    private String getTrainedDataLang() {

        // TODO: ユーザが読み取り言語を選べるようにする
        return "eng";
    }

    // OCRデータを準備
    private void prepareTrainedData(File dataDir, String dataLang) throws IOException {

        // ファイルがあれば何もしない
        String filename = dataLang + ".traineddata";
        File dstFile = new File(dataDir, filename);
        if (dstFile.exists()) {
            return;
        }

        // なければassetsからコピー
        InputStream in = null;
        OutputStream out = null;
        try {
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            String srcFilename = TRAINED_DATA_FILE_DIRNAME + File.separator + filename;
            in = getContext().getAssets().open(srcFilename);
            out = new FileOutputStream(dstFile);
            byte[] buffer = new byte[1024];
            int readSize;
            while ((readSize = in.read(buffer)) >= 0) {
                out.write(buffer, 0, readSize);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    // 画像URIからBitmapを取得
    private Bitmap getBitmap() throws IOException {

        // 生データではなく、グレイスケール化したコピーを返す
        Bitmap rawBmp = Media.getBitmap(getContext().getContentResolver(), imageUri);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);// 彩度をゼロに
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        Paint paint = new Paint();
        paint.setColorFilter(filter);
        int width = rawBmp.getWidth();
        int height = rawBmp.getHeight();
        Bitmap grayBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayBmp);
        canvas.drawBitmap(rawBmp, 0, 0, paint);
        rawBmp.recycle();
        return grayBmp;
    }

    // 画像からテキストを抽出
    private String readTextFromImage(Bitmap bitmap, File appDir, String dataLang) {

        // OCRの準備
        TessBaseAPI api = new TessBaseAPI();
        api.init(appDir.getAbsolutePath(), dataLang);

        // 画像からテキストを抽出
        api.setImage(bitmap);
        String text = api.getUTF8Text();
        Log.d(LOG_TAG, "text: " + text);

        // 後始末
        api.end();
        bitmap.recycle();
        return text;
    }

    // 文字列を分割
    private List<String> splitText(String rawText) {

        // TODO: ユーザが区切り文字を選べるようにする

        // とりあえず改行位置で分割
        List<String> tokens = filterEmpty(rawText.split("\\r?\\n"));

        // 駄目なら空白文字で分割
        if (tokens.size() <= 1) {
            tokens = filterEmpty(rawText.split("[\\s　]+"));
        }
        return tokens;
    }

    // 空要素を排除
    private List<String> filterEmpty(String[] arr) {
        List<String> list = new ArrayList<>();
        for (String str : arr) {
            str = str.trim();
            if (StringUtils.isNotEmpty(str)) {
                list.add(str);
            }
        }
        return list;
    }
}
