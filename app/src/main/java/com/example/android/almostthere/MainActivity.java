package com.example.android.almostthere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

//TODO: Can cancel foreground service via PendingIntent from the Activity(i.e. triggered by button)
//The service will not be restarted, it will just read the new intent, so we can send it
// a STOP_SERVICE_FROM_FOREGROUND action in the intent.
public class MainActivity extends AppCompatActivity implements
        SelectRiderPhoneNumberDialog.SelectRiderPhoneNumberListener,
        SelectDestinationDialog.SelectDestinationListener{

    final String TAG = "AlmostThereApp";
    final String BASE_URL = "https://api.opencagedata.com/geocode/v1/";
    final String MY_API_KEY = "f8bfb68fee434cfd950601fb5152681b";

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_READ_SMS = 9;
    private static final int MY_PERMISSIONS_READ_CONTACTS = 1;
    private static final int PICK_CONTACT_REQUEST = 2;
    public static int sharedPreferencesCount = 0;

    TextView mTextView;
    Button startTripButton, chooseRiderButton, chooseDestinationButton;

    private Location friendsLocation = null;
    private String friendsPhoneNumber, friendsName, friendsAddress;
    TextView phoneNumberTextView, addressInfoTextView;
    ImageView questionMarkImageView;
    SharedPreferences addressPreferences;
    SharedPreferences.Editor addressPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTextViewsAndButtons();
        setButtonOnClickListeners();
        addressPreferences = getPreferences(MODE_PRIVATE);
        addressPreferencesEditor = addressPreferences.edit();

        PermissionRequest mPermissionRequest = new PermissionRequest();
        mPermissionRequest.checkForSendSmsPermissions(this);
        mPermissionRequest.checkForReadSmsPermissions(this);
        mPermissionRequest.checkForReadContactsPermissions(this);
    }

    private void initTextViewsAndButtons() {
        phoneNumberTextView = (TextView) findViewById(R.id.phone_info_text_view);
        addressInfoTextView = (TextView) findViewById(R.id.address_info_text_view);
        startTripButton = (Button) findViewById(R.id.start_trip_button);
        chooseRiderButton = (Button) findViewById(R.id.enter_rider_info_button);
        chooseDestinationButton = (Button) findViewById(R.id.enter_destination_info_button);
        questionMarkImageView = (ImageView) findViewById(R.id.question_mark_image_view);
    }

    private void setButtonOnClickListeners() {
        startTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(friendsLocation != null ){
                    Toast.makeText(MainActivity.this, "Trip has started", Toast.LENGTH_SHORT).show();
                    setUpAndStartForegroundService();
                    //use sharedPrefCount to cycle thru 0->10 and limit address history to 10 items
                    //in my shared preferences
                    addressPreferencesEditor.putString(Integer.toString(sharedPreferencesCount)
                            ,friendsAddress);
                    addressPreferencesEditor.commit();
                    sharedPreferencesCount =  ++sharedPreferencesCount % 10;
                }else{
                    Toast.makeText(MainActivity.this, "Locations are null!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        chooseRiderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpAndStartChooseRiderDialog();
            }
        });
        chooseDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpAndStartChooseDestinationDialog();
            }
        });
        questionMarkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpAndStartQuestionMarkDialog();
            }
        });
    }

    private void setUpAndStartQuestionMarkDialog() {
        Toast.makeText(this, "This will be a info/helper box when clicked", Toast.LENGTH_SHORT).show();
    }

    private void setUpAndStartChooseDestinationDialog() {
        SelectDestinationDialog selectDestinationDialog = new SelectDestinationDialog();
        selectDestinationDialog.setmListener(this);
        selectDestinationDialog.show(getFragmentManager(), "selectDestinationDialog");
    }

    private void setUpAndStartChooseRiderDialog() {
        SelectRiderPhoneNumberDialog selectNumberDialog = new SelectRiderPhoneNumberDialog();
        selectNumberDialog.setmListener(this);
        selectNumberDialog.show(getFragmentManager(), "selectPhoneNumberDialog");
    }

    private void setUpAndStartForegroundService() {
        Intent myIntent = new Intent(this, TextMessageIntentService.class);
        setActionAndExtrasForMyIntent(myIntent);
        startService(myIntent);
    }

    private void setActionAndExtrasForMyIntent(Intent myIntent) {
        myIntent.setAction(TextMessageIntentService.START_LOCATION_UPDATES);
        //Friends location is set YES
        myIntent.putExtra(TextMessageIntentService.FRIENDS_LOCATION, friendsLocation);
        //Friends Phone Number is set YES
        myIntent.putExtra(TextMessageIntentService.FRIENDS_PHONE_NUMBER, friendsPhoneNumber);
        //Friends Name is set YES
        myIntent.putExtra(TextMessageIntentService.FRIENDS_NAME, friendsName);
        //Friends address is set YES
        myIntent.putExtra(TextMessageIntentService.FRIENDS_ADDRESS, friendsAddress);
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
                }
                    return;
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
            case MY_PERMISSIONS_READ_SMS: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Thank you for permission. Request a contact number again",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"Read SMS permission not granted. Sad!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onDialogSelectPhoneNumberPositiveClick(String phoneNumber, String name) {
        Toast.makeText(this, "Positive Button Clicked in Dialog", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Name: " + name, Toast.LENGTH_SHORT).show();
        phoneNumberTextView.setText("Phone Number entered: " + phoneNumber);
        friendsPhoneNumber = phoneNumber;
        friendsName = name;
    }

    @Override
    public void onDialogSelectDestinationPositiveClick(String address, String distance) {
        //TODO: PASS DISTANCE TO INTENT SERVICE
        Toast.makeText(this, "Destination Positive Click!", Toast.LENGTH_SHORT).show();
        friendsAddress = address;
        fetchGpsCoordinatesFromAddress(address);
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
                    loadResponseFromOpenCageApi(response);
                    Log.v(TAG, "Url called is:" + call.request().url().toString());
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

    private void loadResponseFromOpenCageApi(Response<Result> response) {
        if (response != null) {
            Geometry geometryObject = extractGeometryFromResultFromResponse(response);
            Double latitude = geometryObject.getLat();
            Double longitude = geometryObject.getLng();
            postLatLongsIfExist(latitude, longitude);
        } else {
            Toast.makeText(this, "problem occurred", Toast.LENGTH_SHORT).show();
        }
    }
    private Geometry extractGeometryFromResultFromResponse(Response<Result> response) {
        Result resultFromResponse = response.body();
        String code = resultFromResponse.getStatus().getCode().toString();
        //mTextView.setText("The response code is: " + code);
        resultFromResponse = response.body();
        Result_ result_ = resultFromResponse.getResults().get(0);
        return result_.getGeometry();
    }

    private void postLatLongsIfExist(Double latitude, Double longitude) {
        if (latitude != null && longitude != null) {
            String latLngResult = String.format("Latitude:%f \n Longitude:%f", latitude, longitude);
            //mTextView.setText(latLngResult);
            addressInfoTextView.setText("\nAddress is: " + friendsAddress);
            addressInfoTextView.append("\nGPS coordinates are set");
            addressInfoTextView.append(latLngResult);
            friendsLocation = new Location("C");
            friendsLocation.setLatitude(latitude);
            friendsLocation.setLongitude(longitude);
        }
    }
    interface OpenCageApiService {
        //okay, since I am encoding the string myself, I need to include "encode = true".
        @GET("json")
        Call<Result> getGeoCodedLocation(@Query("key") String apiKey,
                                         @Query(value = "q", encoded = true) String addressToQuery);
    }
}