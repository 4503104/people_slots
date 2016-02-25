package jp.gr.java_conf.shygoo.people_slots.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.State;
import jp.gr.java_conf.shygoo.people_slots.R;
import jp.gr.java_conf.shygoo.people_slots.adapter.EditableAdapter;
import jp.gr.java_conf.shygoo.people_slots.adapter.FacePreviewAdapter;
import jp.gr.java_conf.shygoo.people_slots.adapter.NamePreviewAdapter;
import jp.gr.java_conf.shygoo.people_slots.swipedismiss.SwipeDismissListViewTouchListener;

/**
 * スロットに表示する要素を確認・編集する画面
 */
public class PreviewActivity extends BaseActivity {

    @Bind(R.id.preview_ok)
    Button okButton;

    @Bind(R.id.preview_list)
    ListView listView;

    @State
    ArrayList savedNames;

    @State
    ArrayList savedFaces;

    private FacePreviewAdapter faceAdapter;
    private NamePreviewAdapter nameAdapter;
    private EditableAdapter adapterAlias;

    // TODO: 差し替え機能
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
        setListeners();
    }

    /**
     * ListAdapter設定
     */
    @SuppressWarnings("unchecked")
    private void setAdapter() {

        // スロットの種別に応じてAdapterを切り替える
        switch (slotType) {
            case SLOT_TYPE_FACE:
                List<Uri> faces;
                if (savedFaces == null) {
                    faces = getPresetFaces();
                } else {
                    faces = savedFaces;
                }
                faceAdapter = new FacePreviewAdapter(this, faces);
                adapterAlias = faceAdapter;
                break;
            case SLOT_TYPE_NAME:
                List<String> names;
                if (savedNames == null) {
                    names = getPresetNames();
                } else {
                    names = savedNames;
                }
                nameAdapter = new NamePreviewAdapter(this, names);
                adapterAlias = nameAdapter;
                break;
        }
        listView.setAdapter(adapterAlias);
    }

    /**
     * 各種リスナー設定
     */
    private void setListeners() {

        // タップしたら差し替えるやつ
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO
            }
        });

        // スワイプしたら消すやつ
        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
                listView,
                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            adapterAlias.removeItem(position);
                        }
                        if (adapterAlias.getCount() == 0) {
                            okButton.setEnabled(false);
                        }
                    }
                }
        );
        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener(touchListener.makeScrollListener());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        setCurrentItems();
        super.onSaveInstanceState(outState);
    }

    /**
     * Adapterから最新のアイテムを取得して保持
     * TODO: ListView自身に覚えさせとくべきかも？
     */
    private void setCurrentItems() {
        switch (slotType) {
            case SLOT_TYPE_FACE:
                savedFaces = getCurrentFaces();
                break;
            case SLOT_TYPE_NAME:
                savedNames = getCurrentNames();
                break;
        }
    }

    /**
     * 最新の顔情報を取得
     *
     * @return 顔情報
     */
    private ArrayList<Uri> getCurrentFaces() {
        return new ArrayList<>(faceAdapter.getItems());
    }

    /**
     * 最新の名前情報を取得
     *
     * @return 名前情報
     */
    private ArrayList<String> getCurrentNames() {
        return new ArrayList<>(nameAdapter.getItems());
    }

    /**
     * アイテム追加開始
     */
    @OnClick(R.id.preview_add_item)
    public void askAddMethod() {

        // 手段を訊く
        switch (slotType) {
            case SLOT_TYPE_FACE:
                askCaptureMethod();
                break;
            case SLOT_TYPE_NAME:
                askInputMethod();
                break;
        }
    }

    /**
     * アイテム差し替え開始
     */
    private void askChangeMethod(int position) {

        // 手段は固定
        switch (slotType) {
            case SLOT_TYPE_FACE:
                requestPortraitPhoto();
                break;
            case SLOT_TYPE_NAME:
                requestInputName();
                break;
        }
    }

    /**
     * 顔検出完了
     *
     * @param faces 検出された顔
     */
    @Override
    public void onDetectFaces(List<Uri> faces) {
        if (faces.isEmpty()) {
            Toast.makeText(this, R.string.error_detect_face, Toast.LENGTH_SHORT).show();
        } else {
            //TODO
            faceAdapter.addItems(faces);
            okButton.setEnabled(true);
        }
    }

    /**
     * 画像切り出し完了
     *
     * @param tag             Fragmentの識別子
     * @param croppedImageUri 切り出した画像
     */
    @Override
    public void onCropImage(String tag, Uri croppedImageUri) {
        if (TAG_REQUEST_CROP_FACE.equals(tag)) {
            if (croppedImageUri == null) {
                Toast.makeText(this, R.string.error_crop_image, Toast.LENGTH_SHORT).show();
            } else {
                //TODO
                faceAdapter.addItem(croppedImageUri);
                okButton.setEnabled(true);
            }
        } else {
            super.onCropImage(tag, croppedImageUri);
        }
    }

    /**
     * OCR完了
     *
     * @param roster 名簿
     */
    @Override
    public void onFinishOcr(List<String> roster) {
        if (roster.isEmpty()) {
            Toast.makeText(this, R.string.error_ocr, Toast.LENGTH_SHORT).show();
        } else {
            //TODO
            nameAdapter.addItems(roster);
            okButton.setEnabled(true);
        }
    }

    /**
     * 名前入力完了
     *
     * @param name 名前
     */
    @Override
    public void onFinishInput(String name) {
        //TODO
        nameAdapter.addItem(name);
        okButton.setEnabled(true);
    }

    /**
     * スロット開始
     */
    @OnClick(R.id.preview_ok)
    public void startSlot() {

        // スロット種別と編集済みアイテム情報を渡す
        Intent intent = new Intent(this, SlotActivity.class);
        intent.putExtra(EXTRA_SLOT_TYPE, slotType);
        switch (slotType) {
            case SLOT_TYPE_FACE:
                intent.putParcelableArrayListExtra(EXTRA_SLOT_ITEMS, getCurrentFaces());
                break;
            case SLOT_TYPE_NAME:
                intent.putStringArrayListExtra(EXTRA_SLOT_ITEMS, getCurrentNames());
                break;
        }
        startActivity(intent);
    }
}
