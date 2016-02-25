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

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.gr.java_conf.shygoo.people_slots.R;

/**
 * （人の）名前を要素として持つSlotAdapter
 */
public class FaceSlotAdapter extends SlotAdapter<Uri> {

    private RequestManager requestManager;
    private LayoutInflater inflater;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param items 要素
     */
    public FaceSlotAdapter(Context context, Collection<? extends Uri> items) {
        super(items);
        this.requestManager = Glide.with(context);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Viewがあれば再利用
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.slot_item_face, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        // 画像だけ差し替え
        requestManager.load(getItem(position)).into(holder.faceImage);

        return view;
    }

    /**
     * 専用ViewHolder
     */
    static class ViewHolder {

        @Bind(R.id.face_image)
        ImageView faceImage;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
