package jp.gr.java_conf.shygoo.people_slots.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;

/**
 * 選択肢から1つを選ばせるダイアログ
 * TODO: Intent.createChooser()と混在すると不自然なので何とかしたい。
 */
public class ChoiceDialogFragment extends DialogFragment {

    // リソースIDを持たない選択肢を選ばれた場合のダミーID
    private static final int UNKNOWN_ITEM = -1;

    // 引数
    private static final String ARG_TITLE_ID = "titleId";
    private static final String ARG_MESSAGE_ID = "messageId";
    private static final String ARG_ITEMS_ID = "itemsId";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // 作成準備
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // タイトルとメッセージは任意
        Bundle args = getArguments();
        if (args.containsKey(ARG_TITLE_ID)) {
            builder.setTitle(args.getInt(ARG_TITLE_ID));
        }
        if (args.containsKey(ARG_MESSAGE_ID)) {
            builder.setMessage(args.getInt(ARG_MESSAGE_ID));
        }

        // 選択肢は必ず設定
        builder.setItems(args.getInt(ARG_ITEMS_ID), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // 呼び出し元に選択されたリソースのIDを返す
                OnSelectListener listener = null;
                Activity activity = getActivity();
                if (activity instanceof OnSelectListener) {
                    listener = (OnSelectListener) activity;
                } else {
                    Fragment targetFragment = getTargetFragment();
                    if (targetFragment instanceof OnSelectListener) {
                        listener = (OnSelectListener) targetFragment;
                    }
                }
                if (listener == null) {
                    return;
                }
                int arrayId = getArguments().getInt(ARG_ITEMS_ID);
                TypedArray typedArray = getResources().obtainTypedArray(arrayId);
                int selectedItemResId = typedArray.getResourceId(which, UNKNOWN_ITEM);
                typedArray.recycle();
                listener.onSelectItem(selectedItemResId);
            }
        });
        return builder.create();
    }

    public static class Builder {

        private Bundle args;

        public Builder() {
            args = new Bundle();
        }

        public Builder setTitle(@StringRes int titleId) {
            args.putInt(ARG_TITLE_ID, titleId);
            return this;
        }

        public Builder setMessage(@StringRes int messageId) {
            args.putInt(ARG_MESSAGE_ID, messageId);
            return this;
        }

        public Builder setItems(@ArrayRes int itemsId) {
            args.putInt(ARG_ITEMS_ID, itemsId);
            return this;
        }

        public ChoiceDialogFragment create() {
            ChoiceDialogFragment fragment = new ChoiceDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }
    }

    public interface OnSelectListener {
        void onSelectItem(int itemId);
    }
}

