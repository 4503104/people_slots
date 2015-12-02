package jp.gr.java_conf.shygoo.people_slots;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SlotActivity extends BaseActivity {

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

        // スロット初期化
        slotDrum.initialize(loadItems());
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
    }

    // アイテム読み取り
    private List<String> loadItems() {

        // TODO: ユーザが設定したアイテムを読み込む
        List<String> dummyItems = new ArrayList<>();
        dummyItems.add("一郎");
        dummyItems.add("二郎");
        dummyItems.add("三郎");
        dummyItems.add("士郎");
        dummyItems.add("ゆう子");
        dummyItems.add("雪子");
        dummyItems.add("月子");
        dummyItems.add("花子");
        return dummyItems;
    }
}
