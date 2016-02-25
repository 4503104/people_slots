package jp.gr.java_conf.shygoo.people_slots.adapter;

import android.widget.BaseAdapter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

/**
 * 要素の追加・変更・削除ができるAdapter
 * （ArrayAdapterだと全要素の取得メソッドがないので実装）
 */
public abstract class EditableAdapter<T> extends BaseAdapter {

    // 本当は生のListを返すと危ないが、呼び出し側で気をつければ良し
    @Getter
    protected List<T> items;

    /**
     * コンストラクタ
     *
     * @param items 要素
     */
    public EditableAdapter(Collection<? extends T> items) {

        // 編集するのでLinkedListにしとく
        this.items = new LinkedList<>(items);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 要素追加（単独）
     *
     * @param item 追加する要素
     */
    public void addItem(T item) {
        items.add(item);
        notifyDataSetChanged();
    }

    /**
     * 要素追加（複数）
     *
     * @param items 追加する要素
     */
    public void addItems(Collection<? extends T> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * 要素変更
     *
     * @param index 要素の位置
     * @param item 差し替える要素
     */
    public void changeItem(int index, T item) {
        items.set(index, item);
        notifyDataSetChanged();
    }

    /**
     * 要素削除
     *
     * @param index 要素の位置
     */
    public void removeItem(int index) {
        items.remove(index);
        notifyDataSetChanged();
    }
}
