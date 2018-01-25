package com.nickrcastro.android.almostthere;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 12/13/2017.
 */

public class PermissionRequest {

    private static final int MY_PERMISSIONS_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_READ_SMS = 9;
    private static final int MY_PERMISSIONS_READ_CONTACTS = 1;
    private static final int MY_PERMISSIONS_FINE_LOCATION = 98;
    private static final int MY_PERMISSIONS_COURSE_LOCATION = 99;
    private static final int MY_PERMISSION_MULTIPLE_PERMISSIONS = 55;

    private static final String[] MULTIPLE_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS} ;

    public void checkForSendSmsPermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                    Manifest.permission.SEND_SMS)) {
                 showRequestSendSmsPermissionDialog(context);
            } else {
                requestSendSmsPermission(context);
            }
        } else {
            Toast.makeText(context, "SendSMS permission has been granted!", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkForFineLocationPermissions(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                showRequestFineLocationPermissionDialog(context);
            } else {
                requestFineLocationPermission(context);
            }
        } else {
            Toast.makeText(context, "SendSMS permission has been granted!", Toast.LENGTH_SHORT).show();
        }
    }
    public void checkForCoarseLocationPermissions(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showRequestCoarseLocationPermissionDialog(context);
            } else {
                requestFineLocationPermission(context);
            }
        } else {
            Toast.makeText(context, "SendSMS permission has been granted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestFineLocationPermission(Context context) {
        ActivityCompat.requestPermissions((Activity)context,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_FINE_LOCATION);
    }
    private void requestCoarseLocationPermission(Context context){
        ActivityCompat.requestPermissions((Activity)context,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_COURSE_LOCATION);
    }

    private void showRequestFineLocationPermissionDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setMessage("Allow us to track/use your location for distance measurement.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestFineLocationPermission(context);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private void showRequestCoarseLocationPermissionDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setMessage("Allow us to track/use your location for distance measurement.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestCoarseLocationPermission(context);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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
    public static boolean checkPermissionsForActionBar(Context context){
        MenuItem check = ((Activity)context).findViewById(R.id.permissions_check);
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
                        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                ){
            return true;
        }else{
            return false;
        }
    }
    public void checkForReadContactsPermissions(Context context) {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.READ_CONTACTS)){
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
                new String[]{Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_READ_CONTACTS);
    }
    public void requestSendSmsPermission(Context context) {
        ActivityCompat.requestPermissions((Activity)context,
                new String[]{Manifest.permission.SEND_SMS},
                MY_PERMISSIONS_SEND_SMS);
    }
    public void requestReadSmsPermission(Context context) {
        ActivityCompat.requestPermissions((Activity)context,
                new String[]{Manifest.permission.READ_SMS},
                MY_PERMISSIONS_READ_SMS);
    }
    /** These 3 methods go together**/
    public void wrapNeededPermissions(final Context context){
        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();

        if (!addPermission(context, permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(context, permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(context, permissionsList, Manifest.permission.SEND_SMS))
            permissionsNeeded.add("Send Texts");
        if (!addPermission(context, permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Read Contacts");
        if (!addPermission(context, permissionsList, Manifest.permission.READ_SMS))
            permissionsNeeded.add("Read Texts");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(context, message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(
                                        (Activity)context,
                                        permissionsList.toArray(new String[permissionsList.size()]),
                                        MY_PERMISSION_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions((Activity)context,
                    permissionsList.toArray(new String[permissionsList.size()]),
                    MY_PERMISSION_MULTIPLE_PERMISSIONS);
            return;
        }
    }

    private void showMessageOKCancel(final Context context, String message,
                                     DialogInterface.OnClickListener clickListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK",clickListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean addPermission(Context context ,List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission))
                return false;
        }
        return true;
    }

    //can sub MULTIPLE_PERMISSIONS variable later
    public void requestMultiplePermissions(Context context) {
        ActivityCompat.requestPermissions((Activity)context,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.SEND_SMS},
                MY_PERMISSION_MULTIPLE_PERMISSIONS );
    }
}