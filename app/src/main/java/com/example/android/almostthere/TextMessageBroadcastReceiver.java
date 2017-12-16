package com.example.android.almostthere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.DecimalFormat;

/**
 * Created by nick on 12/15/2017.
 */
public class TextMessageBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = TextMessageBroadcastReceiver.class.getSimpleName();
    private static final String DISTANCE = "Distance";
    FriendObject myFriendObject;
    IntentServiceListener myListener;

    public TextMessageBroadcastReceiver(FriendObject friendObject, IntentServiceListener listener) {
        super();
        myListener = listener;
        myFriendObject = friendObject;
        friendObject.setPhoneNumber(extractDigitsFromNumber(friendObject.getPhoneNumber()));
    }

    private String extractDigitsFromNumber(String phoneNumber) {
        String number = "";
        for(int i = 0; i < phoneNumber.length(); i++){
            if(Character.isDigit(phoneNumber.charAt(i))){
                number += phoneNumber.charAt(i);
            }
        }
        Log.v(TAG, "Reconstructed Number is: " + number);
        return number;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            parseIntentForSenderAndTextMsg(intent);
            Log.v(TAG, "Msg received to BroadcastReceiver.");
            Log.v(TAG, "Will compare to this phone number: " + myFriendObject.getPhoneNumber());
        }
    }

    void parseIntentForSenderAndTextMsg(Intent intent) {
        String sender = "";
        String smsBody = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for (SmsMessage smsMsg : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                sender = smsMsg.getDisplayOriginatingAddress();
                smsBody += smsMsg.getMessageBody();
            }
        } else {
            Bundle smsBundle = intent.getExtras();
            if (smsBundle != null) {
                Object[] pdus = (Object[]) smsBundle.get("pdus");
                if (pdus == null) {
                    Log.v(TAG, "SmsBundle has no pdus key");
                    return;
                }
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < messages.length; i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        messages[i] = SmsMessage
                                .createFromPdu((byte[]) pdus[i], smsBundle.getString("format"));
                        smsBody += messages[i].getMessageBody();
                    } else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    sender = messages[0].getOriginatingAddress();
                }
            }
        }
        Log.v(TAG, "Sender Number is: " + sender);
        if (sender.equals(myFriendObject.getPhoneNumber()) && smsBody.startsWith(DISTANCE)) {
            respondWithDistanceText();
        }
    }

    private void respondWithDistanceText() {
        String distance = Double.toString(getLastDistance());
        String msg = "I am currently " + distance + " miles away from you. " +
                "(This text sent automatically via AlmostThere app)";
        SmsManager.getDefault().sendTextMessage(myFriendObject.getPhoneNumber(), null, msg, null, null);
        Log.v(TAG, "Distance text sent to: " + myFriendObject.getName());
    }

    public Double getLastDistance() {
        Double metersConvertedToMiles =  myListener.getLastMeasuredDistance() * 0.000621371192;
        DecimalFormat df = new DecimalFormat("#.##");
        metersConvertedToMiles = Double.valueOf(df.format(metersConvertedToMiles));
        return metersConvertedToMiles;
    }

    public interface IntentServiceListener {
        float getLastMeasuredDistance();
    }
}
