package jp.gr.java_conf.shygoo.people_slots.fragment;


import android.app.Fragment;
import android.os.Bundle;

import icepick.Icepick;

/**
 * 基底Fragment
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
