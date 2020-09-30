package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.*;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.lang.ref.WeakReference;
import java.net.*;

public class MainActivity extends AppCompatActivity {

    TextView SongName;
    TextView Artist;
    static ImageView AlbumPicture;
    static ImageButton PlayBtn;
    static ImageButton PauseBtn;
    static ImageButton StopBtn;
    static SeekBar seekBar;
    static MediaPlayer mediaPlayer;
    static TextView time1;
    static TextView time2;
    static TextView CurLyrics;
    static TextView NextLyrics;
    static Map<Integer,String> hm;

    static int pos = 0;
    static int curTime = 0;
    static boolean isPlaying = false;
    static boolean isEnd = false;

    public static final int LOAD_SUCCESS = 101;
    private final MyHandler mHandler = new MyHandler(this);

    static class MyThread extends Thread{
        @Override
        public void run(){
            while(isPlaying){
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                curTime = Math.min(mediaPlayer.getDuration(),mediaPlayer.getCurrentPosition()/1000);
                time1.post(new Runnable() {
                    @Override
                    public void run() {
                        time1.setText(String.format("%02d",curTime/60)+":"+String.format("%02d",curTime%60));
                    }
                });
                int idx = 0;
                final int[] arr = new int[hm.size()];
                for(int i:hm.keySet()) arr[idx++] = i;

                for(int i=0;i<arr.length;i++){
                    if(mediaPlayer.getCurrentPosition() < arr[i]){
                        CurLyrics.post(new Runnable() {
                            @Override
                            public void run() {
                                CurLyrics.setText("");
                            }
                        });
                        NextLyrics.post(new Runnable() {
                            @Override
                            public void run() {
                                NextLyrics.setText(hm.get(arr[0]));
                            }
                        });
                        break;
                    }
                    if(mediaPlayer.getCurrentPosition() >= arr[i]){
                        if(i+1!=arr.length){
                            if(mediaPlayer.getCurrentPosition()<arr[i+1]){
                                final int finalI = i;
                                CurLyrics.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        CurLyrics.setText(hm.get(arr[finalI]));
                                    }
                                });

                                final int finalI1 = i;
                                NextLyrics.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        NextLyrics.setText(hm.get(arr[finalI1 +1]));
                                    }
                                });
                                break;
                            }
                        }
                        else{
                            final int finalI2 = i;
                            CurLyrics.post(new Runnable() {
                                @Override
                                public void run() {
                                    CurLyrics.setText(hm.get(arr[finalI2]));
                                }
                            });
                            NextLyrics.post(new Runnable() {
                                @Override
                                public void run() {
                                    NextLyrics.setText("");
                                }
                            });
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SongName = findViewById(R.id.SongName);
        Artist = findViewById(R.id.Artist);
        AlbumPicture = findViewById(R.id.AlbumPicture);
        PlayBtn = findViewById(R.id.playBtn);
        PauseBtn = findViewById(R.id.PauseBtn);
        StopBtn = findViewById(R.id.stopBtn);
        seekBar = findViewById(R.id.seekBar);
        time1 = findViewById(R.id.time1);
        time2 = findViewById(R.id.time2);
        CurLyrics = findViewById(R.id.CurLyrics);
        NextLyrics = findViewById(R.id.NextLyrics);

        hm = new LinkedHashMap<>();

        PauseBtn.setVisibility(View.INVISIBLE);

        final String url = "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/song.json";
        new Thread(new Runnable() {
            @Override
            public void run() {
                GET(url);
            }
        }).start();


    }
    public void GET(String s) {
        String result = null;
        try {
            // Open the connection
            URL url = new URL(s);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();

            // Get the stream
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // Set the result
            result = sb.toString();
            Message message = mHandler.obtainMessage(LOAD_SUCCESS, result);
            mHandler.sendMessage(message);
        }
        catch (Exception e) {
            // Error calling the rest api
            Log.e("REST_API", "GET method failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> weakReference;

        public MyHandler(MainActivity mainactivity) {
            weakReference = new WeakReference<MainActivity>(mainactivity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainactivity = weakReference.get();
            //System.out.println(msg.obj);
            if (mainactivity != null) {
                switch (msg.what) {
                    case LOAD_SUCCESS:
                        String jsonString = (String)msg.obj;
                        try {
                            JSONObject jsonObject = new JSONObject(jsonString);
                            mainactivity.SongName.setText(jsonObject.getString("album")+"\n"+jsonObject.getString("title"));
                            mainactivity.Artist.setText(jsonObject.getString("singer"));
                            new DownloadFilesTask().execute(jsonObject.getString("image"));
                            String mp3 = jsonObject.getString("file");
                            String tmp = jsonObject.getString("lyrics");

                            String[] lyrics = tmp.split("\n");
                            for(int i=0;i<lyrics.length;i++){
                                //System.out.println(lyrics[i]);
                                String[] s = lyrics[i].split("]");
                                s[0]=s[0].substring(1,s[0].length()-1);
                                //System.out.println(s[0]);
                                String[] s2 = s[0].split(":");
                                int cur = Integer.parseInt(s2[2])+(Integer.parseInt(s2[1])+Integer.parseInt(s2[0])*60)*1000;
                                hm.put(cur,s[1]);
                                //System.out.println(cur);
                                //System.out.println(hm.get(cur));
                            }

                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(mp3);
                            mediaPlayer.setLooping(false);
                            mediaPlayer.prepare();
                            final int duration = jsonObject.getInt("duration");
                            time2.setText(String.format("%02d",duration/60)+":"+String.format("%02d",duration%60));
                            seekBar.setMax(mediaPlayer.getDuration());
                            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                public void onStopTrackingTouch(SeekBar seekBar) {
                                    if(!isEnd){
                                        isPlaying = true;
                                        int ttt = seekBar.getProgress(); // 사용자가 움직여놓은 위치
                                        mediaPlayer.seekTo(ttt);
                                        mediaPlayer.start();
                                        PlayBtn.setVisibility(View.INVISIBLE);
                                        PauseBtn.setVisibility(View.VISIBLE);
                                        new MyThread().start();
                                    }
                                }
                                public void onStartTrackingTouch(SeekBar seekBar) {
                                    isPlaying = false;
                                    if(mediaPlayer.isPlaying())
                                        mediaPlayer.pause();
                                }
                                public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser) {
                                    if (seekBar.getMax()==progress) {
                                        isEnd = true;
                                        PlayBtn.setVisibility(View.VISIBLE);
                                        PauseBtn.setVisibility(View.INVISIBLE);
                                        isPlaying = false;
                                        mediaPlayer.stop();
                                        try {
                                            mediaPlayer.prepare();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        pos = 0;
                                        //System.out.println(duration);
                                        time1.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                time1.setText(String.format("%02d",duration/60)+":"+String.format("%02d",duration%60));
                                            }
                                        });
                                    }
                                }
                            });
                            PlayBtn.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    isEnd = false;
                                    mediaPlayer.seekTo(pos);
                                    mediaPlayer.start();
                                    PlayBtn.setVisibility(View.INVISIBLE);
                                    PauseBtn.setVisibility(View.VISIBLE);
                                    isPlaying = true;
                                    new MyThread().start();
                                }
                            });
                            PauseBtn.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    pos = mediaPlayer.getCurrentPosition();
                                    System.out.println(pos);
                                    mediaPlayer.pause();
                                    PlayBtn.setVisibility(View.VISIBLE);
                                    PauseBtn.setVisibility(View.INVISIBLE);
                                    isPlaying = false;
                                }
                            });
                            StopBtn.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    pos = 0;
                                    PlayBtn.setVisibility(View.VISIBLE);
                                    PauseBtn.setVisibility(View.INVISIBLE);
                                    isPlaying = false;
                                    if(mediaPlayer!=null){
                                        seekBar.setMax(mediaPlayer.getDuration());
                                        seekBar.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                seekBar.setProgress(0);
                                            }
                                        });
                                        mediaPlayer.stop();
                                        //mediaPlayer.reset();
                                        try {
                                            if(!mediaPlayer.isPlaying())
                                                mediaPlayer.prepare();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    time1.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            time1.setText("00:00");
                                        }
                                    });
                                }
                            });
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }

    }

    private static class DownloadFilesTask extends AsyncTask<String,Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String[] strings) {
            Bitmap bmp = null;
            try {
                String img_url = strings[0]; //url of the image
                URL url = new URL(img_url);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            // doInBackground 에서 받아온 total 값 사용 장소
            AlbumPicture.setImageBitmap(result);
        }
    }
}
