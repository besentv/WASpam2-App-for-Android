package xyz.besentv.waspam2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.NumberPicker;


public class Settings extends AppCompatActivity {


    private EditText messageInput;
    private final String spamMessageStateString = "spamMessage";
    private final String spamAmountStateString = "spamAmount";
    private final String spamDelayStateString = "spamDelay";
    public static String spamMessage = "";
    public static int spamAmount = 0;
    public static int spamDelay = 1;
    private NumberPicker numberPicker;
    private EditText editDelayAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        editDelayAmount = (EditText) findViewById(R.id.editDelayAmount);
        editDelayAmount.setText(new Integer(spamDelay).toString());
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(9999);
        numberPicker.setMinValue(1);
        numberPicker.setValue(1);
        messageInput = (EditText) findViewById(R.id.editTextMessage);

    }
    @Override
    protected void onResume() {

        super.onResume();
        messageInput.setText(spamMessage);
        numberPicker.setValue(spamAmount);
        editDelayAmount.setText(new Integer(spamDelay).toString());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(spamMessageStateString, spamMessage);
        savedInstanceState.putInt(spamAmountStateString, spamAmount);
        savedInstanceState.putInt(spamDelayStateString, spamDelay);
        super.onSaveInstanceState(savedInstanceState);


    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        spamMessage = savedInstanceState.getString(spamMessageStateString);
        spamAmount = savedInstanceState.getInt(spamAmountStateString);
        spamDelay = savedInstanceState.getInt(spamDelayStateString);

    }


    @Override
    protected void onPause() {
        super.onPause();
        spamMessage = messageInput.getText().toString();
        spamAmount = numberPicker.getValue();
        if(!editDelayAmount.getText().toString().equals("")) {
            spamDelay = Integer.parseInt(editDelayAmount.getText().toString());
        }
        else{
            spamDelay = 1;
        }
    }

}
