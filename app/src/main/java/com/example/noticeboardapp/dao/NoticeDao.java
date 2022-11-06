package com.example.noticeboardapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.noticeboardapp.entities.Notice;

import java.util.List;

@Dao
public interface NoticeDao {

    @Query("SELECT * FROM Notices ORDER BY id DESC" )
    List<Notice> getAllNotices();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotice(Notice notice);

    @Delete
    void deleteNotice(Notice notice);

}
