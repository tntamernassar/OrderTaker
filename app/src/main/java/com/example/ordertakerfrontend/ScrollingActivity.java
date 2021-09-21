package com.example.ordertakerfrontend;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ordertakerfrontend.databinding.ActivityScrollingBinding;

import java.io.File;


public class ScrollingActivity extends AppCompatActivity {

    private ActivityScrollingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        int table = intent.getExtras().getInt("table");


        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle("طاوله " + table);

        FloatingActionButton cancel = binding.cancel;
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.WAITRESS.cancelOrder(table);

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton submit = binding.submit;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.WAITRESS.submitOrder(table);
            }
        });

        ListView listView = findViewById(R.id.menu_holder);


        com.example.ordertakerfrontend.Menu menu = com.example.ordertakerfrontend.Menu.getInstance();
        listView.setAdapter(menu);

        Activity that = this;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(that);
                View menuPopup = that.getLayoutInflater().inflate(R.layout.menu_popup, null);

                MenuProduct menuProduct = menu.getMenuProductList().get(i);

                TextView nameTV = menuPopup.findViewById(R.id.name);
                TextView descTV = menuPopup.findViewById(R.id.description);
                ImageView image = menuPopup.findViewById(R.id.image);
//                TextView price = menuPopup.findViewById(R.id.price);

                File imgFile = new  File(getFilesDir().getAbsolutePath()+"/"+menuProduct.getImages()[0]);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                image.setImageBitmap(myBitmap);

                nameTV.setText(menuProduct.getName());
                descTV.setText(menuProduct.getDescription());
//                price.setText(menuProduct.getPrice() + "₪");

                dialogBuilder.setView(menuPopup);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}