package com.kopecrad.dynablaster.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kopecrad.dynablaster.R;
import com.kopecrad.dynablaster.game.infrastructure.GUIHandle;
import com.kopecrad.dynablaster.game.infrastructure.GameDB;
import com.kopecrad.dynablaster.game.infrastructure.GameState;
import com.kopecrad.dynablaster.game.infrastructure.InputHandler;
import com.kopecrad.dynablaster.game.infrastructure.Renderer;
import com.kopecrad.dynablaster.game.infrastructure.Scene;
import com.kopecrad.dynablaster.game.infrastructure.sound.SoundController;
import com.kopecrad.dynablaster.game.infrastructure.level.EnemyDescription;
import com.kopecrad.dynablaster.game.infrastructure.level.PlayerProgress;
import com.kopecrad.dynablaster.game.infrastructure.score.Score;
import com.kopecrad.dynablaster.game.infrastructure.sound.SoundType;

import java.util.ArrayList;

/**
 * Core game activity - where actual game happens.
 */
public class GameActivity extends FullscreenActivity {

    private Renderer renderer;
    private Scene scene;
    private SurfaceView surfView;
    private InputHandler inp;

    private SharedPreferences prefs;
    private SoundController sounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Log.d("kek", "asdfadfa");
        prefs= getSharedPreferences(getResources().getString(R.string.prefs_name), Context.MODE_PRIVATE);

        setupInputListeners();

        initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        renderer.pause();
        scene.soundsDeactivate();
    }

    @Override
    protected void onResume() {
        Log.d("kek", "GameActivity::onResume");
        super.onResume();
        renderer.resume();
        scene.soundsActivate(this);
    }

    private void setupInputListeners() {
        setupMoveButton(R.id.btn_left);
        setupMoveButton(R.id.btn_right);
        setupMoveButton(R.id.btn_up);
        setupMoveButton(R.id.btn_down);
        setupMoveButton(R.id.btn_bomb);
    }

    private void setupMoveButton(final int id) {
        ImageView btn= findViewById(id);
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    inp.setInput(id, true);
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    inp.setInput(id, false);
                }
                return false;
            }
        });
    }

    public void switchToEnd(final GameState state, Score score) {
        final GameActivity ac= this;
        Gson gson= new Gson();
        final String scoreJson = gson.toJson(score);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(ac, EndActivity.class);
                intent.putExtra("state", state.ordinal());
                if(state == GameState.LEVEL_COMPLETED)
                    intent.putExtra("score", scoreJson);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ac).toBundle());
            }
        });
    }

    private void initialize() {
        GUIHandle gameGUI= new GUIHandle(
                (TextView) findViewById(R.id.panel_livesTxt),
                (TextView) findViewById(R.id.panel_clockTxt),
                (TextView) findViewById(R.id.panel_scoreTxt)
        );
        surfView= findViewById(R.id.surfaceView);
        EnemyDescription.setEnemyTableRef(new GameDB(this).getTableEnemy());
        inp= InputHandler.inst();

        renderer= new Renderer(surfView);
        scene= new Scene(this, new PlayerProgress(prefs), renderer, gameGUI);
    }
}
