package com.example.jinjinz.concertprev;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.jinjinz.concertprev.fragments.SongsFragment;

public class ConcertActivity extends AppCompatActivity {
    SongsFragment sFragment;
    Button player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert);

        player = (Button) findViewById(R.id.playerBtn2);
        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ConcertActivity.this, PlayerActivity.class);
                startActivity(i);
            }
        });

        if (savedInstanceState == null) {
            sFragment = SongsFragment.newInstance("params1", "params2");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.songContainer, sFragment);
            ft.commit();
        }
    }
}
