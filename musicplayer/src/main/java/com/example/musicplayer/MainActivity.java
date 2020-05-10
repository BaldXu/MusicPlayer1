package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //    查找控件 播放和下一首
    ImageView nextIv, playIv;
    //歌手、歌名
    TextView singerTv, songTv;
    //播放界面
    RecyclerView musicRv;
    //    数据源
    List<LocalMusicBean> mDataes;
    private LocalMusicAdapter adapter;
    //    当前正在播放的音乐的位置
    int playingPosition = -1;//默认-1，也就是没有播放的意思
    //暂停音乐时进度条的位置
    int pausePosition = 0;
    //Media play
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        initView();
        mediaPlayer = new MediaPlayer();
        mDataes = new ArrayList<>();
//        创建适配器
        adapter = new LocalMusicAdapter(this, mDataes);//因为以后会有数据更新，所以要作为成员变量来写
        musicRv.setAdapter(adapter);

        //设置布局管理器，选择linearlayout的展示形式
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicRv.setLayoutManager(layoutManager);
        //要先加载本地数据源
        localMusicData();
        //设置每一项的点击事件，调用adapter中定义的接口
        setEventListener();
    }

    //    item点击事件
    private void setEventListener() {
        adapter.setOnItemClickListener(new LocalMusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
//                因为后面也要用到这个位置，所以建议记录成一个成员变量
                playingPosition = position;
                LocalMusicBean musicBean = mDataes.get(position);
                playMusicPosition(musicBean);
            }
        });
    }
//被封装出来的方法，用于设置播放音乐的bean
    //根据传入对象播放音乐
    public void playMusicPosition(LocalMusicBean musicBean) {
        //                设置底部显示的歌手名和歌名
        singerTv.setText(musicBean.getSinger());
        songTv.setText(musicBean.getSong());
        stopPlaying();//应该先停止
        //先重置，再暂停再播放
        mediaPlayer.reset();
        //设置新的播放路径
        try {
            mediaPlayer.setDataSource(musicBean.getPath());
            startPlaying();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    播放音乐（两种情况：从暂停开始播放，从0开始播放
    private void startPlaying() {
//        Toast.makeText(this,playingPosition,Toast.LENGTH_SHORT).show();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            //先判断是不是暂停状况
            if (pausePosition == 0) {//从0开始
                try {
                    mediaPlayer.prepare();//先准备
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {//从暂停开始播放
                mediaPlayer.seekTo(pausePosition);
                mediaPlayer.start();
            }
            playIv.setImageResource(R.mipmap.suspend);//改变图标
        }
    }

    //    暂停播放
    private void pausePlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            pausePosition = mediaPlayer.getCurrentPosition();
            //Toast.makeText(this,"暂停被调用",Toast.LENGTH_SHORT).show();
            mediaPlayer.pause();
            playIv.setImageResource(R.mipmap.start);
        }
    }

    //    停止音乐播放
    private void stopPlaying() {
        if (mediaPlayer != null) {
            pausePosition=0;//将播放的位置回到最初
            mediaPlayer.pause();//先暂停
            mediaPlayer.seekTo(0);//回到开题
            mediaPlayer.stop();//停止播放
            playIv.setImageResource(R.mipmap.start);//改变图标
        }
    }

    //    当我离开的时候就停止播放音乐
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlaying();
    }

    ////加载本地数据源
    private void localMusicData() {
        //利用contentTesolver对象
        ContentResolver resolver = getContentResolver();
        //获取本体音乐的URL地址
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //查询地址
        Cursor cursor = resolver.query(uri, null, null, null, null);
        //遍历(是看
        int numId= 0;
        while (cursor.moveToNext()) {//如果cursor可以移动到下一个说明还有没读出来的
            String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            numId++;
            String sid = String.valueOf(numId);
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));//路径
            //这个是获取时长，
            long duration = cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String time = sdf.format(new Date(duration));//转换类型
//            将数据封装
            LocalMusicBean bean = new LocalMusicBean(sid, song, singer, time, path);
            mDataes.add(bean);
        }
//       装好之后要告诉适配器劳资更新了
        adapter.notifyDataSetChanged();
    }

    private void initView() {
//        初始化控件
        nextIv = findViewById(R.id.local_music_botton_icon_next);
        playIv = findViewById(R.id.local_music_botton_icon_start);
        singerTv = findViewById(R.id.local_music_botton_text_singer);
        songTv = findViewById(R.id.local_music_botton_text_song);
        musicRv = findViewById(R.id.local_music_show);
//        给按钮设置点击事件
        nextIv.setOnClickListener(this);
        playIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            点击开始按钮
            case R.id.local_music_botton_icon_start:
                //先判断一下现在是否在播放
                if (playingPosition == -1) {
                    Toast.makeText(this, "先选择要播放的音乐", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    pausePlaying();
                } else {
                    startPlaying();
                }
                break;
//                点击下一首按钮
            case R.id.local_music_botton_icon_next:
                if (playingPosition == -1){//没选任何一首就开始想播放
                    Toast.makeText(this, "先选择要播放的音乐", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(playingPosition == mDataes.size()-1){//最后一首的情况下,注意是角标，所以要减一
                    Toast.makeText(this,"已经是最后一首了，即将回到第一首",Toast.LENGTH_SHORT).show();
                    playingPosition=0;
                    LocalMusicBean firstBean = mDataes.get(playingPosition);
                    playMusicPosition(firstBean);
                }else {//选择了歌曲并且不是最后一首的情况下，点击下一首就应该把playingPosition++
                    playingPosition++;
                    LocalMusicBean firstBean = mDataes.get(playingPosition);
                    playMusicPosition(firstBean);
                }
                break;
        }
    }
}
