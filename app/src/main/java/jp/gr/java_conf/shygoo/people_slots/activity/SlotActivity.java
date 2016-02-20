package jp.gr.java_conf.shygoo.people_slots.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.gr.java_conf.shygoo.people_slots.R;
import jp.gr.java_conf.shygoo.people_slots.adapter.NameAdapter;
import jp.gr.java_conf.shygoo.people_slots.view.SlotDrumView;

public class SlotActivity extends BaseActivity {

    private static final String LOG_TAG = SlotActivity.class.getSimpleName();

    // スロットの種類
    public static final int SLOT_TYPE_NAME = 1;
    public static final String EXTRA_SLOT_TYPE = "itemType";

    // スロットに表示するデータ
    public static final String EXTRA_ITEMS = "items";

    // 画面部品
    @Bind(R.id.slot_drum)
    SlotDrumView slotDrum;

    @Bind(R.id.slot_start)
    Button startButton;

    @Bind(R.id.slot_stop)
    Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot);
        ButterKnife.bind(this);

        // 種別に応じてスロットを初期化
        Intent intent = getIntent();
        switch (intent.getIntExtra(EXTRA_SLOT_TYPE, SLOT_TYPE_NAME)) {
            case SLOT_TYPE_NAME:
                List<String> items = getIntent().getStringArrayListExtra(EXTRA_ITEMS);
                Log.d(LOG_TAG, "items: {" + StringUtils.join(items, "|") + "}");
                slotDrum.setDrumAdapter(new NameAdapter(this, items));
                break;
        }
    }

    /**
     * 開始ボタンの処理
     */
    @OnClick(R.id.slot_start)
    public void onClickStart() {

        // 回転開始
        slotDrum.startSpin();

        // 停止ボタンを表示
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
    }

    /**
     * 停止ボタンの処理
     */
    @OnClick(R.id.slot_stop)
    public void onClickStop() {

        // 回転終了
        slotDrum.stopSpin();

        // 開始ボタンを表示
        stopButton.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);

        // TODO: 当選者を表示
        // TODO: 当選者を除外して再抽選
    }
}
