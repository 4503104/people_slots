package jp.gr.java_conf.shygoo.people_slots.activity;

import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.gr.java_conf.shygoo.people_slots.R;
import jp.gr.java_conf.shygoo.people_slots.fragment.dialog.ChoiceDialogFragment;
import jp.gr.java_conf.shygoo.people_slots.fragment.dialog.NameInputDialogFragment;

/**
 * メイン画面
 */
public class MainActivity extends BaseActivity implements ChoiceDialogFragment.OnSelectListener,
        NameInputDialogFragment.OnFinishInputListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    /**
     * （ダイアログの）選択結果を受け取る
     *
     * @param itemId 選択肢のID
     */
    @Override
    public void onSelectItem(int itemId) {
        switch (itemId) {
            case R.string.type_face:
                askCaptureMethod();
                break;
            case R.string.type_name:
                askInputMethod();
                break;
            case R.string.capture_method_fr:
                //TODO
                break;
            case R.string.capture_method_manual:
                //TODO
                break;
            case R.string.input_method_ocr:
                //TODO
                break;
            case R.string.input_method_manual:
                requestInputName();
                break;
            default:
                // nop
                break;
        }
    }

    @Override
    public void onFinishInput(String name) {
    }

    public void onFinishOcr(String[] roster) {
        //TODO
    }

    /**
     * スロットの種類を尋ねる
     */
    @OnClick(R.id.create_slot)
    public void askType() {
        new ChoiceDialogFragment.Builder()
                .setTitle(R.string.ask_type)
                .setItems(R.array.slot_types)
                .create()
                .show(getFragmentManager(), "ask_type");
    }

    /**
     * 顔取り込みの手段を尋ねる
     */
    private void askCaptureMethod() {
        new ChoiceDialogFragment.Builder()
                .setTitle(R.string.ask_capture_method)
                .setItems(R.array.capture_methods)
                .create()
                .show(getFragmentManager(), "ask_capture_method");
    }

    /**
     * 名前取り込みの手段を尋ねる
     */
    private void askInputMethod() {
        new ChoiceDialogFragment.Builder()
                .setTitle(R.string.ask_input_method)
                .setItems(R.array.input_methods)
                .create()
                .show(getFragmentManager(), "ask_input_method");
    }

    /**
     * 名前の入力をさせる
     */
    private void requestInputName() {
        new NameInputDialogFragment().show(getFragmentManager(), "request_input_name");
    }
}
