package com.example.noticeboardapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noticeboardapp.R;
import com.example.noticeboardapp.database.NoticeDatabase;
import com.example.noticeboardapp.entities.Notice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoticeActivity extends AppCompatActivity {

    private EditText inputNoticeTitle, inputNoticeSubtitle, inputNoticeText;
    private TextView textDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        inputNoticeTitle = findViewById(R.id.inputNoticeTitle);
        inputNoticeSubtitle = findViewById(R.id.inputNoticeSubtitle);
        inputNoticeText = findViewById(R.id.inputNotice);
        textDateTime = findViewById(R.id.textDateTime);

        textDateTime.setText(
                new SimpleDateFormat ("EEEE,dd MMMM yyyy HH:mm a", Locale.getDefault()) //Pattern=Saturday,13 June 2020 21:09PM
                        .format(new Date())
        );

        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNotice();
            }
        });
    }

    private  void saveNotice() {
      if (inputNoticeTitle.getText().toString().trim().isEmpty()){
          Toast.makeText(this, "Note title can't be empty!", Toast.LENGTH_SHORT).show();
          return;
      }else if(inputNoticeSubtitle.getText().toString().trim().isEmpty()
      || inputNoticeText.getText().toString().trim().isEmpty()) {
          Toast.makeText(this,"Notice can't be empty!", Toast.LENGTH_SHORT).show();
          return;
      }

      //Preparing Notice object to save in database
      //Room doesn't allow database operation on the Main thread.
      //That's why using async task to save nate.

      final Notice notice = new Notice();
      notice.setTitle(inputNoticeTitle.getText().toString());
      notice.setSubtitle(inputNoticeSubtitle.getText().toString());
      notice.setNoticeText(inputNoticeText.getText().toString());
      notice.setDateTime(textDateTime.getText().toString());

      @SuppressLint("StaticFieldLeak")
      class SaveNoticeTask extends AsyncTask<Void,Void, Void>{

          @Override
          protected Void doInBackground(Void... voids) {
              NoticeDatabase.getDatabase(getApplicationContext()).noticeDao().insertNotice(notice);
              return null;
          }

          @Override
          protected void onPostExecute(Void unused) {
              super.onPostExecute(unused);
              Intent intent = new Intent();
              setResult(RESULT_OK,intent);
              finish();
          }
      }

      new SaveNoticeTask().execute();
    }
}