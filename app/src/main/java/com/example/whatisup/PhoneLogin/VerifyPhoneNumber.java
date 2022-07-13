package com.example.whatisup.PhoneLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatisup.MainActivity;
import com.example.whatisup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneNumber extends AppCompatActivity {
    //Declaration xml layout Variables
    EditText etCode;
    Button btnVerify;
    // string for storing our verification ID
    String mVerificationId;

    //Declaration Firebase Variables
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);



        //Initialization App Variables
        etCode=findViewById(R.id.etCode);
        btnVerify=findViewById(R.id.btnVerify);

        //Initialization Firebase Variables
        mAuth=FirebaseAuth.getInstance();

        //getting mobile number from the previous activity
        //and sending the verification code to the number
        Intent intent=getIntent();
        String phoneNumber=intent.getStringExtra("phoneNumber");
        sendVerificationCode(phoneNumber);


        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=etCode.getText().toString().trim();
                if(code.isEmpty()||code.length()<6){
                    etCode.setError("Enter the valid code");
                    etCode.requestFocus();
                }

                //verifying the code entered manually
                verifyVerificationCode(code);

            }
        });


    }



    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String phoneNumber){
        //this method is used for getting
        //OTP on user phone number
        PhoneAuthOptions options=new PhoneAuthOptions.Builder(mAuth)
                .setPhoneNumber("+91"+phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            //initializing our callbacks for
            //verification callback method.
            mCallBack=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code=phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if(code!=null){
                etCode.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }
        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // displaying error message with firebase exception.
            Toast.makeText(VerifyPhoneNumber.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        //below method is used when
        //OTP is sent from Firebase
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //when we receive the Otp it
            //contains a unique id which
            //we are storing in our string
            //which we have already created
            mVerificationId=s;
        }
    };




    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //check condition
                        if(task.isSuccessful()){
                            //verification successful we will start the main activity
                            startActivity(new Intent(VerifyPhoneNumber.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }else{
                            //verification unsuccessful....display an error message
                            String message=task.getException().getMessage() ;
                            Snackbar snackbar=Snackbar.make(null,message,Snackbar.LENGTH_SHORT);
                            snackbar.setAction("Dismiss",new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }

                    }
                });
    }
}