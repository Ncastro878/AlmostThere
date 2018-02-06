package com.nickrcastro.android.almostthere;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by nick on 12/22/2017.
 */

public class SelectAddressDialog extends DialogFragment
        implements AddressHistoryDialog.RecentAddressListener{

    private final String TAG = "SelectAddressDialog";

    public interface SelectDestinationListener{
        public void onDialogSelectDestinationPositiveClick(String address, String distance, float radius);
    }
    public void setmListener(SelectDestinationListener mListener) {
        this.mListener = mListener;
    }

    private Location friendsLocation = null;
    private String friendsPhoneNumber;
    private String friendsName;
    private String friendsAddress;
    String selectedDistance;

    SelectDestinationListener mListener;
    Spinner mSpinner;
    EditText destinationEditText = null;
    Button addressHistoryButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.choose_destination_layout, null);
        mSpinner = (Spinner) view.findViewById(R.id.distance_spinner);
        addressHistoryButton = (Button) view.findViewById(R.id.history_button);
        destinationEditText = (EditText) view.findViewById(R.id.destination_edit_text);
        initSpinner();
        initHistoryButton();

        builder.setView(view)
                .setTitle("Enter Address Info")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String enteredAddress = destinationEditText.getText().toString();
                        if(!enteredAddress.equals("") && enteredAddress != null) {
                            float selectedRadius = Float.parseFloat(mSpinner.getSelectedItem().toString());
                            mListener.onDialogSelectDestinationPositiveClick(enteredAddress, selectedDistance, selectedRadius);
                            Toast.makeText(getActivity(), "Distance = " + selectedDistance, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(), "Address not entered", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
        return builder.create();
    }

    private void initHistoryButton() {
        addressHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpAndStartRecentAddressDialog();
            }
        });
    }

    private void setUpAndStartRecentAddressDialog() {
        AddressHistoryDialog dialog = new AddressHistoryDialog();
        dialog.setRecentAddressListener(this);
        dialog.show(getFragmentManager(),"RecentAddressDialog");
    }

    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (getActivity(), R.array.distances_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String distance = adapterView.getItemAtPosition(pos).toString();
                selectedDistance = distance;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                String distance = adapterView.getItemAtPosition(0).toString();
                selectedDistance = distance;
            }
        });
    }

    @Override
    public void addressSelected(String selectedAddress) {
        destinationEditText.setText(selectedAddress);
    }
}
