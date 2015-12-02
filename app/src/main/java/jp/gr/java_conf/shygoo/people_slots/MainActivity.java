package jp.gr.java_conf.shygoo.people_slots;

import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * メイン画面
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    /**
     * 名簿取り込み選択時の処理
     */
    @OnClick(R.id.ocr)
    public void startOcr() {
        Intent intent = new Intent(this, OcrActivity.class);
        startActivity(intent);
    }
}
