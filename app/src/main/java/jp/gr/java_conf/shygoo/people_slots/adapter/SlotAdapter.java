package jp.gr.java_conf.shygoo.people_slots.adapter;

import java.util.Collections;
import java.util.List;

/**
 * スロットマシン専用のCircularAdapter
 */
public abstract class SlotAdapter<T> extends CircularAdapter<T> {

    /**
     * コンストラクタ
     *
     * @param items
     */
    public SlotAdapter(List<T> items) {
        super(items);

        // スロットは通常のListViewとは上下逆向きに回転するので、Listを反転
        Collections.reverse(this.items);
    }
}
