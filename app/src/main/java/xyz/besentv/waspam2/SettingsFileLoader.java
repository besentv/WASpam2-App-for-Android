package xyz.besentv.waspam2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by besentv on 27.09.2017.
 */

public class SettingsFileLoader {

    private Context parentContext;

    public SettingsFileLoader(Context parentContext) {
        this.parentContext = parentContext;
    }

    public void writeSettingsFile(JSONObject settings) {
        if (ContextCompat.checkSelfPermission(parentContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File file = new File(Settings.settingsFilePath + Settings.settingsFileName);
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

    public JSONObject loadSettingsFile() throws JSONException {
        if (ContextCompat.checkSelfPermission(parentContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File file = new File(Settings.settingsFilePath + Settings.settingsFileName);
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
        return null;
    }

}
