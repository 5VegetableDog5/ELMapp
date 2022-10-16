package com.example.elmapp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.elmapp.R;
import java.io.File;

public class Shops_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        //TextView test;
        ImageView imageView = findViewById(R.id.bannerImage);
        File file = new File("data/data/com.example.elmapp/files/Old/banner1.png");
        if(file.exists())
            Glide.with(this).load(file).into(imageView);
        else Log.e("Image","file not exists");

    }
}