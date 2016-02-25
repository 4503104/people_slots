package jp.gr.java_conf.shygoo.people_slots.fragment.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.StringRes;

/**
 * 進捗ダイアログ（通称「ぐるぐる」）
 */
public class ProgressDialogFragment extends DialogFragment {

    private static final String ARG_TITLE_ID = "titleId";
    private static final String ARG_MESSAGE_ID = "messageId";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // 進捗ダイアログを準備
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // タイトルとメッセージは任意
        Bundle args = getArguments();
        if (args.containsKey(ARG_TITLE_ID)) {
            dialog.setTitle(args.getInt(ARG_TITLE_ID));
        }
        if (args.containsKey(ARG_MESSAGE_ID)) {
            dialog.setMessage(getString(args.getInt(ARG_MESSAGE_ID)));
        }

        // キャンセル不可とする
        setCancelable(false);

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
         * タイトル（オプション）を設定
         *
         * @param titleId タイトル
         * @return Builder自身
         */
        public Builder setTitle(@StringRes int titleId) {
            args.putInt(ARG_TITLE_ID, titleId);
            return this;
        }

        /**
         * メッセージ（オプション）を設定
         *
         * @param messageId メッセージ
         * @return Builder自身
         */
        public Builder setMessage(@StringRes int messageId) {
            args.putInt(ARG_MESSAGE_ID, messageId);
            return this;
        }

        /**
         * Fragment生成
         *
         * @return Fragment
         */
        public ProgressDialogFragment create() {
            ProgressDialogFragment fragment = new ProgressDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }
    }
}

