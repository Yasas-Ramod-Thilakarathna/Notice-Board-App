package com.example.noticeboardapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.noticeboardapp.R;
import com.example.noticeboardapp.adapters.NoticeAdapter;
import com.example.noticeboardapp.database.NoticeDatabase;
import com.example.noticeboardapp.entities.Notice;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD_NOTE = 1;

    private RecyclerView noticeRecyclerView;
    private List<Notice> noticeList;
    private NoticeAdapter noticeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageAddNoteMain =findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNoticeActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });

        noticeRecyclerView = findViewById(R.id.noticeRecyclerView);
        noticeRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );

        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(noticeList);
        noticeRecyclerView.setAdapter(noticeAdapter);

        getNotices();
    }

    private void getNotices() {

        //Just as need an async task to save a notice,you will also need it to get notices from the database
        @SuppressLint("StaticFieldLeak")
        class GetNoticesTask extends AsyncTask<Void, Void, List<Notice>>{

            @Override
            protected List<Notice> doInBackground(Void... voids) {
                return NoticeDatabase
                        .getDatabase(getApplicationContext())
                        .noticeDao().getAllNotices();
            }

            @Override
            protected void onPostExecute(List<Notice> notices) {
                super.onPostExecute(notices);
                if(noticeList.size() == 0) {
                    noticeList.addAll(notices);
                    noticeAdapter.notifyDataSetChanged();
                } else {
                    noticeList.add(0, notices.get(0));
                    noticeAdapter.notifyItemInserted(0);
                }
                noticeRecyclerView.smoothScrollToPosition(0);
            }
        }

        new GetNoticesTask().execute();
    }
}