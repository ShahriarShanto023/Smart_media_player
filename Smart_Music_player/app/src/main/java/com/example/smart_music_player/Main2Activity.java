package com.example.smart_music_player;
import android.content.Intent;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    private String[] itemsAll;
    private ListView mSongsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mSongsList = findViewById(R.id.songsList);
        appExternalStoragePermission();
    }


    public void appExternalStoragePermission() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        displayAudioSongsName();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                }).check();
    }


    public ArrayList<File> readOnlyAudioSongs(File file) {

        ArrayList<File> arrayList = new ArrayList<>();

        File[] allFiles = file.listFiles();

        for (File individualFile : allFiles) {

            if (individualFile.isDirectory() && !individualFile.isHidden()) {

                arrayList.addAll(readOnlyAudioSongs(individualFile));
            }

            else {

                if (individualFile.getName().endsWith(".mp3") || individualFile.getName().endsWith(".aac")) {

                    arrayList.add(individualFile);
                }
            }
        }

        return arrayList;
    }


    private void displayAudioSongsName() {

        final ArrayList<File> audioSongs = readOnlyAudioSongs(Environment.getExternalStorageDirectory());

        itemsAll = new String[audioSongs.size()];

        for (int songCounter = 0; songCounter < audioSongs.size(); songCounter++) {

            itemsAll[songCounter] = audioSongs.get(songCounter).getName();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Main2Activity.this, android.R.layout.simple_list_item_1, itemsAll);
        mSongsList.setAdapter(arrayAdapter);


        mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String songName = mSongsList.getItemAtPosition(position).toString();

                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                intent.putExtra("song", audioSongs);
                intent.putExtra("name", songName);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }
}