package xyz.besentv.waspam2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;


public class Settings extends AppCompatActivity {


    private EditText messageInput;
    private final String spamMessageStateString = "spamMessage";
    public static String spamMessage = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent = getIntent();
        messageInput = (EditText) findViewById(R.id.editTextMessage);
        messageInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){

                    spamMessage = messageInput.getText().toString();
                }
            }
        });
    }

    @Override
    protected void onResume(){

    super.onResume();
        messageInput.setText(spamMessage);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

        savedInstanceState.putString(spamMessageStateString, spamMessage);

        super.onSaveInstanceState(savedInstanceState);



    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        spamMessage = savedInstanceState.getString(spamMessageStateString);

    }


    @Override
    protected void onPause(){
        super.onPause();
        spamMessage = messageInput.getText().toString();



    }
}
