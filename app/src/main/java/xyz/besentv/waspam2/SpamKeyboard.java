package xyz.besentv.waspam2;

/**
 * Created by Bernhard on 22.09.2016.
 */

import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;


public class SpamKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener

{

    private KeyboardView inputView;
    private Keyboard spamKeyboard;
    private boolean gotInputConnection = false;
    public static InputConnection ic;
    private boolean isSpamming = false;
    private int spamAmount;
    private InputMethodManager inputMethodManager;

    @Override
    public void onCreate() {
        inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        super.onCreate();

    }


    @Override
    public View onCreateInputView() {
        super.onCreateInputView();
        inputView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        spamKeyboard = new Keyboard(this, R.xml.keyboard_spam);
        inputView.setOnKeyboardActionListener(this);
        inputView.setKeyboard(spamKeyboard);
        return inputView;
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }


    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if (!gotInputConnection) {
            ic = getCurrentInputConnection();
        }
        switch (primaryCode) {
            case 1:
                if (!isSpamming) {
                    isSpamming = true;
                    spamAmount = Settings.spamAmount;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int i = 0; i < spamAmount; i++) {
                                    ic = getCurrentInputConnection();
                                    ic.commitText(Settings.spamMessage, 1);
                                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                                    Thread.sleep(Settings.spamDelay);

                                }
                                isSpamming = false;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
                break;
            case 2:
                Intent settings = new Intent(SpamKeyboard.this, Settings.class);
                settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(settings);
            case 3:
                //keys.get(1).text = "aaaaaaaaaa";
                spamAmount = 1;
                break;
            case 4:
                inputMethodManager.showInputMethodPicker();
                break;
            default:
                char code = (char) primaryCode;
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
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
