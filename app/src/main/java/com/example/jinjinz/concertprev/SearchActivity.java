package com.example.jinjinz.concertprev;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.jinjinz.concertprev.fragments.ConcertsFragment;

public class SearchActivity extends AppCompatActivity {
    ConcertsFragment cFragment;
    Button player;
    Button concert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        player = (Button) findViewById(R.id.playerBtn);
        concert = (Button) findViewById(R.id.concertBtn);

        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SearchActivity.this, PlayerActivity.class);
                startActivity(i);
            }
        });

        concert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SearchActivity.this, ConcertActivity.class);
                startActivity(i);
            }
        });

        if (savedInstanceState == null) {
            cFragment = cFragment.newInstance("param1", "param2");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.concertContainer, cFragment);
            ft.commit();
        }


    }
}
