package jp.gr.java_conf.shygoo.people_slots;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SlotActivity extends AppCompatActivity {

    // 画面部品
    @Bind(R.id.slot_drum)
    ListView slotDrum;

    @Bind(R.id.slot_start)
    Button startButton;

    @Bind(R.id.slot_stop)
    Button stopButton;

    // スロットの状態
    private boolean spinning;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot);
        ButterKnife.bind(this);

        // スロット初期化
        initSlots();
    }

    // 回す
    @OnClick(R.id.slot_start)
    public void startSpin() {
        spinning = true;
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (spinning) {
                    scrollDrum();
                } else {
                    // TODO: スクロール位置調整＆当選item取得
                    this.cancel();
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 50);
    }

    // 止める
    @OnClick(R.id.slot_stop)
    public void stopSpin() {
        spinning = false;
        stopButton.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
    }

    // スロット初期化
    private void initSlots() {

        // 自動回転させるので、タッチイベントを無効化
        slotDrum.setEnabled(false);

        // 抽選対象をセット
        Collection<String> items = loadItems();
        ListAdapter adapter = new CircularAdapter(this, items);
        slotDrum.setAdapter(adapter);

        // ドラム位置を初期化
        initPosition();

        // 回転OFF
        spinning = false;
    }

    // アイテム読み取り
    private Collection<String> loadItems() {

        // TODO: ユーザが設定したアイテムを読み込む
        Set<String> dummyItems = new LinkedHashSet<>();
        dummyItems.add("太郎");
        dummyItems.add("二郎");
        dummyItems.add("三郎");
        dummyItems.add("士郎");
        dummyItems.add("花子");
        dummyItems.add("鳥子");
        dummyItems.add("風子");
        dummyItems.add("ゆう子");
        return dummyItems;
    }

    // ドラムの位置を初期化
    private void initPosition() {

        // 逆向きにスクロールするので、「もっとも末尾に近い先頭の要素」を選択
        position = slotDrum.getCount() - ((CircularAdapter) slotDrum.getAdapter()).getRealCount();
        slotDrum.setSelection(position);
    }

    // ドラムを1要素分スクロール
    private void scrollDrum() {

        // ドラムの先頭に達したら末尾に戻す
        if (position == 0) {
            initPosition();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                position--;
                slotDrum.smoothScrollToPosition(position);
            }
        });
    }

    /**
     * 要素が無限に循環する（ように見える）Adapter
     */
    public static class CircularAdapter extends BaseAdapter {

        // 実際の要素（有限個）
        private List<String> items;

        // layout読み取り用
        private LayoutInflater inflater;

        /**
         * コンストラクタ
         *
         * @param context
         * @param items
         */
        public CircularAdapter(Context context, Collection<String> items) {
            this.items = new ArrayList<>(items);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {

            // ほぼ無限個の要素を扱う（ただし端数は出ないよう調整）
            return Integer.MAX_VALUE - (Integer.MAX_VALUE % items.size());
        }

        /**
         * 実サイズ取得
         *
         * @return （繰り返しを含まない）実際の要素数
         */
        public int getRealCount() {

            // ほぼ無限個の要素を扱う（ただし端数は出ないよう調整）
            return items.size();
        }

        @Override
        public Object getItem(int position) {

            // List内の要素を繰り返し利用する
            return items.get(position % items.size());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Viewがあれば再利用
            View view;
            if (convertView == null) {
                view = inflater.inflate(R.layout.item_slot, parent, false);
            } else {
                view = convertView;
            }

            // 文言だけ差し替え
            TextView itemText = (TextView) view.findViewById(R.id.item_text);
            itemText.setText((String) getItem(position));

            return view;
        }
    }
}
