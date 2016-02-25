package jp.gr.java_conf.shygoo.people_slots.view;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import jp.gr.java_conf.shygoo.people_slots.R;
import jp.gr.java_conf.shygoo.people_slots.adapter.SlotAdapter;

/**
 * スロットマシンの回転する部分
 */
public class SlotDrumView extends FrameLayout {

    // TODO: Fragment化した方が良いかもしれない…

    // ドラム本体
    @Bind(R.id.drum_main)
    ListView drumMain;

    // 回転速度
    private static final int SCROLL_INTERVAL_MSEC = 96;// TODO: 速度調整

    // 専用Adapter
    private SlotAdapter drumAdapter;

    // 位置調整用
    @State
    int itemStopPixel;

    // スロットの状態
    @State
    boolean spinning;
    @State
    int position;

    /**
     * 引数1つのコンストラクタ
     */
    public SlotDrumView(Context context) {
        this(context, null);
    }

    /**
     * 引数2つのコンストラクタ
     */
    public SlotDrumView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 引数3つのコンストラクタ
     */
    public SlotDrumView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 定義済みのレイアウトを設定
        LayoutInflater.from(context).inflate(R.layout.slot_drum, this);
        ButterKnife.bind(this);

        // 回転フラグOFF
        spinning = false;

        // 図柄の停止位置を取得しておく
        itemStopPixel = getResources().getDimensionPixelSize(R.dimen.slot_drum_margin);

        // 自動回転させるので、タッチイベントを無効化
        drumMain.setEnabled(false);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    /**
     * Adapter設定
     *
     * @param drumAdapter スロット専用Adapter
     */
    public void setDrumAdapter(SlotAdapter drumAdapter) {

        // Adapterを設定
        this.drumAdapter = drumAdapter;
        drumMain.setAdapter(drumAdapter);

        // ドラム位置を初期化
        post(new Runnable() {
            @Override
            public void run() {
                resetPosition();
            }
        });
    }

    // ドラムの位置を初期化
    private void resetPosition() {

        // 逆向きにスクロールするので末尾付近の要素から開始
        position = drumMain.getCount() - drumAdapter.getRealCount() - 1;

        // 該当要素をドラムの中央に表示
        drumMain.setSelectionFromTop(position, itemStopPixel);
    }

    /**
     * ドラムを1要素分スクロール
     */
    public void scrollNext() {

        // ドラムの先頭に達しそうな場合（達してしまうと上に表示する要素がなくなるのでその手前）
        if (position == 1) {

            // 回転を続ける為、末尾付近の同一要素へワープ（よほど長時間放置しない限りあり得ないが念の為）
            position = drumMain.getCount() - drumAdapter.getRealCount() + 1;
            drumMain.setSelectionFromTop(position, itemStopPixel);
        }

        // １つ上の要素をドラム中央までスクロール
        position--;
        drumMain.smoothScrollToPositionFromTop(position, itemStopPixel);
    }

    /**
     * ドラムを回す
     */
    public void startSpin() {

        // 回転フラグON
        spinning = true;

        // フラグがOFFになるまで定期的にスクロールし続ける
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (spinning) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            scrollNext();
                        }
                    });
                } else {
                    this.cancel();// TODO: タイマーを確実に止める
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, SCROLL_INTERVAL_MSEC);
    }

    /**
     * ドラムを止める
     */
    public void stopSpin() {

        // 回転フラグOFF
        spinning = false;

        // TODO: 抽選結果を返す
        // TODO: 徐々に回転速度を落とす
        // TODO: 停止タイミングに若干の揺らぎを持たせる（目押し防止）
    }
}
