package xyz.besentv.waspam2;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

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
    public static String spamMessage = "";
    public static int spamAmount = 0;
    public static int spamDelay = 1;
    private JSONObject settingsJSON = null;
    private NumberPicker numberPicker;
    private EditText editDelayAmount;
    private Button changeKeyboardButton;
    private InputMethodManager inputMethodManager;
    private boolean settingsLoaded = false;
    private int permissionGrantID = 1;

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
        editDelayAmount = (EditText) findViewById(R.id.editDelayAmount);
        editDelayAmount.setText(new Integer(spamDelay).toString());
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(9999);
        numberPicker.setMinValue(1);
        numberPicker.setValue(1);
        messageInput = (EditText) findViewById(R.id.editTextMessage);
        changeKeyboardButton = (Button) findViewById(R.id.changeKeyboardButton);
        changeKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.showInputMethodPicker();
            }
        });
        super.onCreate(savedInstanceState);
        getSettings();
        if (settingsLoaded) {
            try {
                spamMessage = settingsJSON.getString(spamMessageStateString);
                spamDelay = settingsJSON.getInt(spamDelayStateString);
                spamAmount = settingsJSON.getInt(spamAmountStateString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionGrantID);
        }
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
        spamAmount = numberPicker.getValue();
        if (!editDelayAmount.getText().toString().equals("")) {
            spamDelay = Integer.parseInt(editDelayAmount.getText().toString());
        } else {
            spamDelay = 1;
        }
            try {
                settingsJSON.put(spamMessageStateString, spamMessage);
                settingsJSON.put(spamAmountStateString, spamAmount);
                settingsJSON.put(spamDelayStateString, spamDelay);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            writeSettingsFile(settingsJSON);
        }

}
