package com.example.android.almostthere;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.almostthere.api_results.Geometry;
import com.example.android.almostthere.api_results.Result;
import com.example.android.almostthere.api_results.Result_;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by nick on 12/22/2017.
 */

public class SelectDestinationDialog extends DialogFragment{

    private final String TAG = "SelectDestinationDialog";

    public interface SelectDestinationListener{
        public void onDialogSelectDestinationPositiveClick(String address, String distance);
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.choose_destination_layout, null);
        mSpinner = (Spinner) view.findViewById(R.id.distance_spinner);
        destinationEditText = (EditText) view.findViewById(R.id.destination_edit_text);
        initSpinner();

        builder.setView(view)
                .setTitle("Enter Address Info")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String enteredAddress = destinationEditText.getText().toString();
                        if(!enteredAddress.equals("") && enteredAddress != null) {
                            mListener.onDialogSelectDestinationPositiveClick(enteredAddress, selectedDistance);
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

}
