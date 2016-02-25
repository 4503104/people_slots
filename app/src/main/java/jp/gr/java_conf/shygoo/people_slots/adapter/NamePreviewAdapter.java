package jp.gr.java_conf.shygoo.people_slots.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collection;

import jp.gr.java_conf.shygoo.people_slots.R;

/**
 * 名前をPreviewする為のAdapter
 */
public class NamePreviewAdapter extends EditableAdapter<String> {

    private LayoutInflater inflater;

    /**
     * コンストラクタ
     *
     * @param context
     */
    public NamePreviewAdapter(Context context, Collection<? extends String> items) {
        super(items);
        inflater = LayoutInflater.from(context);
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
        // TODO: ViewHolder使う

        // 文言だけ差し替え
        ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position));

        return view;
    }
}
