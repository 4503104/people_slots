package jp.gr.java_conf.shygoo.people_slots;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_sample);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(
                new LoopListAdapter(
                        this,
                        Arrays.asList(new String[]{"ほげ", "ふが", "ぴよ"})
                )
        );
    }

    public class LoopListAdapter extends BaseAdapter {
        private List<String> itemTexts;
        private LayoutInflater inflater;

        public LoopListAdapter(Context context, List<String> itemTexts) {
            this.itemTexts = new ArrayList<>(itemTexts);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object getItem(int position) {
            return itemTexts.get(position % itemTexts.size());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            if (convertView == null) {
                view = inflater.inflate(R.layout.item_slot, parent, false);
            } else {
                view = convertView;
            }

            TextView itemText = (TextView) view.findViewById(R.id.item_text);
            itemText.setText((String) getItem(position));

            return view;
        }
    }
}
