package com.autosos.yd.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autosos.yd.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentForOrder extends Fragment {

    private static FragmentForOrder fragment = null;
    public static Fragment newInstance() {
        if (fragment == null) {
            synchronized (FragmentForOrder.class) {
                if (fragment == null) {
                    fragment = new FragmentForOrder();

                }
            }
        }
        return fragment;
    }


    public FragmentForOrder() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_for_order, null);
    }

}
