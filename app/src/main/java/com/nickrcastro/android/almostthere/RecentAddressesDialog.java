package com.nickrcastro.android.almostthere;

 import android.app.AlertDialog;
 import android.app.Dialog;
 import android.app.DialogFragment;
 import android.content.Context;
 import android.content.DialogInterface;
 import android.content.SharedPreferences;
 import android.os.Bundle;
 import android.support.annotation.LayoutRes;
 import android.support.annotation.NonNull;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.widget.AdapterView;
 import android.widget.ArrayAdapter;
 import android.widget.ListView;
 import android.widget.Toast;

 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;

/**
 * Created by nick on 1/3/2018.
 */

public class RecentAddressesDialog extends DialogFragment {

    ListView addressHistoryListView;
    RecentAddressListener mRecentAddressListener;

    public interface RecentAddressListener{
        void addressSelected(String selectedAddress);
    }

    void setRecentAddressListener(RecentAddressListener listener){
        mRecentAddressListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.address_history_dialog_layout, null);

        addressHistoryListView = view.findViewById(R.id.address_history_list_view);
        populateAddressHistoryTextView();
        builder.setView(view)
                .setTitle("Address History")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "CLICKED!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
        return builder.create();
    }

    private void populateAddressHistoryTextView() {
        SharedPreferences sharedPrefs = getActivity().getPreferences(0);
        int numOfAddress = sharedPrefs.getAll().size();
        List<String> addressList = new ArrayList<>();
        for(int i = 0; i < numOfAddress; i++){
            String address = sharedPrefs.getString(Integer.toString(i), "empty");
            addressList.add(address);
        }
        AddressHistoryArrayAdapter adapter = new AddressHistoryArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, addressList);//
        addressHistoryListView.setAdapter(adapter);

        addressHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                String selectedAddress = (String) adapterView.getItemAtPosition(pos);
                mRecentAddressListener.addressSelected(selectedAddress);
                dismiss();
            }
        });
    }

    private class AddressHistoryArrayAdapter extends ArrayAdapter<String>{
        HashMap<String, Integer> myHashMap = new HashMap<String, Integer>();
        public AddressHistoryArrayAdapter(@NonNull Context context,
                                          @LayoutRes int resource  ,List<String> addressList) {
            super(context, resource,  addressList);
            for(int i = 0; i <addressList.size(); i++){
                myHashMap.put(addressList.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return myHashMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
