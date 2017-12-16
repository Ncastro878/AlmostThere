package com.example.android.almostthere;

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
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
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
    public static String START_LOCATION_UPDATES = "startLocationUpdates";
    public static String STOP_SERVICE_FROM_FOREGROUND = "stopFromForeground";
    public static String FRIENDS_LOCATION = "friendsLocation";

    private Location myCurrentLocation = new Location("");
    private static final int NOTIFICATION_ID = 123456;
    private static final String CHANNEL_ID = "channel_01";

    private Location friendsLocation = new Location("x");
    public static String myNumber = "9402574628";
    private String friendsPhoneNumber;
    private LocalBinder mBinder = new LocalBinder();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private TextMessageBroadcastReceiver mTextMsgBroadcastReceiver;

    /**
     * New Variables for making this service work in
     **/
    private Handler mServiceHandler;
    private float lastCalculatedDistance;

    @Override
    public void onCreate() {
        super.onCreate();
        initAndRegisterTextMsgReceiver();
        mFusedLocationProviderClient = getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.v(TAG, "onLocationResult() called.");
                onLocationChanged(locationResult.getLastLocation());
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Log.v(TAG, "Location availability?: " + locationAvailability.isLocationAvailable());
            }
        };
        /*
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        Log.v(TAG, "Creating thread:" + handlerThread.toString());
        */
    }

    private void initAndRegisterTextMsgReceiver() {
        mTextMsgBroadcastReceiver = new TextMessageBroadcastReceiver(myNumber, this);
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(mTextMsgBroadcastReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action == START_LOCATION_UPDATES) {
            Location location = intent.getParcelableExtra(FRIENDS_LOCATION);
            setFriendsLocation(location);
            startLocationUpdates();
            Toast.makeText(this, "Service started!", Toast.LENGTH_SHORT).show();
        } else if (action == STOP_SERVICE_FROM_FOREGROUND) {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    public void startLocationUpdates() {
        Log.v(TAG, "First call to location updates.");
        //startService(new Intent(getApplicationContext(), TextMessageIntentService.class));
        startLocationUpdatesFromInside();
        showForegroundNotification();
    }

    //uses this tutorial:
    // https://github.com/codepath/android_guides/wiki/Retrieving-Location-with-LocationServices-API
    public void startLocationUpdatesFromInside() {
        Log.v(TAG, "Second call to location updates");
        Log.v(TAG, "Friends Location latitude is: " + friendsLocation.getLatitude());

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
            Log.v(TAG, "Location Permission not granted");
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
        Log.v(TAG, "\nAm I on main Thread? : " + isOnMainThread);
        Double lastLatitude = lastLocation.getLatitude();
        Double lastLongitude = lastLocation.getLongitude();
        myCurrentLocation.setLatitude(lastLatitude);
        myCurrentLocation.setLongitude(lastLongitude);
        String locationString = String.format("Your location: %f, %f",
                myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
        Log.v(TAG, locationString);
        if (friendsLocation != null) {
            checkDistanceBetweenYouAndFriend();
        }
    }

    private void checkDistanceBetweenYouAndFriend() {
        Log.v(TAG, "Checking distance between you and friend. ");
        float distanceBetween = myCurrentLocation.distanceTo(friendsLocation);
        lastCalculatedDistance = distanceBetween;
        if (distanceBetween < METERS_IN_A_MILE) {
            Toast.makeText(this, "You are within distance of friends location.", Toast.LENGTH_SHORT).show();
            sendTextMessage(friendsPhoneNumber);
        }
    }

    private void sendTextMessage(String friendsPhoneNumber) {
        Log.v(TAG, "Sending text message to friend.");
        String newMsg = "I am nearing your location";
        Toast.makeText(this, "Notifying: " + friendsPhoneNumber, Toast.LENGTH_SHORT).show();
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(myNumber, null, newMsg, null, null);
        showEndOfTipNotification();
        stopServiceAndLocationUpdates();
    }

    private void showEndOfTipNotification() {
        //Create and add an Pending Intent via setContentIntent(pendingIntent).
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("End of trip")
                .setContentText("Trip has ended. Text has been sent to ___");
        int mNotificationId = 001;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(mNotificationId, mBuilder.build());
    }

    private void stopServiceAndLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        stopForeground(true);
        stopSelf();
        Log.v(TAG, "Stopping background textMsg service");
    }

    public void setFriendsLocation(Location friendsLocation) {
        this.friendsLocation = friendsLocation;
    }


    private Notification getNotification() {
        return null;
    }

    private void showForegroundNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Notification notification = buildNotification(icon, pendingIntent);
        startForeground(NOTIFICATION_ID, notification);
    }

    private Notification buildNotification(Bitmap icon, PendingIntent pendingIntent) {
        NotificationCompat.Action stopServiceAction = buildStopAction();
        Notification newNotification = new NotificationCompat.Builder(this)
                .setContentTitle("ContentTitle")
                .setTicker("TickerTitle")
                .setContentText("ContentText")
                .setSmallIcon(R.mipmap.ic_launcher)
                .addAction(stopServiceAction)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
        return newNotification;
    }

    private NotificationCompat.Action buildStopAction() {
        Intent stopIntent = new Intent(this, TextMessageIntentService.class);
        stopIntent.setAction(STOP_SERVICE_FROM_FOREGROUND);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, stopIntent, 0);
        NotificationCompat.Action.Builder builder =
                new NotificationCompat.Action.Builder(R.mipmap.ic_launcher_round, "Stop Trip", pendingIntent);
        return builder.build();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mTextMsgBroadcastReceiver);
        Toast.makeText(this, "Service has been destroyed.", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public float getLastMeasuredDistance() {
        return lastCalculatedDistance;
    }

    public class LocalBinder extends Binder {
        TextMessageIntentService getService() {
            return TextMessageIntentService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
