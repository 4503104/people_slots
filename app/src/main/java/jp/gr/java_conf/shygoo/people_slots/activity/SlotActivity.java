package jp.gr.java_conf.shygoo.people_slots.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.gr.java_conf.shygoo.people_slots.R;
import jp.gr.java_conf.shygoo.people_slots.adapter.FaceSlotAdapter;
import jp.gr.java_conf.shygoo.people_slots.adapter.NameSlotAdapter;
import jp.gr.java_conf.shygoo.people_slots.adapter.SlotAdapter;
import jp.gr.java_conf.shygoo.people_slots.view.SlotDrumView;

public class SlotActivity extends BaseActivity {

    private static final String LOG_TAG = SlotActivity.class.getSimpleName();

    // 画面部品
    @Bind(R.id.slot_drum)
    SlotDrumView slotDrum;

    @Bind(R.id.slot_start)
    Button startButton;

    @Bind(R.id.slot_stop)
    Button stopButton;

    // TODO: スロットマシンのデザイン改善

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot);
        ButterKnife.bind(this);
        setAdapter();
    }

    /**
     * ListAdapter設定
     */
    private void setAdapter() {

        // スロットの種別に応じてAdapterを切り替える
        SlotAdapter adapter = null;
        switch (slotType) {
            case SLOT_TYPE_FACE:
                adapter = new FaceSlotAdapter(this, getPresetFaces());
                break;
            case SLOT_TYPE_NAME:
                adapter = new NameSlotAdapter(this, getPresetNames());
                break;
        }
        slotDrum.setDrumAdapter(adapter);
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

        // TODO: 当選者を表示 with ド派手な演出
        // TODO: 当選者を除外して再抽選
    }
}
