package jp.gr.java_conf.shygoo.people_slots.adapter;

import java.util.Collection;
import java.util.Collections;

/**
 * スロットマシン専用のCircularAdapter
 */
public abstract class SlotAdapter<T> extends CircularAdapter<T> {

    /**
     * コンストラクタ
     *
     * @param items
     */
    public SlotAdapter(Collection<? extends T> items) {
        super(items);

        // スロットは通常のListViewとは上下逆向きに回転するので、Listを反転
        Collections.reverse(this.items);
    }
}
