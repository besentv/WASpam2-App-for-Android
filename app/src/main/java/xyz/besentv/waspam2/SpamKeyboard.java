package xyz.besentv.waspam2;

/**
 * Created by Bernhard on 22.09.2016.
 */

import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;


public class SpamKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener

{

    private KeyboardView inputView;
    private Keyboard spamKeyboard;
    private boolean spamming = false;
    private boolean gotInputConnection = false;
    final int loops = 1;
    private int loopsDone = 0;
    private InputConnection ic;
    public static String message = " ";


    @Override
    public void onCreate() {
        spamming = false;
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
        final Handler handler = new Handler();
        if(!gotInputConnection) {
            ic = getCurrentInputConnection();
        }
        switch (primaryCode) {
            case 1:
   //for(int i= 0; i<100; i++) {
        ic.commitText(Settings.spamMessage, 1);
     //     handler.postDelayed(new Runnable() {
       //       @Override
         //     public void run() {

        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
        //ic.commitText("_", 1);
            // }
        //}, 100);

        //  loopsDone++;

        // loopsDone = 0;
   // }
                break;
            case 2:
                    Intent settings = new Intent(SpamKeyboard.this , Settings.class);

                    settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(settings);

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
