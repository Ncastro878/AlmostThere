package com.nickrcastro.android.almostthere;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nickrcastro.android.almostthere.api_results.Result;
import com.nickrcastro.android.almostthere.api_results.ApiResults;
import com.nickrcastro.android.almostthere.api_results.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static android.view.View.GONE;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.nickrcastro.android.almostthere.TextMessageIntentService.STOP_SERVICE_FROM_FOREGROUND;

public class MainActivity extends AppCompatActivity implements
        SelectPhoneNumberDialog.SelectRiderPhoneNumberListener,
        SelectAddressDialog.SelectDestinationListener{

    public static final String MY_ADDRESS_HISTORY = "my-address-history";
    public static final String TEXT_MSG_TO_SEND = "text-msg-to-send";
    public static final String FRESH_INSTALL = "fresh-install";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_READ_SMS = 9;
    private static final int MY_PERMISSIONS_READ_CONTACTS = 1;
    public static int sharedPreferencesCount = 0;
    final String BASE_URL = "https://api.opencagedata.com/geocode/v1/";
    final String MY_API_KEY = "f8bfb68fee434cfd950601fb5152681b";
    public Button startTripButton, chooseRiderButton, chooseDestinationButton, stopTripButton;
    TextView phoneNumberTextView, addressInfoTextView;
    ImageView mapIcon;
    SharedPreferences addressPreferences,textMessageToSendPrefs, freshInstallPrefs;
    SharedPreferences.Editor addressPreferencesEditor, textMessageToSendPrefEditor;
    private Location friendsLocation = null;
    private String friendsPhoneNumber, friendsName, friendsAddress;
    private float destinationRadius;

    private BroadcastReceiver myReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
             String message = intent.getStringExtra("change-button");
            if(message.equals("true") ){
                startTripButton.setVisibility(View.VISIBLE);
                stopTripButton.setVisibility(GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTextViewsAndButtons();
        setButtonOnClickListeners();
        initSharedPreferences();
        initPermissionsRequest();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter("destination-reached"));
        runTourGuideOnFreshInstall();
    }

    private void runTourGuideOnFreshInstall() {
        freshInstallPrefs = getSharedPreferences(FRESH_INSTALL, MODE_PRIVATE);
        String isFreshInstall = freshInstallPrefs.getString("freshInstall","true");
        if(isFreshInstall == "true"){
            MyTourGuide.runTourGuide(this);
            freshInstallPrefs.edit().putString("freshInstall", "false");
            freshInstallPrefs.edit().apply();
        }else{

        }
    }

    private void initPermissionsRequest() {
        /** Lets try the multiple permissions
         mPermissionRequest.checkForSendSmsPermissions(this);
         mPermissionRequest.checkForReadSmsPermissions(this);
         mPermissionRequest.checkForReadContactsPermissions(this);
         mPermissionRequest.checkForCoarseLocationPermissions(this);
         mPermissionRequest.checkForFineLocationPermissions(this);
         */
        PermissionRequest mPermissionRequest = new PermissionRequest();
        mPermissionRequest.requestMultiplePermissions(this);
    }

    private void initSharedPreferences() {
        addressPreferences = getSharedPreferences(MY_ADDRESS_HISTORY, MODE_PRIVATE);
        addressPreferencesEditor = addressPreferences.edit();
        textMessageToSendPrefs = getSharedPreferences(TEXT_MSG_TO_SEND, MODE_PRIVATE);
        textMessageToSendPrefEditor = textMessageToSendPrefs.edit();
        if(textMessageToSendPrefs.getString("1","") == ""){
            Toast.makeText(this, "it was true", Toast.LENGTH_SHORT).show();
            textMessageToSendPrefEditor.putString("1", TextMsgToSendDialog.choices[0]);
            textMessageToSendPrefEditor.commit();
            Toast.makeText(this, textMessageToSendPrefs.getString("1", "none again"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter("destination-reached"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }

    private void initTextViewsAndButtons() {
        phoneNumberTextView = (TextView) findViewById(R.id.phone_info_text_view);
        addressInfoTextView = (TextView) findViewById(R.id.address_info_text_view);
        startTripButton = (Button) findViewById(R.id.start_trip_button);
        chooseRiderButton = (Button) findViewById(R.id.enter_rider_info_button);
        chooseDestinationButton = (Button) findViewById(R.id.enter_destination_info_button);
        mapIcon = (ImageView) findViewById(R.id.map_icon);
        stopTripButton = (Button) findViewById(R.id.stop_trip_button);
    }

    private void setButtonOnClickListeners() {
        startTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(friendsLocation != null && friendsPhoneNumber != null){
                    Toast.makeText(MainActivity.this, "Trip has started", Toast.LENGTH_SHORT).show();
                    setUpAndStartForegroundService();
                    //use sharedPrefCount to cycle thru 0->10 and limit address history to 10 items
                    //in my shared preferences
                    addressPreferencesEditor.putString(Integer.toString(sharedPreferencesCount)
                            ,friendsAddress);
                    addressPreferencesEditor.commit();
                    sharedPreferencesCount =  ++sharedPreferencesCount % 10;
                    startTripButton.setVisibility(GONE);
                    stopTripButton.setVisibility(View.VISIBLE);
                 }else{
                    Toast.makeText(MainActivity.this, "Choose a Contact AND Location!", Toast.LENGTH_SHORT).show();
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
        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 verifyAddressWithGoogleMaps();
            }
        });
        stopTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stopServiceIntent = new Intent(MainActivity.this, TextMessageIntentService.class);
                stopServiceIntent.setAction(STOP_SERVICE_FROM_FOREGROUND);
                startService(stopServiceIntent);
                stopTripButton.setVisibility(GONE);
                startTripButton.setVisibility(View.VISIBLE);
                showEndedTripEarlyNotification();
            }
        });
    }

    private void verifyAddressWithGoogleMaps() {
        if(friendsLocation == null){
            Toast.makeText(this, "Enter an address please!", Toast.LENGTH_SHORT).show();
            return;
        }
        String myLat = String.valueOf(friendsLocation.getLatitude());
        String myLong = String.valueOf(friendsLocation.getLongitude());
        String name = friendsName == null ?  "Friend" : friendsName;
        String gps2 = String.format("geo:0,0?q=%s,%s(%s)", myLat, myLong, name);
        Uri mapUri = Uri.parse(gps2);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(mapUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showEndedTripEarlyNotification() {
        //Create and add an Pending Intent via setContentIntent(pendingIntent).
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Trip ended")
                .setContentText("Trip was Ended Early.");
        int mNotificationId = 002;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(mNotificationId, mBuilder.build());
    }

    private void setUpAndStartChooseDestinationDialog() {
        SelectAddressDialog selectAddressDialog = new SelectAddressDialog();
        selectAddressDialog.setmListener(this);
        selectAddressDialog.show(getFragmentManager(), "selectAddressDialog");
    }

    private void setUpAndStartChooseRiderDialog() {
        SelectPhoneNumberDialog selectNumberDialog = new SelectPhoneNumberDialog();
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
        myIntent.putExtra(TextMessageIntentService.FRIENDS_LOCATION, friendsLocation);
        myIntent.putExtra(TextMessageIntentService.FRIENDS_PHONE_NUMBER, friendsPhoneNumber);
        myIntent.putExtra(TextMessageIntentService.FRIENDS_NAME, friendsName);
        myIntent.putExtra(TextMessageIntentService.FRIENDS_ADDRESS, friendsAddress);
        myIntent.putExtra(TextMessageIntentService.DISTANCE_TO_NOTIFY, destinationRadius);
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
    public void onDialogSelectPhoneNumberPositiveClick(FriendObject myFriend) {
        Toast.makeText(this, "Name: " + myFriend.getName(), Toast.LENGTH_SHORT).show();
        phoneNumberTextView.setText("Phone Number entered: " + myFriend.getPhoneNumber());
        friendsPhoneNumber = myFriend.getPhoneNumber();
        friendsName = myFriend.getName();
    }

    @Override
    public void onDialogSelectDestinationPositiveClick(String address, String distance, float radius) {
        friendsAddress = address;
        destinationRadius = radius;
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
                } else {
                    loadResponseFromOpenCageApi(response);
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failure!", Toast.LENGTH_SHORT).show();
                //Log.e(TAG, t.toString());
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
          resultFromResponse = response.body();
        ApiResults apiResults_ = resultFromResponse.getResults().get(0);
        return apiResults_.getGeometry();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        if(PermissionRequest.checkPermissionsForActionBar(this)){
            menu.findItem(R.id.permissions_check).setIcon(R.mipmap.check_mark_3);
        }else {
            menu.findItem(R.id.permissions_check).setIcon(R.mipmap.error_mark_3);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.help_item:
                buildAlertDialog("help");
                return true;
            case R.id.info_item:
                buildAlertDialog("info");
                return true;
            case R.id.permissions_check:
                String msg = "This signals if you have granted us all necessary permissions";
                Toast.makeText(this, msg , Toast.LENGTH_SHORT).show();
                return true;
            case R.id.set_message:
                selectTextMessageToSend();
                return true;
            case R.id.tutorial_item:
                playTutorial();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void playTutorial() {
        MyTourGuide.runTourGuide(this);
    }

    private void selectTextMessageToSend() {
        TextMsgToSendDialog dialog = new TextMsgToSendDialog();
        dialog.show(getFragmentManager(), "select-txt-dialog");
    }

    private void buildAlertDialog(String choice) {
        String message = "";
        if(choice.equals("help")){
            message = "Format the address correctly, such as this: \n" +
                    "123 Fake St, Dallas, TX 78496\n\n"+
                    "Separating the street, city, and state/zip with commas.\n\n" +
                    "Tip: Enter address in Google, & copy Google's result.";
        }else{
            message = "More information coming soon";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(choice.equals("help")? "Help": "Info");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    interface OpenCageApiService {
        //okay, since I am encoding the string myself, I need to include "encode = true".
        @GET("json")
        Call<Result> getGeoCodedLocation(@Query("key") String apiKey,
                                         @Query(value = "q", encoded = true) String addressToQuery);
    }

}