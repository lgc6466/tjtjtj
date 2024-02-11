package com.example.todoido.WeekPage;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoido.Adapter.WeekAdapter;
import com.example.todoido.R;
import com.example.todoido.ViewModel.WeekViewModel;

public abstract class BaseWeekFragment extends Fragment {
    protected RecyclerView recyclerView;
    protected WeekAdapter adapter;

    private WeekViewModel weekViewModel;

    protected abstract String getWeekId();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.weekrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        weekViewModel = new ViewModelProvider(requireActivity()).get(WeekViewModel.class);
        adapter = new WeekAdapter(weekViewModel, getWeekId());
        recyclerView.setAdapter(adapter);

        weekViewModel.getWeekDataList().observe(getViewLifecycleOwner(), weekDataList -> {
            for (WeekViewModel.WeekData weekData : weekDataList) {
                if (weekData.getId().equals(getWeekId())) {
                    adapter.setGoals(weekData.getGoals());
                    adapter.setSelectedEmoji(weekData.getSelectedEmoji());  // 이모지 설정
                    break;
                }
            }
        });
    }



    protected void setupDialog(final ImageView mainImageView) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.emoiton_popup);

        ImageView happyIcon = dialog.findViewById(R.id.happy);
        happyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainImageView.setImageResource(R.drawable.ic_happy2);
                adapter.setSelectedEmoji("happy");  // 선택한 이모지를 WeekAdapter에 전달하고 Firebase에 저장
                dialog.dismiss();
            }
        });


        ImageView smileIcon = dialog.findViewById(R.id.smile);
        smileIcon. setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainImageView.setImageResource(R.drawable.ic_smile);
                adapter.setSelectedEmoji("smile");
                dialog.dismiss();
            }
        });

        ImageView sosoIcon = dialog.findViewById(R.id.soso);
        sosoIcon. setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainImageView.setImageResource(R.drawable.ic_soso);
                adapter.setSelectedEmoji("soso");
                dialog.dismiss();
            }
        });

        ImageView badIcon = dialog.findViewById(R.id.bad);
        badIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainImageView.setImageResource(R.drawable.ic_bad);
                adapter.setSelectedEmoji("bad");
                dialog.dismiss();
            }
        });

        ImageView angryIcon = dialog.findViewById(R.id.angry);
        angryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainImageView.setImageResource(R.drawable.ic_angry);
                adapter.setSelectedEmoji("angry");
                dialog.dismiss();
            }
        });

        mainImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }
}
