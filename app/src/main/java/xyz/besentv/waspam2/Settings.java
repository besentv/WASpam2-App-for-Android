package xyz.besentv.waspam2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class Settings extends Activity {

    private EditText messageInput;
    public static final String settingsFilePath = Environment.getExternalStorageDirectory().getPath() + "/besentv.xyz/WASpam2";
    public static final String settingsFileName = "/waspam2settingsSave.json";
    public static final String spamMessageStateString = "spamMessage";
    public static final String spamAmountStateString = "spamAmount";
    public static final String spamDelayStateString = "spamDelay";
    public static final String needSpamConfirmationStateString = "needSpamConfirmation";
    public static final String recentMessagesStateString = "recentMessages";
    public static String[] recentMessages = {"","",""};
    public static String spamMessage = "";
    public static int spamAmount = 1;
    public static int spamDelay = 1;
    public static boolean needSpamConfirmation = false;
    private JSONObject settingsJSON = null;
    private NumberPicker delayAmountPicker;
    private NumberPicker editAmountPicker;
    private Button changeKeyboardButton;
    private Button openWhatsappButton;
    private InputMethodManager inputMethodManager;
    private int permissionGrantID = 1;
    private Switch needsConfirmSwitch;
    private TextView gotoBesentv;
    private SettingsFileLoader settingsFileLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        delayAmountPicker = (NumberPicker) findViewById(R.id.delayPicker);
        delayAmountPicker.setValue(spamDelay);
        delayAmountPicker.setMaxValue(99999);
        delayAmountPicker.setValue(1);
        editAmountPicker = (NumberPicker) findViewById(R.id.amountPicker);
        editAmountPicker.setValue(spamAmount);
        editAmountPicker.setMaxValue(99999);
        editAmountPicker.setMinValue(1);
        messageInput = (EditText) findViewById(R.id.editTextMessage);
        needsConfirmSwitch = (Switch) findViewById(R.id.needsConfirmSwitch);
        changeKeyboardButton = (Button) findViewById(R.id.changeKeyboardButton);
        openWhatsappButton = (Button) findViewById(R.id.openWhatsappButton);
        gotoBesentv = (TextView) findViewById(R.id.goto_besentv);
        settingsFileLoader = new SettingsFileLoader(this);

        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionGrantID);
        }
        if (getSettings()) {
            try {
                spamMessage = settingsJSON.getString(spamMessageStateString);
                spamDelay = settingsJSON.getInt(spamDelayStateString);
                spamAmount = settingsJSON.getInt(spamAmountStateString);
                needSpamConfirmation = settingsJSON.getBoolean(needSpamConfirmationStateString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        addListeners();
    }

    /*
    *returns true if settings were found on file */
    private boolean getSettings() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File dir = new File(settingsFilePath);
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

    @Override
    protected void onResume() {

        super.onResume();
        messageInput.setText(spamMessage);
        editAmountPicker.setValue(spamAmount);
        delayAmountPicker.setValue(spamDelay);
        needsConfirmSwitch.setChecked(needSpamConfirmation);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(spamMessageStateString, spamMessage);
        savedInstanceState.putInt(spamAmountStateString, spamAmount);
        savedInstanceState.putInt(spamDelayStateString, spamDelay);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
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
        spamAmount = editAmountPicker.getValue();
        spamDelay = delayAmountPicker.getValue();
        try {
            settingsJSON.put(spamMessageStateString, spamMessage);
            settingsJSON.put(spamAmountStateString, spamAmount);
            settingsJSON.put(spamDelayStateString, spamDelay);
            settingsJSON.put(needSpamConfirmationStateString, needSpamConfirmation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        settingsFileLoader.writeSettingsFile(settingsJSON);
    }

    private void addListeners() {
        changeKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.showInputMethodPicker();
                settingsFileLoader.writeSettingsFile(settingsJSON);
            }
        });
        delayAmountPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                spamDelay = newVal;
                try {
                    settingsJSON.put(spamDelayStateString, spamDelay);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        editAmountPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                spamAmount = newVal;
                try {
                    settingsJSON.put(spamAmountStateString, spamAmount);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                spamMessage = messageInput.getText().toString();
                try {
                    settingsJSON.put(spamMessageStateString, spamMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        needsConfirmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                needSpamConfirmation = isChecked;
            }
        });

        openWhatsappButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openWa = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                if (openWa != null) {
                    startActivity(openWa);
                }
            }
        });
        gotoBesentv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent gotoBesentv = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/besentv"));
                startActivity(gotoBesentv);
                return false;
            }
        });
    }
}
