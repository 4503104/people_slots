package jp.gr.java_conf.shygoo.people_slots;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 要素が無限に循環する（ように見える）ListAdapter
 */
public class CircularAdapter extends BaseAdapter {

    // 要素のレイアウト
    private int layoutId;

    // 実際の要素（有限個）
    private List<String> items;

    // layout読み込み用
    private LayoutInflater inflater;

    /**
     * コンストラクタ
     *
     * @param context
     * @param layoutId
     * @param items
     */
    public CircularAdapter(Context context, @LayoutRes int layoutId, List<String> items) {
        this.inflater = LayoutInflater.from(context);
        this.layoutId = layoutId;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Viewがあれば再利用
        View view;
        if (convertView == null) {
            view = inflater.inflate(layoutId, parent, false);
        } else {
            view = convertView;
        }

        // 文言だけ差し替え
        TextView itemText = (TextView) view.findViewById(android.R.id.text1);
        itemText.setText((String) getItem(position));

        return view;
    }
}
