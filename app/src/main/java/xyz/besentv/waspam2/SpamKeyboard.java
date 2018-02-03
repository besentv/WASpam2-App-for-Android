package xyz.besentv.waspam2;

/**
 * Created by besentv on 22.09.2016.
 */

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;


public class SpamKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener

{

    private KeyboardView inputView;
    private Keyboard spamKeyboard;
    private Keyboard spamConfirmKeyboard;
    public static InputConnection ic;
    private boolean isSpamming = false;
    private int spamAmount;
    private InputMethodManager inputMethodManager;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder nBuilder;
    private int notifyID = 1;
    private JSONObject settingsJSON;
    private SettingsFileLoader settingsFileLoader;
    private List<Keyboard.Key> spamKeyboardKeys;


    public SpamKeyboard() {

    }

    @Override
    public void onCreate() {
        inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        super.onCreate();

    }

    @Override
    public View onCreateInputView() {
        super.onCreateInputView();
        inputView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        spamKeyboard = new Keyboard(this, R.xml.keyboard_spam);
        spamKeyboardKeys = spamKeyboard.getKeys();
        spamConfirmKeyboard = new Keyboard(this, R.xml.keyboard_confirmspam);
        inputView.setOnKeyboardActionListener(this);
        inputView.setKeyboard(spamKeyboard);
        inputView.setPreviewEnabled(false);
        nBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Spammed Messages:").setContentText("").setOngoing(true);
        settingsFileLoader = new SettingsFileLoader(this);
        if (getSettings()) {
            try {
                Settings.spamMessage = settingsJSON.getString(Settings.spamMessageStateString);
                Settings.spamDelay = settingsJSON.getInt(Settings.spamDelayStateString);
                Settings.spamAmount = settingsJSON.getInt(Settings.spamAmountStateString);
                Settings.needSpamConfirmation = settingsJSON.getBoolean(Settings.needSpamConfirmationStateString);
                JSONArray recentMessagesJSONArray = settingsJSON.getJSONArray(Settings.recentMessagesStateString);
                for(int i=0; i < Settings.recentMessages.length; i++){
                    Settings.recentMessages[i] = (String) recentMessagesJSONArray.get(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < spamKeyboardKeys.size(); i++) {
            switch (spamKeyboardKeys.get(i).codes[0]) {
                case 10:
                    setRecentMessageForKey(spamKeyboardKeys.get(i),0);
                    break;
                case 11:
                    setRecentMessageForKey(spamKeyboardKeys.get(i),1);
                    break;
                case 12:
                    setRecentMessageForKey(spamKeyboardKeys.get(i),2);
                    break;
            }
        }
        return inputView;
    }


    private boolean getSettings() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File dir = new File(Settings.settingsFilePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        try {
            settingsJSON = settingsFileLoader.loadSettingsFile();
            if (settingsJSON == null) {
                settingsJSON = new JSONObject();
            } else {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void rotateRecentMessages(String newMessage) {
        for(int i=0; i < Settings.recentMessages.length; i++){
            if(Settings.recentMessages[i].compareTo(newMessage) == 0){
                return;
            }
        }
        Settings.recentMessages[2] = Settings.recentMessages[1];
        Settings.recentMessages[1] = Settings.recentMessages[0];
        Settings.recentMessages[0] = newMessage;
        for (int i = 0; i < spamKeyboardKeys.size(); i++) {
            switch (spamKeyboardKeys.get(i).codes[0]) {
                case 10:
                    setRecentMessageForKey(spamKeyboardKeys.get(i),0);
                    break;
                case 11:
                    setRecentMessageForKey(spamKeyboardKeys.get(i),1);
                    break;
                case 12:
                    setRecentMessageForKey(spamKeyboardKeys.get(i),2);
                    break;
            }
        }
    }

    private void setRecentMessageForKey(Keyboard.Key key, int recentMessagePosition){
        if (Settings.recentMessages[recentMessagePosition].length() > 0) {
            if (Settings.recentMessages[recentMessagePosition].length() > 7) {
                String keyLabel = "\"" + Settings.recentMessages[recentMessagePosition].substring(0, 7) + "...\"";
                key.label = keyLabel;
            } else {
                key.label = Settings.recentMessages[recentMessagePosition];
            }
        }
    }

    private void spam() {
        if (!isSpamming) {
            isSpamming = true;
            spamAmount = Settings.spamAmount;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 1; i <= spamAmount; i++) {
                            ic = getCurrentInputConnection();
                            ic.commitText(Settings.spamMessage, 1);
                            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                            nBuilder.setContentText(i + " of " + spamAmount);
                            notificationManager.notify(notifyID, nBuilder.build());
                            Thread.sleep(Settings.spamDelay);
                        }
                        notificationManager.cancel(notifyID);
                        isSpamming = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        isSpamming = false;
                    }

                }
            }).start();
            notificationManager.cancel(notifyID);
        }
        else if (isSpamming) {
            Toast.makeText(getApplicationContext(), "Spam Thread is running! Please wait for it to end.", Toast.LENGTH_SHORT).show();
        }
    }


    private void replaceSpamMessage(String msg){
        Settings.spamMessage = msg;
        if(msg.length() > 10){
            Toast.makeText(getApplicationContext(), "Set spam message to:\"" + Settings.spamMessage.substring(0,10) + "...\"", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Set spam message to:\"" + Settings.spamMessage + "\"", Toast.LENGTH_SHORT).show();
        }
        rotateRecentMessages(msg);
    }


    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        switch (primaryCode) {
            case 1:
                if (Settings.needSpamConfirmation && !isSpamming) {
                    inputView.setKeyboard(spamConfirmKeyboard);
                    return;
                } else
                    spam();
                break;
            case 2:
                Intent settings = new Intent(SpamKeyboard.this, Settings.class);
                settings.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(settings);
            case 3:
                spamAmount = 1;
                break;
            case 4:
                inputMethodManager.showInputMethodPicker();
                break;
            case 5:
                inputView.setKeyboard(spamKeyboard);
                spam();
                break;
            case 6:
                inputView.setKeyboard(spamKeyboard);
                break;
            case 7:
                InputConnection inputConnection = getCurrentInputConnection();
                CharSequence text;
                /*0x0102001f = select all*/
                inputConnection.performContextMenuAction(0x0102001f);
                text = inputConnection.getSelectedText(0);
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                if (text != null) {
                    Settings.spamMessage = text.toString();
                    rotateRecentMessages(text.toString());
                    inputView.setKeyboard(spamKeyboard);
                    Log.d("WASpam2", "Inputconn String:" + text.toString());
                }
                break;
            case 10:
                replaceSpamMessage(Settings.recentMessages[0]);
                break;
            case 11:
                replaceSpamMessage(Settings.recentMessages[1]);
                break;
            case 12:
                replaceSpamMessage(Settings.recentMessages[2]);
                break;
            default:
                return;
        }
    }

    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
        inputView.setKeyboard(spamKeyboard);
        spamAmount = 1;
        try {
            settingsJSON.put(Settings.spamMessageStateString, Settings.spamMessage);
            settingsJSON.put(Settings.spamAmountStateString, Settings.spamAmount);
            settingsJSON.put(Settings.spamDelayStateString, Settings.spamDelay);
            settingsJSON.put(Settings.needSpamConfirmationStateString, Settings.needSpamConfirmation);
            settingsJSON.put(Settings.recentMessagesStateString, new JSONArray(Settings.recentMessages));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        settingsFileLoader.writeSettingsFile(settingsJSON);
    }


    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
