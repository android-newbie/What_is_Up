package com.example.whatisup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatisup.Models.Users;
import com.example.whatisup.PhoneLogin.EnterPhoneNumber;
import com.example.whatisup.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class SignUpActivity extends AppCompatActivity {
    //Firebase variables declaration
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    int RC_SIGN_IN=2;
    GoogleSignInClient mGoogleSignInClient;


    //App variables declaration
    ActivitySignUpBinding binding;
    ProgressDialog p1,p2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Hide ActionBar
        getSupportActionBar().hide();

        //Firebase variables initialization
        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        //App variables initialization

        p2=loadingDiaglog("Login","Authenticating....");
        p1=loadingDiaglog("Loading","Please wait");
        // Initialize sign in options
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Initialize sign in client
        mGoogleSignInClient=GoogleSignIn.getClient(this,gso);


        binding.ivPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this, EnterPhoneNumber.class);
                startActivity(intent);
            }
        });


        binding.ivGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(RC_SIGN_IN);
            }
        });







        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String nameField, emailField, passwordField;
//                nameField = ;
//                emailField = ;
//                passwordField =;
                if(binding.etName.getText().toString().equals("") | binding.etEmail.getText().toString().equals("") |  binding.etPassword.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                }else{
                    p2.show();
                    mAuth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {



                                        if (task.isSuccessful()) {

                                            Users user = new Users(binding.etName.getText().toString(), binding.etEmail.getText().toString(), binding.etPassword.getText().toString());

                                            String id = task.getResult().getUser().getUid();
                                            database.getReference().child("Users").child(id).setValue(user);

                                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                           // p1.dismiss();
                                            DynamicToast.makeSuccess(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            DynamicToast.makeError(SignUpActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                           // p1.dismiss();
                                        }

                                    p2.dismiss();


                                }

                            });
                }
            }
        });

        binding.tvLoginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check condition
        if (requestCode == RC_SIGN_IN) {
            // When request code is equal to RC_SIGN_IN
            // Initialize task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            // check condition
            if (task.isSuccessful()) {
                // When google sign in successful
                // Display Toast

                // Initialize sign in account
                try {
                    GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null
                        firebaseAuthWithGoogle(googleSignInAccount);
                    }
                }
                catch (ApiException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount){
            AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
            //display  progress dialog box(authentication)
            p2.show();
            // Check credential
            mAuth.signInWithCredential(authCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Check condition
                            if (task.isSuccessful()) {
                                // When task is successful
                                //Initialize firebaseUser
                                FirebaseUser firebaseUser=mAuth.getCurrentUser();
                                //Creating object of Users class
                                //prsent in Models package
                                Users user=new Users();
                                //Set values to user object
                                //from firebase authentication
                                user.setName(firebaseUser.getDisplayName());
                                user.setId(firebaseUser.getUid());
                                user.setProfilePic(firebaseUser.getPhotoUrl().toString());
                                user.setEmail(firebaseUser.getEmail());
                                //Upload values to the firebase realtime database
                                database.getReference().child("Users").child(user.getId()).setValue(user);
                                // Redirect to profile activity
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                // Display Toast
                                 displaySuccessToast("Authentication successful");        //Firebase authentication successful
                            } else {
                                // When task is unsuccessful
                                // Display Toast
                                displayErrorToast("Authentication Failed :"+task.getException()
                                       .getMessage());
                                    //if account is blocked from Firebase
                                    mAuth.signOut();
                                    mGoogleSignInClient.signOut();
                            }

                            //dismiss  progress dialog box(authentication)
                            p2.dismiss();
                        }

                    });

        }

    @Override
    protected void onStart() {
        super.onStart();
        // Initialize firebase user
        FirebaseUser currentUser=mAuth.getCurrentUser();
        // Check condition
        if (currentUser!=null){

            // When user already sign in
            // redirect to main activity
            Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //Set this method on google sign in button
    void signIn(int RC_SIGN_IN){
        //Show progress dialog box before google signin intent

        p1.show();
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);


        //Delay method start here
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                //dismiss progress dialog box after google sign in intent loaded
                p1.dismiss();
            }
        }, 1000);
        //Delay method end here
    }

    //To display toast  anywhere in this class just call this method (SUCCESS)
    private void displaySuccessToast(String s) {
        DynamicToast.makeSuccess(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }
    //To display toast  anywhere in this class just call this method (ERROR)
    private void displayErrorToast(String s) {
        DynamicToast.makeError(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }


    //To display progress dialog box anywhere in these class just call this method
    //first delclare a object of ProgressDialog globally
    //then initialize that object using this method in oncreate
    //now it is ready to use
    ProgressDialog loadingDiaglog(String title,String message){
        ProgressDialog progressDialog;
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);

        return progressDialog;
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}
