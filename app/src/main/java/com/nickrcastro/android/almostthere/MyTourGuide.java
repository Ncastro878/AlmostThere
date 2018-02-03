package com.nickrcastro.android.almostthere;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

/**
 * Created by nick on 2/2/2018.
 */

public class MyTourGuide {

    public static void runTourGuide(Activity mainActivity){
        /* setup enter and exit animation */
        Animation mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        Animation mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);
        // the return handler is used to manipulate the cleanup of all the tutorial elements
        ChainTourGuide tourGuide1 = ChainTourGuide.init(mainActivity)
                .setToolTip(new ToolTip()
                        .setTitle("Choose a Rider")
                        .setDescription("Select the top button and enter Rider's phone number.")
                        .setGravity(Gravity.TOP)
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(mainActivity.findViewById(R.id.enter_rider_info_button));

        ChainTourGuide tourGuide2 = ChainTourGuide.init(mainActivity)
                .setToolTip(new ToolTip()
                        .setTitle("Enter their address")
                        .setDescription("Enter their address or choose a previously entered address.")
                        .setGravity(Gravity.BOTTOM )
                        .setBackgroundColor(Color.parseColor("#c0392b"))
                )
                .setOverlay(new Overlay()
                        .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .playLater( mainActivity.findViewById(R.id.enter_destination_info_button));


        ChainTourGuide tourGuide3 = ChainTourGuide.init(mainActivity)
                .setToolTip(new ToolTip()
                        .setTitle("Verify Address")
                        .setDescription("Verify the address on another Map app")
                        .setGravity(Gravity.TOP)
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(mainActivity.findViewById(R.id.map_icon));

        ChainTourGuide tourGuide4 = ChainTourGuide.init(mainActivity)
                .setToolTip(new ToolTip()
                        .setTitle("Start Trip")
                        .setDescription("Press Start Trip to begin trip.(Start Trip button will change to End Trip button)")
                        .setGravity(Gravity.TOP)
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(mainActivity.findViewById(R.id.start_trip_button));

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2, tourGuide3, tourGuide4)
                .setDefaultOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.OVERLAY)
                .build();


        ChainTourGuide.init(mainActivity).playInSequence(sequence);
    }

}
