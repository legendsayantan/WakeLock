package com.legendsayantan.wakelock;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import static com.legendsayantan.wakelock.MainActivity.editor;

public class Floating extends Service {
    public static ViewGroup floatView;
    private int LAYOUT_TYPE;
    public static Boolean isEnabled;
    public static WindowManager.LayoutParams floatWindowLayoutParam,param2;
    public static WindowManager windowManager;
    public static MainActivity main;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("Extra Key Value"+ intent.getStringExtra("extra"));
        return null;
    }
    @Override

    public void onCreate() {
        System.out.println("Started floating");

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            startMyOwnForeground();
        }
        Toast.makeText(getApplicationContext(),"Yellow dot on the top right means\nWakeLock is Active",Toast.LENGTH_LONG).show();

        super.onCreate();




        // The screen height and width are calculated, cause

        // the height and width of the floating window is set depending on this


        // To obtain a WindowManager of a different Display,

        // we need a Context for that display, so WINDOW_SERVICE is used

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        // A LayoutInflater instance is created to retrieve the
        // LayoutInflater for the floating_layout xml

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        // inflate a new view hierarchy from the floating_layout xml

        floatView = (ViewGroup) inflater.inflate(R.layout.floating, null);

        // The Buttons and the EditText are connected with

        // the corresponding component id used in floating_layout xml file



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // If API Level is more than 26, we need TYPE_APPLICATION_OVERLAY
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        } else {

            // If API Level is lesser than 26, then we can

            // use TYPE_SYSTEM_ERROR,

            // TYPE_SYSTEM_OVERLAY, TYPE_PHONE, TYPE_PRIORITY_PHONE.

            // But these are all

            // deprecated in API 26 and later. Here TYPE_TOAST works best.

            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST;

        }



        // Now the Parameter of the floating-window layout is set.

        // 3) Layout_Type is already set.

        // 4) Next Parameter is Window_Flag. Here FLAG_NOT_FOCUSABLE is used. But

        // problem with this flag is key inputs can't be given to the EditText.

        // This problem is solved later.

        // 5) Next parameter is Layout_Format. System chooses a format that supports

        // translucency by PixelFormat.TRANSLUCENT
        floatWindowLayoutParam = new WindowManager.LayoutParams(
                20,
                20,
                LAYOUT_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        // The Gravity of the Floating Window is set.
        // The Window will appear in the center of the screen

        floatWindowLayoutParam.gravity = Gravity.TOP;
        floatWindowLayoutParam.x=(metrics.widthPixels/2)-20;
        //floatWindowLayoutParam.y=height;
        // X and Y value of the window is set
        // The ViewGroup that inflates the floating_layout.xml is
        // added to the WindowManager with all the parameters
        windowManager.addView(floatView, floatWindowLayoutParam);
    }
    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "Stopped WakeLock", Toast.LENGTH_SHORT).show();
        System.out.println("onDestroy");
        windowManager.removeView(floatView);
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "com.legendsayantan.wakelock.active";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }
}