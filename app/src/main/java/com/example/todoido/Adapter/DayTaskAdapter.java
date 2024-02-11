package com.example.todoido.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoido.R;
import com.example.todoido.ViewModel.DayTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DayTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_HEADER = 1;

    private List<DayTask> taskList;
    private OnItemClickListener listener;
    private OnHeaderClickListener headerClickListener;

    public void setTaskList(List<DayTask> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    public List<DayTask> getTaskList() {
        return this.taskList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        this.headerClickListener = listener;
    }

    public interface OnHeaderClickListener {
        void onHeaderClick(DayTask header);
    }

    public interface OnItemClickListener {
        void onItemClick(DayTask task);
    }

    public DayTaskAdapter(List<DayTask> taskList, OnItemClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = inflater.inflate(R.layout.item_day_date, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View itemView = inflater.inflate(R.layout.item_day, parent, false);
            return new ItemViewHolder(itemView, this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            bindItemViewHolder((ItemViewHolder) holder, position);
        } else if (holder instanceof HeaderViewHolder) {
            bindHeaderViewHolder((HeaderViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return taskList.get(position).isHeader() ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    private void bindItemViewHolder(ItemViewHolder holder, int position) {
        DayTask task = taskList.get(position);
        holder.startTime.setText(task.getStartTime());
        holder.endTime.setText(task.getEndTime());
        holder.contentText.setText(task.getText());

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onItemClick(task));
        }

        holder.closeButton.setOnClickListener(v -> {
            taskList.remove(task);
            notifyItemRemoved(position);

            DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("day")
                    .child(task.getId());
            taskRef.removeValue();
        });
    }

    private void bindHeaderViewHolder(HeaderViewHolder holder, int position) {
        DayTask header = taskList.get(position);
        String formattedDate = header.getDate(); // 날짜 정보 가져오기
        String dayOfWeek = DayTask.getDayOfWeek(formattedDate); // 요일 정보 가져오기

        String displayText = formattedDate + " " + dayOfWeek+ " >";
        holder.dateText.setText(displayText);

        holder.itemView.setOnClickListener(v -> {
            if (headerClickListener != null) {
                headerClickListener.onHeaderClick(header);
            }
        });
    }

    public DayTask getTaskAt(int position) {
        return taskList.get(position);
    }
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView startTime, endTime, contentText;
        ImageButton closeButton;
        DayTaskAdapter adapter;

        public ItemViewHolder(@NonNull View itemView, DayTaskAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);
            contentText = itemView.findViewById(R.id.contentText);
            closeButton = itemView.findViewById(R.id.closeButton);

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    DayTask task = adapter.taskList.get(position);

                    String startTime = task.getStartTime();
                    String endTime = task.getEndTime();
                    String text = task.getText();

                    String shareText = "오늘 일정 공유"+"\n시작 시간: " + startTime + "\n종료 시간: " + endTime + "\n내용: " + text;

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    v.getContext().startActivity(shareIntent);

                    return true;
                }
                return false;
            });
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.tv_date);
        }
    }
}


