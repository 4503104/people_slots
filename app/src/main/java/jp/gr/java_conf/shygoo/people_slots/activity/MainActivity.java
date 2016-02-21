package jp.gr.java_conf.shygoo.people_slots.activity;

import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.gr.java_conf.shygoo.people_slots.R;

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
     * 「名前」選択時の処理
     */
    @OnClick(R.id.type_name)
    public void startOcr() {
        Intent intent = new Intent(this, OcrActivity.class);
        startActivity(intent);
    }
}
