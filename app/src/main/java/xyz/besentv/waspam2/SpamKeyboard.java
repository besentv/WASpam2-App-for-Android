package xyz.besentv.waspam2;

/**
 * Created by besentv on 22.09.2016.
 */

import android.app.NotificationManager;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


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
        spamConfirmKeyboard = new Keyboard(this, R.xml.keyboard_confirmspam);
        inputView.setOnKeyboardActionListener(this);
        inputView.setKeyboard(spamKeyboard);
        inputView.setPreviewEnabled(false);
        nBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Spammed Messages:").setContentText("").setOngoing(true);


        return inputView;
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
        inputView.setKeyboard(spamKeyboard);
        spamAmount = 1;
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
                            if (i >= spamAmount) {

                            }
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
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        switch (primaryCode) {
            case 1:
                if (isSpamming) {
                    Toast.makeText(getApplicationContext(), "Spam Thread is running! Please wait for it to end.", Toast.LENGTH_SHORT).show();
                } else if (Settings.needSpamConfirmation && !isSpamming) {
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
                    Log.d("WASpam2", "Inputconn String:" + text.toString());
                }
                break;
            default:
                return;
        }
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
