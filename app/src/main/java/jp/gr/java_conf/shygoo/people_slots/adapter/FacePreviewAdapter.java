package jp.gr.java_conf.shygoo.people_slots.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Collection;

import jp.gr.java_conf.shygoo.people_slots.R;

/**
 * 名前をPreviewする為のAdapter
 */
public class FacePreviewAdapter extends ArrayAdapter<Uri> {

    /**
     * コンストラクタ
     *
     * @param context
     * @param items
     */
    public FacePreviewAdapter(Context context, Collection<Uri> items) {
        super(context, 0);
        addAll(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Viewがあれば再利用
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.slot_item_face, parent, false);
        } else {
            view = convertView;
        }
        // TODO: ViewHolder使う

        // 画像だけ差し替え
        ImageView imageView = (ImageView) view.findViewById(R.id.face_image);
        Glide.with(getContext()).load(getItem(position)).into(imageView);

        return view;
    }
}
