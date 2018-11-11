package com.shakepoint.mobile;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.shakepoint.mobile.util.SharedUtils;

public class ShakepointFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public ShakepointFirebaseInstanceIDService() {
    }

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        SharedUtils.setFCMToken(this, token);
        Log.i("InstanceIdService", "Saved new FCM token");
    }


}
