package com.freescale.bletoolbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.freescale.bletoolbox.activity.MainActivity;
import com.freescale.bletoolbox.activity.MainBeacon;

public class EnterPassword extends AppCompatActivity {
    EditText editText;
    Button button;

    String password,password1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);

        SharedPreferences settings = getSharedPreferences("PREFS", 0);
        password = settings.getString("password","");
        password1 = settings.getString("password1","");

        editText = (EditText) findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();

                if(text.equals(password)){
                    //enter the app
                    Intent intent = new Intent(getApplicationContext(), MainBeacon.class);
                    startActivity(intent);
                    finish();
                }

                else if(text.equals(password1)){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                else{
                    Toast.makeText(EnterPassword.this,"Wrong Password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
