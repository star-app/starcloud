package com.owncloud.android.ui.preview;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ShareScreenService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        
        intent.getStringExtra("");
        
        return null;
    }

}
