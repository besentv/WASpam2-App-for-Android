package xyz.besentv.waspam2;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class Settings extends Activity {


    private EditText messageInput;
    private final String settingsFilePath = Environment.getExternalStorageDirectory().getPath() + "/WASpam2";
    private final String settingsFileName = "/waspam2settingsSave.json";
    private final String spamMessageStateString = "spamMessage";
    private final String spamAmountStateString = "spamAmount";
    private final String spamDelayStateString = "spamDelay";
    private final String needSpamConfirmationStateString = "needSpamConfirmation";
    public static String spamMessage = "";
    public static int spamAmount = 1;
    public static int spamDelay = 1;
    public static boolean needSpamConfirmation = false;
    private JSONObject settingsJSON = null;
    private NumberPicker delayAmountPicker;
    private NumberPicker editAmountPicker;
    private Button changeKeyboardButton;
    private InputMethodManager inputMethodManager;
    private boolean settingsLoaded = false;
    private int permissionGrantID = 1;
    private Switch needsConfirmSwitch;

    public Settings() throws JSONException, IOException {

    }

    private void writeSettingsFile(JSONObject settings) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File file = new File(settingsFilePath + settingsFileName);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);

                try {
                    fos.write(settings.toString().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private JSONObject loadSettingsFile() throws JSONException {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        File file = new File(settingsFilePath + settingsFileName);
        if (file.length() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        FileInputStream fis = null;
        int ch;
        try {
            fis = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fis, "UTF-8");
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(sb.toString());
    }

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

        super.onCreate(savedInstanceState);
        getSettings();
        if (settingsLoaded) {
            try {
                spamMessage = settingsJSON.getString(spamMessageStateString);
                spamDelay = settingsJSON.getInt(spamDelayStateString);
                spamAmount = settingsJSON.getInt(spamAmountStateString);
                needSpamConfirmation = settingsJSON.getBoolean(needSpamConfirmationStateString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionGrantID);
        }
        addListeners();
    }

    private void getSettings() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File dir = new File(settingsFilePath);
            if (!dir.exists()) {
                dir.mkdir();
            }
        }
        try {
            if (loadSettingsFile() == null) {
                Log.d("LOL", "SETT FILE NULL");
                settingsJSON = new JSONObject();
            } else {
                settingsJSON = loadSettingsFile();
                settingsLoaded = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
            settingsJSON.put(needSpamConfirmationStateString,needSpamConfirmation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        writeSettingsFile(settingsJSON);
    }

    private void addListeners(){
        changeKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.showInputMethodPicker();
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
                    settingsJSON.put(spamMessageStateString, spamMessage);;
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
    }
}
