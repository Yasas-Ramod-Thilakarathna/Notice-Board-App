package com.example.noticeboardapp.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noticeboardapp.R;
import com.example.noticeboardapp.entities.Notice;
import com.example.noticeboardapp.listeners.NoticeListener;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>{

    private List<Notice> notices;
    private NoticeListener noticeListener;

    public NoticeAdapter(List<Notice> notices, NoticeListener noticeListener) {
        this.notices = notices;
        this.noticeListener = noticeListener;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoticeViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_notice,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        holder.setNotice(notices.get(position));
        holder.layoutNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noticeListener.onNoticeClicked(notices.get(position),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notices.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoticeViewHolder extends RecyclerView.ViewHolder{

        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutNotice;

        NoticeViewHolder (@NonNull View itemView){
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutNotice = itemView.findViewById(R.id.layoutNote);
        }

        void setNotice(Notice notice) {
            textTitle.setText(notice.getTitle());
            if(notice.getSubtitle().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(notice.getSubtitle());
            }
            textDateTime.setText(notice.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutNotice.getBackground();
            if(notice.getColor() != null) {
                gradientDrawable.setColor(Color.parseColor(notice.getColor()));
            } else {
                gradientDrawable.setColor(Color.parseColor("333333"));
            }
        }
    }
}
