package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//    查找控件 播放和下一首
ImageView nextIv,playIv;
//歌手、歌名
    TextView singerTv,songTv;
//播放界面
    RecyclerView musicRv;
//    数据源
    List<LocalMusicBean>mDataes;
    private LocalMusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mDataes = new ArrayList<>();
//        创建适配器
        adapter = new LocalMusicAdapter(this, mDataes);//因为以后会有数据更新，所以要作为成员变量来写
        musicRv.setAdapter(adapter);
        //设置布局管理器，选择linearlayout的展示形式
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        musicRv.setLayoutManager(layoutManager);
        //要先加载本地数据源
        localMusicData();
    }
////加载本地数据源
    private void localMusicData() {
        //利用contentTesolver对象
        ContentResolver resolver = getContentResolver();
        //获取本体音乐的URL地址
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //查询地址
        Cursor cursor = resolver.query(uri,null,null,null,null);
        //遍历(是看
        int id=0;
        while (cursor.moveToNext()) {//如果cursor可以移动到下一个说明还有没读出来的
            String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            id++;
            String sid = String.valueOf(id);
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));//路径
            //这个是获取时长，
            long duration = cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String time = sdf.format(new Date(duration));//转换类型
//            将数据封装
            LocalMusicBean bean = new LocalMusicBean(sid,song,singer,time,path);
            mDataes.add(bean);
        }
//       装好之后要告诉适配器劳资更新了
        adapter.notifyDataSetChanged();
    }

    private void initView(){
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
        switch (v.getId()){
            case R.id.local_music_botton_icon_start:

                break;
            case R.id.local_music_botton_icon_next:

                break;
        }
    }
}
