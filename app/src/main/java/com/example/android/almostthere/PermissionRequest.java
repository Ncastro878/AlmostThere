package com.example.android.almostthere;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by nick on 12/13/2017.
 */

public class PermissionRequest {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_READ_SMS = 9;
    private static final int MY_PERMISSIONS_READ_CONTACTS = 1;

    public void checkForSendSmsPermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                    android.Manifest.permission.SEND_SMS)) {
                 showRequestSendSmsPermissionDialog(context);
            } else {
                requestSendSmsPermission(context);
            }
        } else {
            Toast.makeText(context, "SendSMS permission has been granted!", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkForReadSmsPermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                    Manifest.permission.READ_SMS)) {
                showRequestReadSmsPermissionDialog(context);
            } else {
                requestReadSmsPermission(context);
            }
        } else {
            Toast.makeText(context, "SendSMS permission has been granted!", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkForReadContactsPermissions(Context context) {
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    android.Manifest.permission.READ_CONTACTS)){
                showRequestReadContactsPermissionDialog(context);
            }else{
                requestReadContactsPermission(context);
            }
        }else{
            Toast.makeText(context, "Read Contacts permission granted! Thank you!", Toast.LENGTH_SHORT).show();
        }
    }

    public void showRequestSendSmsPermissionDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setMessage("Allow Send SMS permission so we can send text messages for you.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestSendSmsPermission(context);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    public void showRequestReadContactsPermissionDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setMessage("Allow Read Contacts permission so we can get your contacts for you.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestReadContactsPermission(context);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private void showRequestReadSmsPermissionDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setMessage("Allow Read-SMS permission so we can read text messages for you.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestReadSmsPermission(context);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void requestReadContactsPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{android.Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_READ_CONTACTS);
    }
    public void requestSendSmsPermission(Context context) {
        ActivityCompat.requestPermissions((Activity)context,
                new String[]{android.Manifest.permission.SEND_SMS},
                MY_PERMISSIONS_REQUEST_SEND_SMS);
    }
    public void requestReadSmsPermission(Context context) {
        ActivityCompat.requestPermissions((Activity)context,
                new String[]{Manifest.permission.READ_SMS},
                MY_PERMISSIONS_READ_SMS);
    }
}