package com.example.noticeboardapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.noticeboardapp.dao.NoticeDao;
import com.example.noticeboardapp.entities.Notice;

@Database(entities = Notice.class, version = 1, exportSchema = false)

public abstract class NoticeDatabase extends RoomDatabase {

    private static NoticeDatabase noticeDatabase;

    public static synchronized NoticeDatabase getDatabase(Context context){
        if(noticeDatabase == null){
            noticeDatabase = Room.databaseBuilder(
                    context,
                    NoticeDatabase.class,
                    "notice_db"
            ).build();
        }
        return noticeDatabase;
    }

    public abstract NoticeDao noticeDao();
}
