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

/**
 * Created by nick on 12/15/2017.
 */

public class TextMessageBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = TextMessageBroadcastReceiver.class.getSimpleName();
    String phoneNumberToListenFor;
    private static final String DISTANCE = "Distance";
    IntentServiceListener myListner;

    public TextMessageBroadcastReceiver(String phoneNumber, IntentServiceListener listner) {
        super();
        myListner = listner;
        phoneNumberToListenFor = phoneNumber;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)){
            checkThroughIntent(intent);
            Log.v(TAG, "Msg receivede to Receiver:");
        }
    }

    void checkThroughIntent(Intent intent){
        String sender = "";
        String smsBody = "";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for(SmsMessage smsMsg: Telephony.Sms.Intents.getMessagesFromIntent(intent)){
                sender = smsMsg.getDisplayOriginatingAddress();
                smsBody += smsMsg.getMessageBody();
            }
        }else{
            Bundle smsBundle = intent.getExtras();
            if(smsBundle != null){
                Object[] pdus = (Object[]) smsBundle.get("pdus");
                if(pdus == null){
                    Log.v(TAG, "SmsBundle has no pdus key");
                    return;
                }
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for(int i = 0; i < messages.length; i++){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        messages[i] = SmsMessage
                                .createFromPdu( (byte[]) pdus[i], smsBundle.getString("format") );
                        smsBody += messages[i].getMessageBody();
                    }else{
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    sender = messages[0].getOriginatingAddress();

                }
            }
        }
        Log.v(TAG, "Sender Number is: " + sender);
        if(sender.equals(phoneNumberToListenFor) && smsBody.startsWith(DISTANCE)){
            respondWithDistanceText();
        }
    }

    private void respondWithDistanceText() {
        String distance = Float.toString(getLastDistance());
        String msg = "I am currently " + distance + " miles away from you. This text sent automatically";
        SmsManager.getDefault().sendTextMessage(TextMessageIntentService.myNumber, null, msg, null, null);
    }

    public float getLastDistance() {
        float metersConvertedToMiles = (float) (myListner.getLastMeasuredDistance() * 0.000621371192);
        return metersConvertedToMiles;
    }

    public interface IntentServiceListener {
        float getLastMeasuredDistance();
    }
}
