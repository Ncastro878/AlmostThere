package com.example.android.almostthere;

import android.location.Location;

/**
 * Created by nick on 12/16/2017.
 */

public class FriendObject {
    String Name;
    String PhoneNumber;
    Location location;
    String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public boolean areLocationAndNumberInitialized() {
        return (PhoneNumber == null && location == null);
    }
}
