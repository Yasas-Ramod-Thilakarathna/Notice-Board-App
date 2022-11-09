package com.example.noticeboardapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noticeboardapp.R;
import com.example.noticeboardapp.database.NoticeDatabase;
import com.example.noticeboardapp.entities.Notice;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoticeActivity extends AppCompatActivity {

    private EditText inputNoticeTitle, inputNoticeSubtitle, inputNoticeText;
    private TextView textDateTime;
    private View viewSubtitleIndicator;
    private TextView textWebURL;
    private LinearLayout layoutWebURL;

    private String selectedNoticeColor;

    private AlertDialog dialogAddURL;

    private Notice alreadyAvailableNotice;

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
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        textWebURL = findViewById(R.id.textWebURL);
        layoutWebURL = findViewById(R.id.layoutWebURL);

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

        selectedNoticeColor = "#333333";//Default notice color

        if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailableNotice = (Notice) getIntent().getSerializableExtra("notice");
            setViewOrUpdateNotice();
        }

        initOptions();
        setSubtitleIndicatorColor();
    }

    private void setViewOrUpdateNotice() {
        inputNoticeTitle.setText(alreadyAvailableNotice.getTitle());
        inputNoticeSubtitle.setText(alreadyAvailableNotice.getSubtitle());
        inputNoticeText.setText(alreadyAvailableNotice.getNoticeText());
        textDateTime.setText(alreadyAvailableNotice.getDateTime());

        if(alreadyAvailableNotice.getWebLink() != null && !alreadyAvailableNotice.getWebLink().trim().isEmpty()){
            textWebURL.setText(alreadyAvailableNotice.getWebLink());
            layoutWebURL.setVisibility(View.VISIBLE);
        }
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
      notice.setColor(selectedNoticeColor);

        //checked if "layoutWebURL" is visible or not.
        // if its visible it means Web URL is added since we have made it visible only while adding Web URL from add URL dialog.
        if(layoutWebURL.getVisibility() == View.VISIBLE){
          notice.setWebLink(textWebURL.getText().toString());
      }

        /*if id of new notice is already available in the  database
        then it will be replaced with new notice and notice get updated*/
        if(alreadyAvailableNotice != null){
            notice.setId(alreadyAvailableNotice.getId());
        }

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

    private void initOptions(){
        final LinearLayout layoutOptions = findViewById(R.id.layoutOptions);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutOptions);
        layoutOptions.findViewById(R.id.textOptions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState((BottomSheetBehavior.STATE_EXPANDED));
                } else {
                    bottomSheetBehavior.setState(bottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView imageColor1 = layoutOptions.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutOptions.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutOptions.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutOptions.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutOptions.findViewById(R.id.imageColor5);

        layoutOptions.findViewById(R.id.viewColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoticeColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutOptions.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoticeColor = "#FdBE3B";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutOptions.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoticeColor = "#FF4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutOptions.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoticeColor = "#3A52Fc";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutOptions.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoticeColor = "#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setSubtitleIndicatorColor();
            }
        });

        if(alreadyAvailableNotice != null && alreadyAvailableNotice.getColor() != null && !alreadyAvailableNotice.getColor().trim().isEmpty()){
            switch (alreadyAvailableNotice.getColor()){
                case "#FdBE3B":
                    layoutOptions.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#FF4842":
                    layoutOptions.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#3A52Fc":
                    layoutOptions.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#000000":
                    layoutOptions.findViewById(R.id.viewColor5).performClick();
                    break;
            }
        }

        layoutOptions.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddURLDialog();
            }
        });
    }

    private void setSubtitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoticeColor));
    }

    private void showAddURLDialog() {
        if(dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoticeActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,
                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);

            dialogAddURL = builder.create();
            if(dialogAddURL.getWindow() != null){
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(inputURL.getText().toString().trim().isEmpty()){
                        Toast.makeText(CreateNoticeActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if(!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()){
                        Toast.makeText(CreateNoticeActivity.this, "Enter valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        textWebURL.setText(inputURL.getText().toString());
                        layoutWebURL.setVisibility(View.VISIBLE);
                        dialogAddURL.dismiss();
                    }
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogAddURL.dismiss();
                }
            });
        }
        dialogAddURL.show();
    }
}