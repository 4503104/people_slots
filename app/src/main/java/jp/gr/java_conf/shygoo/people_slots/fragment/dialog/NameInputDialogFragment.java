package jp.gr.java_conf.shygoo.people_slots.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import jp.gr.java_conf.shygoo.people_slots.R;

/**
 * 名前を入力させるダイアログ
 */
public class NameInputDialogFragment extends DialogFragment {

    /**
     * 引数：デフォルト値（省略可）
     */
    public static final String ARG_DEFAULT_VALUE = "defaultValue";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // カスタムViewを使う
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_name_input, null);
        builder.setView(customView);

        // デフォルト値が指定されていればセット
        String defaultValue = getArguments().getString(ARG_DEFAULT_VALUE, "");
        EditText nameInput = (EditText) customView.findViewById(R.id.name_input);
        nameInput.setText(defaultValue);

        // OKボタンを設定
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            // リスナーに入力値を渡す
            @Override
            public void onClick(DialogInterface dialog, int id) {
                OnFinishInputListener listener = null;
                Activity activity = getActivity();
                if (activity instanceof OnFinishInputListener) {
                    listener = (OnFinishInputListener) activity;
                } else {
                    Fragment targetFragment = getTargetFragment();
                    if (targetFragment instanceof OnFinishInputListener) {
                        listener = (OnFinishInputListener) targetFragment;
                    }
                }
                if (listener == null) {
                    return;
                }
                AlertDialog alertDialog = (AlertDialog) dialog;
                EditText nameInput = (EditText) alertDialog.findViewById(R.id.name_input);
                listener.onFinishInput(nameInput.getText().toString());
            }
        });

        // 値がある時だけボタンを押せるようにする
        Dialog dialog = builder.create();
        if (defaultValue.isEmpty()) {
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            });
        }
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) getDialog();
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setEnabled(!s.toString().isEmpty());
            }
        });

        return dialog;
    }

    /**
     * 専用Builder
     */
    public static class Builder {

        private Bundle args;

        public Builder() {
            args = new Bundle();
        }

        /**
         * デフォルト値（オプション）を設定
         *
         * @param defaultValue デフォルト値
         * @return Builder自身
         */
        public Builder setDefaultValue(String defaultValue) {
            args.putString(ARG_DEFAULT_VALUE, defaultValue);
            return this;
        }

        /**
         * Fragment生成
         *
         * @return Fragment
         */
        public NameInputDialogFragment create() {
            NameInputDialogFragment fragment = new NameInputDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }
    }

    /**
     * 結果通知用Listener
     */
    public interface OnFinishInputListener {

        /**
         * 入力完了イベント
         *
         * @param name 入力値（名前）
         */
        void onFinishInput(String name);
    }
}

