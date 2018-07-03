package com.example.siddharth.have_moreserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.siddharth.have_moreserver.Comman.Comman;
import com.example.siddharth.have_moreserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {
    EditText edtphone,edtPassword;
    Button btnSignIn;
    FirebaseDatabase database;
    DatabaseReference table_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        edtPassword=(MaterialEditText)findViewById(R.id.edtPassword);
        edtphone=(MaterialEditText)findViewById(R.id.edtphone);
        btnSignIn=(Button)findViewById(R.id.btnSignIn);


        //init firebase
         database=FirebaseDatabase.getInstance();
         table_user=database.getReference("User");

      btnSignIn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              signInUser(edtphone.getText().toString(),edtPassword.getText().toString());
          }
      });

    }

      private void signInUser(final String phone, String password) {
        final ProgressDialog pd=new ProgressDialog(SignIn.this);
        pd.setMessage("Loading.......");
        pd.show();
        final String localPhone=phone;
        final String localpassword=password;
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(localPhone).exists())
                {
                    pd.dismiss();;
                    User user=dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if(Boolean.parseBoolean(user.getIsStaff()))//if staff is true;
                    {
                        if (user.getPassword().equals(localpassword))
                        {
                          Intent Login=new Intent(SignIn.this,Home.class);
                            Comman.currentUser=user;
                            startActivity(Login);
                            finish();
                        }
                        else{
                            Toast.makeText(SignIn.this, "Wrong Password!!!", Toast.LENGTH_SHORT).show();}
                    }
                    else{
                        Toast.makeText(SignIn.this, "Please Login With Staff Account", Toast.LENGTH_SHORT).show();}
                }
                else{
                    pd.dismiss();;
                Toast.makeText(SignIn.this, "User Does Not Exsist in  Database!!!", Toast.LENGTH_SHORT).show();}

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
