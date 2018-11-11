package com.shakepoint.mobile;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ShakepointFirebaseMessagingService extends FirebaseMessagingService {
    public ShakepointFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("FirebaseService", "Received push notification");
    }
}
