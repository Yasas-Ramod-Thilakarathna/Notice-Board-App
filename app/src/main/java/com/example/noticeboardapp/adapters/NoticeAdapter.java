package com.example.noticeboardapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noticeboardapp.R;
import com.example.noticeboardapp.entities.Notice;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>{

    private List<Notice> notices;

    public NoticeAdapter(List<Notice> notices) {
        this.notices = notices;
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

        NoticeViewHolder (@NonNull View itemView){
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
        }

        void setNotice(Notice notice) {
            textTitle.setText(notice.getTitle());
            if(notice.getSubtitle().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(notice.getSubtitle());
            }
            textDateTime.setText(notice.getDateTime());
        }
    }
}
