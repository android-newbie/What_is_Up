package com.example.whatisup.PhoneLogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.whatisup.R;

public class EnterPhoneNumber extends AppCompatActivity {
    //Declaration App Variables
    EditText etPhoneNumber;
    Button btnSendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone_number);

        //Initialization App Variables
        etPhoneNumber=findViewById(R.id.etPhoneNumber);
        btnSendOtp=findViewById(R.id.btnSendOtp);

        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber=etPhoneNumber.getText().toString().trim();
                if (phoneNumber.isEmpty() || phoneNumber.length()<10){

                    etPhoneNumber.setError("Enter a valid phone number");
                    etPhoneNumber.requestFocus();
                }else{
                    Intent intent = new Intent(EnterPhoneNumber.this, VerifyPhoneNumber.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    startActivity(intent);
                }
            }
        });

    }
}