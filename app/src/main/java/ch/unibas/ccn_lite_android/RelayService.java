package ch.unibas.ccn_lite_android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Random;

/**
 * Created by adrian on 2016-10-03.
 */
public class RelayService extends Service{
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        RelayService getService() {
            // Return this instance of RelayService so clients can call public methods
            return RelayService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startRely() {
        relayInit();
    }

    public String startAndroidPeek(String ipString, int portInt, String contentString) {
        return androidPeek(ipString, portInt, contentString);
    }


    // JNI declarations
    public native String relayInit();

    public native String androidPeek(String ipString, int portString, String contentString);

    static {
        System.loadLibrary("ccn-lite-android");
    }
}
