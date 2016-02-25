package jp.gr.java_conf.shygoo.people_slots.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.Collection;

import jp.gr.java_conf.shygoo.people_slots.R;

/**
 * 名前をPreviewする為のAdapter
 */
public class FacePreviewAdapter extends EditableAdapter<Uri> {

    private LayoutInflater inflater;
    private RequestManager requestManager;

    /**
     * コンストラクタ
     *
     * @param context
     * @param items
     */
    public FacePreviewAdapter(Context context, Collection<? extends Uri> items) {
        super(items);
        inflater = LayoutInflater.from(context);
        requestManager = Glide.with(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Viewがあれば再利用
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.slot_item_face, parent, false);
        } else {
            view = convertView;
        }
        // TODO: ViewHolder使う

        // 画像だけ差し替え
        requestManager.load(getItem(position)).into((ImageView) view.findViewById(R.id.face_image));

        return view;
    }
}
