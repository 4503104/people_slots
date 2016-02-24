package jp.gr.java_conf.shygoo.people_slots.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.gr.java_conf.shygoo.people_slots.R;
import jp.gr.java_conf.shygoo.people_slots.adapter.FacePreviewAdapter;
import jp.gr.java_conf.shygoo.people_slots.adapter.NamePreviewAdapter;

/**
 * スロットに表示する要素を確認・編集する画面
 */
public class PreviewActivity extends SlotBaseActivity {

    @Bind(R.id.preview_list)
    ListView previewList;

    private ArrayAdapter adapter;

    // TODO: 削除機能
    // TODO: 差し替え機能
    // TODO: 0件チェック機能
    // TODO: 画面閉じる時に画像データ破棄

    /**
     * 初期処理
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        setAdapter();
    }

    /**
     * ListAdapter設定
     */
    private void setAdapter() {

        // スロットの種別に応じてAdapterを切り替える TODO: クラス分けるべき？
        Intent intent = getIntent();
        int slotType = intent.getIntExtra(EXTRA_SLOT_TYPE, 0);
        switch (slotType) {
            case SLOT_TYPE_FACE:
                List<Uri> faces = intent.getParcelableArrayListExtra(EXTRA_ITEMS);
                adapter = new FacePreviewAdapter(this, faces);
                break;
            case SLOT_TYPE_NAME:
                List<String> names = intent.getStringArrayListExtra(EXTRA_ITEMS);
                adapter = new NamePreviewAdapter(this, names);
                break;
        }
        previewList.setAdapter(adapter);
    }

    /**
     * OKボタンの処理
     */
    @OnClick(R.id.preview_ok)
    public void startSlot() {
        Intent selfIntent = getIntent();
        Intent targetIntent = new Intent(this, SlotActivity.class);
        int slotType = selfIntent.getIntExtra(EXTRA_SLOT_TYPE, 0);
        targetIntent.putExtra(EXTRA_SLOT_TYPE, slotType);
        switch (slotType) {
            case SLOT_TYPE_FACE:
                ArrayList<Uri> faces = selfIntent.getParcelableArrayListExtra(EXTRA_ITEMS);
                targetIntent.putParcelableArrayListExtra(EXTRA_ITEMS, faces);
                break;
            case SLOT_TYPE_NAME:
                ArrayList<String> names = selfIntent.getStringArrayListExtra(EXTRA_ITEMS);
                targetIntent.putStringArrayListExtra(EXTRA_ITEMS, names);
                break;
        }

        //TODO: 最新のitemsを取得

        startActivity(targetIntent);
    }
}
