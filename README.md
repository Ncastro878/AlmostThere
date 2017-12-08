# AlmostThere
Notifies a contact via text when you are close to their gps location

This app is another result of hearing the advice "Build something you would find useful/solves a problem you have".

How it functions: It will allow you to enter in a phone number or select a phone number from your list of contacts that corresponds to the person you will send a text notification to. You will then enter in the address where that person is located. You will then select a distance from a list of distances(".5 mile, 1 mile, 100 yards, etc"). You then press start. Your phone will make recurring location updates of your current location. When you are within range of the distance you selected to the address you entered, a text message will be sent to your friend letting them know approximately how far away you are from them so they can be ready for your arrival and you don't have to handle your phone while driving."

**< Background Info>**(Not coding-related, feel free to skip)

So, one inconvenience I sought to fix involves the situation of picking up my sister from work. Basically, half the time she was never ready/outside waiting when I would pull up or I would take a while and she would send texts seeing if I was still coming. It would be nice to text her when I was just a few blocks away to be outside ready and waiting so I wouldn't have to wait for her to finish up her convos or whatever it is she did that made me wait. But a new local law forbids texting & driving, and even before that it's a risky thing to try and text her that I'm close by while driving. So I thought it'd be nice if my phone would text her for me when I was, say, 100 yards away, so that she could be certain I was nearby and I wouldn't have to even touch my phone or wait to park to notify her. 

This was my very first app idea and when I first tried to build it after 2 days of research I frustratingly gave up because I was still very inexperienced with the Android System as well as other concepts such as Location Updates and Network calls, not to mention finding a reliable geocoding service(Google's geocoder was very unreliable when I tried). It was a very good feeling to revisit the idea out of boredom and see that essentially all the concepts that tripped me up on my first program attempt were now well within my reach. Maybe no one will find that all that useful but it feels good tackling one of my first failed attempts. 

**</Background Info>**

Concepts/Libraries/Services used and learned building this:

-OpenCageData GeoCoder service

-Retrofit for Network calls to OpenCageData API

-Permission Request for Dangerous permissions
