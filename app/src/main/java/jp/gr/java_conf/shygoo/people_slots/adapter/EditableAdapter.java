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
     * @param items
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

    public void addItem(T item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void addItems(Collection<T> items) {
        items.addAll(items);
        notifyDataSetChanged();
    }

    public void changeItem(int index, T item) {
        items.set(index, item);
        notifyDataSetChanged();
    }

    public void removeItem(int index) {
        items.remove(index);
        notifyDataSetChanged();
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }
}
