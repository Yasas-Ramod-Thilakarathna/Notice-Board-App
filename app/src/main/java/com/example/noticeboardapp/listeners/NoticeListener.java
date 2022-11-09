package com.example.noticeboardapp.listeners;

import com.example.noticeboardapp.entities.Notice;

public interface NoticeListener {
    void onNoticeClicked(Notice notice, int position);
}
