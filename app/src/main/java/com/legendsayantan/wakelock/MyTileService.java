package com.legendsayantan.wakelock;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.service.quicksettings.Tile;
import android.widget.Toast;


public class MyTileService extends android.service.quicksettings.TileService {
    public static Tile tile ;
    Boolean overlay;

    private SharedPreferences prefs;

    @Override
    public void onStartListening() {
        super.onStartListening();
        MyTileService.tile=getQsTile();
        updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        MyTileService.tile=getQsTile();
        boolean isEnabled=
                getPrefs()
                        .getBoolean("run", false);

        getPrefs()
                .edit()
                .putBoolean("run", !isEnabled)
                .commit();
        updateTile();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
    }

    private void updateTile() {
        Tile tile=getQsTile();
        MyTileService.tile=getQsTile();

        if (tile!=null) {
            boolean isEnabled=
                    getPrefs()
                            .getBoolean("run", false);
            int state=isEnabled ?
                    Tile.STATE_ACTIVE :
                    Tile.STATE_INACTIVE;

            if(MainActivity.enabled)state=Tile.STATE_ACTIVE;

            tile.setIcon(Icon.createWithResource(this,
                    R.drawable.icon));
            tile.setLabel(getString(R.string.app_name));
            tile.setState(state);
            if(isEnabled) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.setSubtitle("enabled");
            }
            if(!isEnabled) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.setSubtitle("disabled");
            }
            tile.updateTile();
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),Floating.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getOverlayPerm();

            if(overlay)
                if(isEnabled){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                        startService(intent);
                }
            }else{
                    stopService(intent);
            }
        }
    }

    private SharedPreferences getPrefs() {
        if (prefs==null) {
            prefs= PreferenceManager.getDefaultSharedPreferences(this);
        }
        return(prefs);
    }
    public void enableFromActivity(){
        tile.setState(Tile.STATE_ACTIVE);
    }
    public void getOverlayPerm() {
        checkOverlay();
        if (!overlay) {
            tile.setState(Tile.STATE_INACTIVE);
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            Toast.makeText(getApplicationContext(), "Find WakeLock and enable overlay permission to use WakeLock", Toast.LENGTH_LONG).show();
        }
        checkOverlay();
    }

    public void checkOverlay() {
        overlay = Settings.canDrawOverlays(getApplicationContext());
        System.out.println(Settings.canDrawOverlays(getApplicationContext()));
    }
}
