package com.nickrcastro.android.almostthere;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import static android.R.attr.phoneNumber;
import static android.app.Activity.RESULT_OK;

/**
 * Created by nick on 12/22/2017.
 */

public class SelectPhoneNumberDialog extends DialogFragment {

    public interface SelectRiderPhoneNumberListener {
        void onDialogSelectPhoneNumberPositiveClick(FriendObject friend);
    }

    private String friendsPhoneNumber = null;
    private String friendsName = "";
    Button chooseContactButton;
    private static final int PICK_CONTACT_REQUEST = 2;
    String TAG = "SelectRiderPhoneNumber";

    EditText phoneNumberEditText;

    public void setmListener(SelectRiderPhoneNumberListener mListener) {
        this.mListener = mListener;
    }

    SelectRiderPhoneNumberListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.choose_rider_number_layout, null);

        chooseContactButton = (Button) view.findViewById(R.id.contacts_button);
        phoneNumberEditText = (EditText) view.findViewById(R.id.phone_number_edit_text);
        setChooseContactButtonOnClickListener();
        builder.setView(view)
                .setTitle("Enter or Choose Rider's Phone Number")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FriendObject locationlessFriendObject = new FriendObject();
                        String phoneNumber = phoneNumberEditText.getText().toString();
                        String name =  friendsName.equals(null) || friendsName.equals("")?
                                "No Name": friendsName;
                        locationlessFriendObject.setName(name);
                        locationlessFriendObject.setPhoneNumber(phoneNumber);
                        if(!phoneNumber.equals("") && !phoneNumber.equals(null)){
                            //TODO: turn this into an object
                            mListener.onDialogSelectPhoneNumberPositiveClick(locationlessFriendObject);
                        } else{
                            Toast.makeText(getActivity(), "Phone Number not entered or selected",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
        return builder.create();
    }

    private void setChooseContactButtonOnClickListener() {
        chooseContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "We need Read Contacts permission", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT_REQUEST);
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK) {
            Uri uriContact = data.getData();
            String phoneNumberRetrieved = retrieveContactNumber(uriContact);
            String contactNameRetrieved = retrieveContactsName(uriContact);
            String msg = String.format("ContactName: %s \nContact Number: %s",
                    contactNameRetrieved, phoneNumberRetrieved);
            //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            friendsPhoneNumber = phoneNumberRetrieved;
            phoneNumberEditText.setText(friendsPhoneNumber);
            //TODO: SEND FRIENDS NAME
            friendsName = contactNameRetrieved;
        }
    }

    private String retrieveContactsName(Uri uriContact) {
        String contactName = null;
        Cursor cursor = getActivity().getContentResolver().query(uriContact, null, null, null, null);
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
        return contactName;
    }

    /**
     * Stole this function from a github example. It works!
     * https://gist.github.com/evandrix/7058235
     * All I can tell is that it uses Cursors, kinda like SqLite
     * TODO: learn ContentResolvers
     * this link may come in handy:
     * https://developer.android.com/training/basics/intents/result.html
     */
    private String retrieveContactNumber(Uri uriContact) {
        String contactNumber = null;
        String contactId = null;
        Cursor cursorId = getActivity().getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);
        if (cursorId.moveToFirst()) {
            contactId = cursorId.getString(cursorId.getColumnIndex(ContactsContract.Contacts._ID));
        }
        cursorId.close();
        //Log.v(TAG, "Contact ID:" + contactId);
        //using the ContactId we now get the contact phone number
        Cursor cursorPhone = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                //this looks similar to a SQL statement
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        /*this returns MOBILE number, not WORK, or HOME, which is fine
                        because only MOBILE numbers can receive texts. */
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[]{contactId}, null);
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone
                    .getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursorPhone.close();
        return contactNumber;
    }
}
