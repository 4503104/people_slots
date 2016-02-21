package jp.gr.java_conf.shygoo.people_slots.adapter;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 要素が無限に循環する（ように見える）ListAdapter
 */
public abstract class CircularAdapter<T> extends BaseAdapter {

    // 実際の要素（有限個）
    protected List<T> items;

    /**
     * コンストラクタ
     *
     * @param items
     */
    public CircularAdapter(List<T> items) {
        this.items = new ArrayList<>(items);
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
}
