package xyz.besentv.waspam2;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Bernhard on 25.10.2016.
 */

public class SpamService extends IntentService {

    public SpamService(){

        super("WASpam2SettingsService");

    }


    @Override
    public void onHandleIntent(Intent intent){

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        return super.onStartCommand(intent,flags,startId);
    }

}
