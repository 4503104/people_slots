package jp.gr.java_conf.shygoo.people_slots.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.gr.java_conf.shygoo.people_slots.R;

/**
 * 名前をPreviewする為のEditableAdapter
 */
public class NamePreviewAdapter extends EditableAdapter<String> {

    private LayoutInflater inflater;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    public NamePreviewAdapter(Context context, Collection<? extends String> items) {
        super(items);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Viewがあれば再利用
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.slot_item_name, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        // 文言だけ差し替え
        holder.text1.setText(getItem(position));

        return view;
    }

    /**
     * 専用ViewHolder
     */
    static class ViewHolder {

        @Bind(android.R.id.text1)
        TextView text1;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
