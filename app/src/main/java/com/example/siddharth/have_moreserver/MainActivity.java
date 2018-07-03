package com.example.siddharth.have_moreserver;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.siddharth.have_moreserver.Model.Category;
import com.example.siddharth.have_moreserver.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button btnsignin;
    TextView Slogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Slogan= (TextView) findViewById(R.id.txtSlogan);
        btnsignin= (Button) findViewById(R.id.btnSignIn);

        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Pacifico.ttf");
        Slogan.setTypeface(face);



        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SignIn.class);
                startActivity(intent);
            }
        });
    }
}
