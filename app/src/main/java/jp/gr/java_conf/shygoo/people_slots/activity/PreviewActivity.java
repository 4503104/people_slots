package jp.gr.java_conf.shygoo.people_slots.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import jp.gr.java_conf.shygoo.people_slots.fragment.dialog.NameInputDialogFragment;
import jp.gr.java_conf.shygoo.people_slots.swipedismiss.SwipeDismissListViewTouchListener;

/**
 * スロットに表示する要素を確認・編集する画面
 */
public class PreviewActivity extends BaseActivity {

    // TODO: スロットから戻ってくるとデータがリセットされる不具合に対応
    // TODO: 要素追加時のスクロール制御
    // TODO: 画面閉じる時に画像データ破棄

    // フラグメント呼び出し用
    private static final String TAG_REQUEST_EDIT_NAME = "requestEditName";

    /**
     * アイテム位置：なし
     */
    private static final int ITEM_POSITION_NOTHING = -1;

    @State
    int changingItemPosition = ITEM_POSITION_NOTHING;

    @State
    ArrayList<Uri> savedFaces;

    @State
    ArrayList<String> savedNames;

    @Bind(R.id.finish_edit)
    Button finishEditButton;

    @Bind(R.id.preview_list)
    ListView listView;

    // Adapterはどちらか一方のみ使う
    private FacePreviewAdapter faceAdapter;
    private NamePreviewAdapter nameAdapter;

    // でもそれだと使いにくいので、ショートカット用の変数も用意
    private EditableAdapter adapterAlias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        initListView();
    }

    /**
     * 初期設定
     */
    private void initListView() {
        setFooter();
        setAdapter();
        setListeners();
    }

    /**
     * フッタ設定
     */
    private void setFooter() {
        View footer = LayoutInflater.from(this).inflate(R.layout.slot_item_footer, listView, false);
        listView.addFooterView(footer);
    }

    /**
     * ListAdapter設定
     */
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
                startChange(position);
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
                            finishEditButton.setEnabled(false);
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
    public void startAdd() {

        // 対象アイテムなし
        changingItemPosition = ITEM_POSITION_NOTHING;

        // アイテム追加の手段を訊く
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
     *
     * @param position アイテムの位置
     */
    private void startChange(int position) {

        // 対象アイテムを覚えておく
        changingItemPosition = position;

        // アイテム差し替えの手段は固定
        switch (slotType) {
            case SLOT_TYPE_FACE:
                requestPortraitPhoto();
                break;
            case SLOT_TYPE_NAME:
                requestEditName(nameAdapter.getItem(position));
                break;
        }
    }

    /**
     * アイテム変更中か判定
     *
     * @return 変更対象アイテムがあればtrue
     */
    private boolean isChanging() {
        return changingItemPosition != ITEM_POSITION_NOTHING;
    }

    /**
     * 顔検出完了
     */
    @Override
    public void onDetectFaces(List<Uri> faces) {
        removeFragment(TAG_REQUEST_DETECT_FACES);
        if (faces.isEmpty()) {
            Toast.makeText(this, R.string.error_detect_face, Toast.LENGTH_SHORT).show();
        } else {
            faceAdapter.addItems(faces);
            finishEditButton.setEnabled(true);
        }
    }

    /**
     * 画像（顔）切り出し完了
     */
    @Override
    public void onCropImage(String tag, Uri croppedImageUri) {
        if (TAG_REQUEST_CROP_FACE.equals(tag)) {
            removeFragment(TAG_REQUEST_CROP_FACE);
            if (croppedImageUri == null) {
                Toast.makeText(this, R.string.error_crop_image, Toast.LENGTH_SHORT).show();
            } else {
                if (isChanging()) {
                    faceAdapter.changeItem(changingItemPosition, croppedImageUri);
                    changingItemPosition = ITEM_POSITION_NOTHING;
                } else {
                    faceAdapter.addItem(croppedImageUri);
                    finishEditButton.setEnabled(true);
                }
            }
        } else {
            super.onCropImage(tag, croppedImageUri);
        }
    }

    /**
     * 名簿取り込み完了
     */
    @Override
    public void onFinishOcr(List<String> roster) {
        removeFragment(TAG_REQUEST_OCR);
        if (roster.isEmpty()) {
            Toast.makeText(this, R.string.error_ocr, Toast.LENGTH_SHORT).show();
        } else {
            nameAdapter.addItems(roster);
            finishEditButton.setEnabled(true);
        }
    }

    /**
     * 名前編集要求
     *
     * @param currentName 現在の名前
     */
    private void requestEditName(String currentName) {
        new NameInputDialogFragment.Builder()
                .setDefaultValue(currentName)
                .create()
                .show(getFragmentManager(), TAG_REQUEST_EDIT_NAME);
    }

    /**
     * 名前入力or編集完了
     */
    @Override
    public void onFinishInput(String name) {
        if (isChanging()) {
            removeFragment(TAG_REQUEST_EDIT_NAME);
            nameAdapter.changeItem(changingItemPosition, name);
            changingItemPosition = ITEM_POSITION_NOTHING;
        } else {
            removeFragment(TAG_REQUEST_INPUT_NAME);
            nameAdapter.addItem(name);
            finishEditButton.setEnabled(true);
        }
    }

    /**
     * スロット開始
     */
    @OnClick(R.id.finish_edit)
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
