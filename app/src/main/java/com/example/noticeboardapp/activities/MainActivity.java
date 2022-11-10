package com.example.noticeboardapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.noticeboardapp.R;
import com.example.noticeboardapp.adapters.NoticeAdapter;
import com.example.noticeboardapp.database.NoticeDatabase;
import com.example.noticeboardapp.entities.Notice;
import com.example.noticeboardapp.listeners.NoticeListener;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoticeListener {

    //request code is used to add a new notice
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    //request code is used to update notice
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    //request code is used to display all notice
    public static final int REQUEST_CODE_SHOW_NOTES = 3;


    private RecyclerView noticeRecyclerView;
    private List<Notice> noticeList;
    private NoticeAdapter noticeAdapter;

    private int noticeClickedPosition = -1;

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
        noticeAdapter = new NoticeAdapter(noticeList, this);
        noticeRecyclerView.setAdapter(noticeAdapter);

        getNotices(REQUEST_CODE_SHOW_NOTES,false);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                noticeAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(noticeList.size() != 0) {
                    noticeAdapter.searchNotices(s.toString());
                }
            }
        });
    }

    @Override
    public void onNoticeClicked(Notice notice, int position) {
        noticeClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(),CreateNoticeActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("notice",notice);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);
    }

    private void getNotices(final int requestCode, final boolean isNoteDeleted) {

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
                if(requestCode == REQUEST_CODE_SHOW_NOTES) {
                    noticeList.addAll(notices);
                    noticeAdapter.notifyDataSetChanged(); //adding all notices from database to noticeList and notify adapter about the new data set.
                } else if(requestCode == REQUEST_CODE_ADD_NOTE){
                    noticeList.add(0,notices.get(0));
                    noticeAdapter.notifyItemInserted(0);
                    noticeRecyclerView.smoothScrollToPosition(0);//adding only newly added notice from the DB to noticeList.Scrolling recycler view to the top.
                }else if (requestCode ==  REQUEST_CODE_UPDATE_NOTE){
                    noticeList.remove(noticeClickedPosition);

                    if(isNoteDeleted){
                        noticeAdapter.notifyItemRemoved(noticeClickedPosition);
                    }else {
                        noticeList.add(noticeClickedPosition, notices.get(noticeClickedPosition));
                        noticeAdapter.notifyItemChanged(noticeClickedPosition);//remove notice from the clicked position and adding latest notice from same position from DB.
                    }
                }

            }
        }

        new GetNoticesTask().execute();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNotices(REQUEST_CODE_ADD_NOTE,false);
        } else if(requestCode ==REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK){
            if(data != null) {
                getNotices(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoticeDeleted",false));
            } else {

            }
        }
    }
}