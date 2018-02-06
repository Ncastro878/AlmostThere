package com.nickrcastro.android.almostthere;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Created by nick on 12/13/2017.
 */

public class TextMessageIntentService extends Service implements TextMessageBroadcastReceiver.IntentServiceListener {
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 sec */
    private float METERS_IN_A_MILE = 1609;

    private String TAG = TextMessageIntentService.class.getSimpleName();
    public static  String DISTANCE_TO_NOTIFY = "distanceToNotify";
    public static String START_LOCATION_UPDATES = "startLocationUpdates";
    public static String STOP_SERVICE_FROM_FOREGROUND = "stopFromForeground";
    public static String FRIENDS_LOCATION = "friendsLocation";
    public static String FRIENDS_PHONE_NUMBER = "friendsPhoneNumber";
    public static String FRIENDS_ADDRESS = "friendsAddress";
    public static String FRIENDS_NAME = "friendsName";
    private static final String CHANNEL_ID = "channel_01";
    private FriendObject myFriendObject = new FriendObject();

    private Location myCurrentLocation = new Location("");
    private static final int NOTIFICATION_ID = 123456;

    public static String myNumber = "9402574628";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private TextMessageBroadcastReceiver mTextMsgBroadcastReceiver;
    private float lastCalculatedDistance;

    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationProviderClient = getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //Log.v(TAG, "onLocationResult() called.");
                onLocationChanged(locationResult.getLastLocation());
            }
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                //Log.v(TAG, "Location availability?: " + locationAvailability.isLocationAvailable());
            }
        };
    }

    private void initAndRegisterTextMsgReceiver() {
        mTextMsgBroadcastReceiver =
                new TextMessageBroadcastReceiver(myFriendObject, this);
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(mTextMsgBroadcastReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(START_LOCATION_UPDATES)) {
            Location location = intent.getParcelableExtra(FRIENDS_LOCATION);
            String phoneNumber = intent.getStringExtra(FRIENDS_PHONE_NUMBER);
            String name = intent.getStringExtra(FRIENDS_NAME);
            String address = intent.getStringExtra(FRIENDS_ADDRESS);
            float distance = intent.getFloatExtra(DISTANCE_TO_NOTIFY, 1);
            initializeMyFriendObject(location, name, phoneNumber, address, distance);
            initAndRegisterTextMsgReceiver();
            startLocationUpdates();
            Toast.makeText(this, "Trip started!", Toast.LENGTH_SHORT).show();
        } else if (action.equals(STOP_SERVICE_FROM_FOREGROUND)) {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private void initializeMyFriendObject(Location location, String name,
                                          String phoneNumber, String address, float distance){
        myFriendObject.setLocation(location);
        myFriendObject.setName(name);
        myFriendObject.setPhoneNumber(phoneNumber);
        myFriendObject.setAddress(address);
        myFriendObject.setDistance(distance);
    }

    public void startLocationUpdates() {
        //Log.v(TAG, "First call to location updates.");
        startLocationUpdatesFromInside();
        showForegroundNotification();
    }

    //uses this tutorial:
    // https://github.com/codepath/android_guides/wiki/Retrieving-Location-with-LocationServices-API
    public void startLocationUpdatesFromInside() {
        //Log.v(TAG, "Second call to location updates");
        //Log.v(TAG, "Friends Location latitude is: " + myFriendObject.getLocation().getLatitude());

        mLocationRequest = createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //Log.v(TAG, "Location Permission not granted");
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, Looper.myLooper());
    }

    private LocationRequest createLocationRequest() {
        LocationRequest lr = new LocationRequest();
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        lr.setInterval(UPDATE_INTERVAL);
        lr.setFastestInterval(FASTEST_INTERVAL);
        return lr;
    }

    private void onLocationChanged(Location lastLocation) {
        Boolean isOnMainThread = Looper.myLooper() == Looper.getMainLooper();
        Double lastLatitude = lastLocation.getLatitude();
        Double lastLongitude = lastLocation.getLongitude();
        myCurrentLocation.setLatitude(lastLatitude);
        myCurrentLocation.setLongitude(lastLongitude);
        String locationString = String.format("Your location: %f, %f",
                myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
        //Log.v(TAG, locationString);
        if (myFriendObject.getLocation() != null) {
            checkDistanceBetweenYouAndFriend();
        }
    }

    private void checkDistanceBetweenYouAndFriend() {
        float distanceBetween = myCurrentLocation.distanceTo(myFriendObject.getLocation());
        lastCalculatedDistance = distanceBetween;
        if (distanceBetween < (METERS_IN_A_MILE * myFriendObject.getDistance() )) {
            Toast.makeText(this, "You are within distance of friends location.", Toast.LENGTH_SHORT).show();
            sendTextMessageToFriend(myFriendObject.getPhoneNumber());
        }
    }

    private void sendTextMessageToFriend(String friendsPhoneNumber) {
        //Log.v(TAG, "Sending text message to friend: " + myFriendObject.getName());
        String newMsg = "I am nearing your location. (Now ending trip and automatic text notifications)";
        Toast.makeText(this, "Notifying: " + friendsPhoneNumber, Toast.LENGTH_SHORT).show();
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(friendsPhoneNumber, null, newMsg, null, null);
        showEndOfTripNotification();
        stopServiceAndLocationUpdates();
    }

    private void showEndOfTripNotification() {
        //Create and add an Pending Intent via setContentIntent(pendingIntent).
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("End of trip")
                .setContentText("Notification text has been sent to " + myFriendObject.getName());
        int mNotificationId = 001;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(mNotificationId, mBuilder.build());
    }

    private void stopServiceAndLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        switchStartAndStopButtonsViaIntent();
        stopForeground(true);
        stopSelf();
        //Log.v(TAG, "Stopping background textMsg service");
    }

    private void switchStartAndStopButtonsViaIntent() {
        Intent intent = new Intent("destination-reached");
        intent.putExtra("change-button","true");
        Log.v("TextMessageIntent", "Broadcast sent!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void showForegroundNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Notification notification = buildNotification(icon, pendingIntent);
        startForeground(NOTIFICATION_ID, notification);
    }

    private Notification buildNotification(Bitmap icon, PendingIntent pendingIntent) {
        NotificationCompat.Action stopServiceAction = buildStopServiceAction();
        String notifyString = "Will notify " + myFriendObject.getName() + " as you near destination.";
        Notification newNotification = new NotificationCompat.Builder(this)
                .setContentTitle("Trip started: " + myFriendObject.getAddress())
                .setContentText(notifyString)
                .setSmallIcon(R.mipmap.ic_launcher)
                .addAction(stopServiceAction)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
        return newNotification;
    }

    private NotificationCompat.Action buildStopServiceAction() {
        Intent stopIntent = new Intent(this, TextMessageIntentService.class);
        stopIntent.setAction(STOP_SERVICE_FROM_FOREGROUND);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, stopIntent, 0);
        NotificationCompat.Action.Builder builder =
                new NotificationCompat.Action.Builder(R.mipmap.ic_launcher_round, "End Trip", pendingIntent);
        return builder.build();
    }

    @Override
    public void onDestroy() {
        //TODO PROBLEM.
        unregisterReceiver(mTextMsgBroadcastReceiver);
        //Toast.makeText(this, "Service has been destroyed.", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public float getLastMeasuredDistance() {
        return lastCalculatedDistance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {return null;}
}
