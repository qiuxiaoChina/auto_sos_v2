package com.autosos.rescue.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autosos.rescue.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentForMine extends Fragment {
    private static FragmentForMine fragment = null;
    public static Fragment newInstance() {
        if (fragment == null) {
            synchronized (FragmentForMine.class) {
                if (fragment == null) {
                    fragment = new FragmentForMine();

                }
            }
        }
        return fragment;
    }

    public FragmentForMine() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_for_mine, null);
    }

}
