package com.example.android.almostthere;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

//TODO: make help page to show how to grant SEND_SMS permission from Settings.
public class MainActivity extends AppCompatActivity {

    final String MY_API_KEY = "f8bfb68fee434cfd950601fb5152681b";
    final String BASE_URL = "https://api.opencagedata.com/geocode/v1/";
    final String EIGHT_ST_COFFEE = "710 8th St, Wichita Falls, TX 76301, United States";
    final String TAG = "AlmostThereApp";

    String myNumber = "940-257-4628";
    String msg = "this is only a test";

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_READ_CONTACTS = 1;
    private static final int PICK_CONTACT_REQUEST = 2;

    TextView mTextView;
    EditText mEditText;
    Button mButton;
    Button sendTextButton;
    Button chooseContactButton;

    public interface OpenCageApiService {
        //okay, since I am encoding the string myself, I need to include encode=true.
        @GET("json")
        Call<Result> getGeoCodedLocation(@Query("key") String apiKey,
                                         @Query(value = "q", encoded = true) String addressToQuery);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intiViews();
        setButtonOnClickListeners();

        checkForSendSmsPermissions();
        checkForReadContactsPermissions();
    }

    private void setButtonOnClickListeners() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = mEditText.getText().toString();
                if (address == null || address == "") return;
                //Lets test out 8th st coffee first then enter our own address.
                fetchGpsCoordinatesFromAddress(address);
                //fetchGpsCoordsFromAddress(EIGHT_ST_COFFEE);
            }
        });
        sendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checkForSendSmsPermissions();
                if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this, "We need SendSMS permission!", Toast.LENGTH_SHORT).show();
                }else{
                    sendTextMessage();
                }
            }
        });
        chooseContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this, "We need Read Contacts permission", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT_REQUEST);
                }
            }
        });
    }

    private void sendTextMessage() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(myNumber, null, msg, null, null);
    }

    private void checkForSendSmsPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
                showRequestSendSmsPermissionDialog();
            } else {
                requestSmsPermission();
            }
        } else {
            Toast.makeText(this, "SendSMS permission has been granted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkForReadContactsPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)){
                showRequestReadContactsPermissionDialog();
            }else{
                requestReadContactsPermission();
            }
        }else{
            Toast.makeText(this, "Read Contacts permission granted! Thank you!", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestReadContactsPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_READ_CONTACTS);
    }

    private void showRequestReadContactsPermissionDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Allow Read Contacts permission so we can get your contacts for you.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestReadContactsPermission();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void requestSmsPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS},
                MY_PERMISSIONS_REQUEST_SEND_SMS);
    }

    private void showRequestSendSmsPermissionDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Allow Send SMS permission so we can send text messages for you.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestSmsPermission();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void intiViews() {
        mTextView = (TextView) findViewById(R.id.text_view_1);
        mEditText = (EditText) findViewById(R.id.edit_text_view);
        mButton = (Button) findViewById(R.id.search_button);
        sendTextButton = (Button) findViewById(R.id.send_text_button);
        chooseContactButton = (Button) findViewById(R.id.choose_contact_button);
    }

    private void fetchGpsCoordinatesFromAddress(String addressToQuery) {
        OpenCageApiService service = createOpenCageApiServiceInstance();
        String formattedAddressString = formatAddressProperly(addressToQuery);
        Call<Result> call = service.getGeoCodedLocation(MY_API_KEY, formattedAddressString);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body() == null) {
                    int code = response.code();
                    Toast.makeText(MainActivity.this, "Response is null.Problem!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Response code: " + code, Toast.LENGTH_SHORT).show();
                    Log.v(TAG, response.message());
                    Log.v(TAG, response.errorBody().toString());
                    Log.v(TAG, "Request Url: " + call.request().url().toString());
                } else {
                    loadResponse(response);
                    Log.v(TAG, "Url2 called is:" + call.request().url().toString());
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failure!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.toString());
            }
        });
    }

    private OpenCageApiService createOpenCageApiServiceInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(OpenCageApiService.class);
    }

    private String formatAddressProperly(String addressToQuery) {
        String replacedSpacesString = addressToQuery.replace(' ', '+');
        String secondReplacedString = replacedSpacesString.replace(",", "%2C");
        Log.v(TAG, "The reformatted string is: " + secondReplacedString);
        return secondReplacedString;
    }

    private void loadResponse(Response<Result> response) {
        if (response != null) {
            Geometry geometryObject = extractGeometryFromResultFromResponse(response);
            Double latitude = geometryObject.getLat();
            Double longitude = geometryObject.getLng();
            postLatLongsIfExist(latitude, longitude);
        } else {
            Toast.makeText(MainActivity.this, "problem occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private Geometry extractGeometryFromResultFromResponse(Response<Result> response) {
        Result resultFromResponse = response.body();
        String code = resultFromResponse.getStatus().getCode().toString();
        mTextView.setText("The response code is: " + code);
        resultFromResponse = response.body();
        Result_ result_ = resultFromResponse.getResults().get(0);
        return result_.getGeometry();
    }

    private void postLatLongsIfExist(Double latitude, Double longitude) {
        if (latitude != null && longitude != null) {
            String latLngResult = String.format("Latitude:%f \n Longitude:%f", latitude, longitude);
            mTextView.setText(latLngResult);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "Thank you for the permission! Try sending text again.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Permission failed to be granted. Sad Panda face.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
            case MY_PERMISSIONS_READ_CONTACTS: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Thank you for permission. Request a contact number again",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"Read Contacts permission not granted. Sad!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK){
            Uri uriContact = data.getData();
            String phoneNumberRetrieved = retrieveContactNumber(uriContact);
            String contactNameRetrieved = retrieveContactsName(uriContact);
            String msg = String.format("ContactName: %s \nContact Number: %s",
                    contactNameRetrieved, phoneNumberRetrieved);
            mTextView.setText(msg);
        }
    }

    private String retrieveContactsName(Uri uriContact) {
        String contactName = null;
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
        if(cursor.moveToFirst()){
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
        return contactName;
    }

    /**
     * Stole this function from a github example. It works!
     * https://gist.github.com/evandrix/7058235
     * All I can tel is that it uses Cursors, kinda like SqLite
     * TODO: learn ContentResolvers
     */
    private String retrieveContactNumber(Uri uriContact) {
        String contactNumber = null;
        String contactId = null;
        Cursor cursorId = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);
        if(cursorId.moveToFirst()){
            contactId = cursorId.getString(cursorId.getColumnIndex(ContactsContract.Contacts._ID));
        }
        cursorId.close();
        Log.v(TAG, "Contact ID: " + contactId);
        //using the ContactId we now get the contact phone number
        //TODO: It currently only retrieves numbers saved as "mobile" in the contact.
        //TODO: Have it check for "home" and probably "work" also. 
        Cursor cursorPhone = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                //this looks similar to a SQL statement
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactId},
                null);
        if(cursorPhone.moveToFirst()){
            contactNumber = cursorPhone
                    .getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursorPhone.close();
        return contactNumber;
    }
}
