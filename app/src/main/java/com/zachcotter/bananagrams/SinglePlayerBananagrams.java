package com.zachcotter.bananagrams;


import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SinglePlayerBananagrams extends Bananagrams {
  private BananagramsCountdownTimer timer;
  private long timeRemaining;
  private boolean isPOC;

  @Override
  public boolean isMultiPlayer() {
    return false;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.single_player_bananagrams);
    super.onCreate(savedInstanceState);
  }

  public boolean isPOC() {
    return isPOC;
  }

  @Override
  protected void initialize() {
    this.timeRemaining = TIME_ALLOWED_MS;
    isPOC = this.getIntent().getBooleanExtra(GCMIntentService.PROOF_OF_CONCEPT,
                                             false);
    super.initialize();
  }

  @Override
  protected void reinitialize(Bundle savedInstanceState) {
    timeRemaining = savedInstanceState.getLong(TIME_KEY);
    isPOC = savedInstanceState.getBoolean(GCMIntentService.PROOF_OF_CONCEPT);
    super.reinitialize(savedInstanceState);
  }


  private void startTimer() {
    this.timer = new BananagramsCountdownTimer(timeRemaining,
                                               1000);
    timer.start();
  }

  private void pauseTimer() {
    timer.cancel();
    timer = null;
  }

  private class BananagramsCountdownTimer extends CountDownTimer {

    private TextView timeView;


    public BananagramsCountdownTimer(long millisInFuture,
                                     long countDownInterval) {
      super(millisInFuture,
            countDownInterval);
      this.timeView = (TextView) findViewById(R.id.time_left);
    }

    @Override
    public void onTick(long msTillDone) {
      timeRemaining = msTillDone;

      if(timeRemaining < 10000) {
        tutor.show(getApplicationContext(),
                   Tutor.TIME_RUNNING_OUT);
      }
      if(timeRemaining < 6000) {
        timeView.setTextColor(Color.RED);
        new ToneGenerator(AudioManager.STREAM_NOTIFICATION,
                          100).startTone(
          ToneGenerator.TONE_PROP_BEEP2);
      }

      timeView.setText("Time: " + msTillDone / 1000);
      timeView.invalidate();
    }

    @Override
    public void onFinish() {
      endGame();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(TIME_KEY,
                     timeRemaining);
    outState.putBoolean(GCMIntentService.PROOF_OF_CONCEPT,
                        isPOC);
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    if(hasFocus) {
      startTimer();
    }
    else {
      pauseTimer();
    }
    super.onWindowFocusChanged(hasFocus);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.action_bananagrams_pause:
        pause();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }


  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.single_player_bananagrams_actions,
                              menu);
    return super.onCreateOptionsMenu(menu);
  }
}
