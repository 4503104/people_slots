package jp.gr.java_conf.shygoo.people_slots.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jp.gr.java_conf.shygoo.people_slots.R;

/**
 * （人の）名前を要素として持つSlotAdapter
 */
public class NameSlotAdapter extends SlotAdapter<String> {

    // layout読み込み用
    private LayoutInflater inflater;

    /**
     * コンストラクタ
     *
     * @param context
     * @param items
     */
    public NameSlotAdapter(Context context, List<String> items) {
        super(items);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Viewがあれば再利用
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.slot_item_name, parent, false);
        } else {
            view = convertView;
        }

        // 文言だけ差し替え
        TextView itemText = (TextView) view.findViewById(android.R.id.text1);
        itemText.setText((String) getItem(position));

        return view;
    }
}
