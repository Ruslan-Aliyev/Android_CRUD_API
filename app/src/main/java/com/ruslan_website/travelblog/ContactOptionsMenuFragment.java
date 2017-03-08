package com.ruslan_website.travelblog;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ContactOptionsMenuFragment extends ListFragment {

    String[] ContactFormOptionsText = new String[] { "Normal Form", "Interactive Form", "Ratings" };
    int[] ContactFormOptionsNumber = new int[] { 1, 2, 3 };

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.contact_options_menu_fragment, container, false);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ContactFormOptionsText);
        setListAdapter(adapter);

        return view;

    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ContactContentFragment txt = (ContactContentFragment)getFragmentManager().findFragmentById(R.id.contactContentFragment);
        txt.change(ContactFormOptionsNumber[position]);
        getListView().setSelector(android.R.color.holo_blue_dark);
    }
}
