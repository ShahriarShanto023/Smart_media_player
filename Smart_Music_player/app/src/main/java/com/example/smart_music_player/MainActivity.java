package com.example.smart_music_player;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.SettingInjectorService;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";

    private ImageView pausePlayBtn, nextBtn, previousBtn, repeatBtn;
    private TextView songNameTxt;

    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;
    private Button voiceEnabledBtn;

    private String mode = "ON";

    private MediaPlayer myMediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String mSongName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkVoiceCommandPermission();

        pausePlayBtn = findViewById(R.id.play_pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        previousBtn = findViewById(R.id.previous_btn);
        repeatBtn = findViewById(R.id.loop);

        imageView = findViewById(R.id.logo);
        lowerRelativeLayout = findViewById(R.id.lower);
        voiceEnabledBtn = findViewById(R.id.voice_enable_btn);
        songNameTxt = findViewById(R.id.songName);

        parentRelativeLayout = findViewById(R.id.parentRelativiteLayout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        validateRecieveValuesAndStartPlaying();
        imageView.setBackgroundResource(R.drawable.six);


        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {

                ArrayList<String> matchesFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matchesFound != null) {

                    if (mode.equals("ON")) {

                        keeper = matchesFound.get(0);

                        if (keeper.equals("pause the song")) {

                            playPauseSong();

                            Toast.makeText(MainActivity.this, "" + keeper, Toast.LENGTH_LONG).show();
                        }

                        else if (keeper.equals("play the song")) {

                            playPauseSong();

                            Toast.makeText(MainActivity.this, "" + keeper, Toast.LENGTH_LONG).show();
                        }

                        else if (keeper.equals("play next song")) {

                            playNextSong();

                            Toast.makeText(MainActivity.this, "" + keeper, Toast.LENGTH_LONG).show();
                        }

                        else if (keeper.equals("play previous song")) {

                            playPreviousSong();

                            Toast.makeText(MainActivity.this, "" + keeper, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });


        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper = "";
                        break;

                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }

                return false;
            }
        });


        voiceEnabledBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mode.equals("ON")) {

                    mode = "OFF";
                    voiceEnabledBtn.setText("Voice Enabled Mode - OFF");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);
                }

                else {

                    mode = "ON";
                    voiceEnabledBtn.setText("Voice Enabled Mode - ON");
                    lowerRelativeLayout.setVisibility(View.GONE);
                }
            }
        });


        pausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playPauseSong();
            }
        });


        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myMediaPlayer.getCurrentPosition() > 0) {

                    playPreviousSong();
                }
            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myMediaPlayer.getCurrentPosition() > 0) {

                    playNextSong();
                }
            }
        });

        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                repeatTheSong();
            }
        });

        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                playNextSong();
            }
        });
    }


    private void validateRecieveValuesAndStartPlaying() {

        if (myMediaPlayer != null) {

            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("song");
        mSongName = mySongs.get(position).getName();
        String songName = intent.getStringExtra("name");

        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        position = bundle.getInt("position", 0);
        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(MainActivity.this, uri);
        myMediaPlayer.start();
        //myMediaPlayer.setLooping(true);
    }


    private void checkVoiceCommandPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }


    private void playPauseSong() {

        imageView.setBackgroundResource(R.drawable.four);

        if (myMediaPlayer.isPlaying()) {

            pausePlayBtn.setImageResource(R.drawable.play);
            myMediaPlayer.pause();
        }

        else {

            pausePlayBtn.setImageResource(R.drawable.pause);
            imageView.setBackgroundResource(R.drawable.five);

            myMediaPlayer.start();
            // myMediaPlayer.setLooping(true);
        }
    }


    private void repeatTheSong() {

        if (myMediaPlayer.isLooping()) {

            repeatBtn.setImageResource(R.drawable.norepeat);
            myMediaPlayer.setLooping(false);
            Toast.makeText(MainActivity.this, "Loop off", Toast.LENGTH_SHORT).show();
        }

        else {

            repeatBtn.setImageResource(R.drawable.repeat);
            myMediaPlayer.setLooping(true);
            Toast.makeText(MainActivity.this, "Loop on", Toast.LENGTH_SHORT).show();
        }
    }


    private void playNextSong() {

        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position = ((position + 1) % mySongs.size());

        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(MainActivity.this, uri);

        mSongName = mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        myMediaPlayer.start();
        //myMediaPlayer.setLooping(true);

        imageView.setBackgroundResource(R.drawable.three);

        if (myMediaPlayer.isPlaying()) {

            pausePlayBtn.setImageResource(R.drawable.pause);
            // myMediaPlayer.pause();
        }

        else {

            pausePlayBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);
        }
    }


    private void  playPreviousSong() {

        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position = ((position - 1) < 0 ? (mySongs.size() - 1) : (position - 1));

        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(MainActivity.this, uri);

        mSongName = mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        myMediaPlayer.start();
        //myMediaPlayer.setLooping(true);

        imageView.setBackgroundResource(R.drawable.two);

        if (myMediaPlayer.isPlaying()) {

            pausePlayBtn.setImageResource(R.drawable.pause);
            // myMediaPlayer.pause();
        }

        else {

            pausePlayBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);
        }
    }
}
