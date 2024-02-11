package com.example.todoido.WeekPage;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.todoido.Adapter.WeekAdapter;
import com.example.todoido.AnimeView.FlowerView;
import com.example.todoido.AnimeView.LeaveView;
import com.example.todoido.AnimeView.RainView;
import com.example.todoido.AnimeView.SnowView;
import com.example.todoido.R;
import com.example.todoido.ViewModel.WeekViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Week2Fragment extends BaseWeekFragment {
    private RecyclerView weekRecyclerView;
    private WeekAdapter weekAdapter;
    private WeekViewModel weekViewModel;

    ViewPager viewPager;
    ImageButton buttonPrev, buttonNext;
    ImageView mainImageView;

    @Override
    protected String getWeekId() {
        return "week2";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week2, container, false);

        weekViewModel = new ViewModelProvider(requireActivity()).get(WeekViewModel.class);

        weekRecyclerView = view.findViewById(R.id.weekrecyclerView);
        weekAdapter = new WeekAdapter(weekViewModel, "week2");
        weekRecyclerView.setAdapter(weekAdapter);
        weekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewPager = getActivity().findViewById(R.id.week_viewPager);
        buttonPrev = view.findViewById(R.id.week2_prev);
        buttonNext = view.findViewById(R.id.week2_next);
        mainImageView = view.findViewById(R.id.mainImageView);

        setupDialog(mainImageView);

        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });


        // 효과
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FlowerView flowerView = view.findViewById(R.id.flowerView);
        RainView rainView = view.findViewById(R.id.rainView);
        LeaveView leaveView = view.findViewById(R.id.leaveView);
        SnowView snowView = view.findViewById(R.id.snowView);

        if (currentUser != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference("Users").child(currentUser.getUid());

            // Read the selected effect from the database
            userRef.child("seasonEffect").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String seasonEffect = dataSnapshot.getValue(String.class);
                    if (seasonEffect == null) {
                        seasonEffect = "선택 안함";  // Use 'none' as the default value
                    }

                    // Hide all views initially
                    flowerView.setVisibility(View.INVISIBLE);
                    rainView.setVisibility(View.INVISIBLE);
                    leaveView.setVisibility(View.INVISIBLE);
                    snowView.setVisibility(View.INVISIBLE);

                    // Show the selected view
                    switch (seasonEffect) {
                        case "봄":
                            flowerView.setVisibility(View.VISIBLE);
                            break;
                        case "여름":
                            rainView.setVisibility(View.VISIBLE);
                            break;
                        case "가을":
                            leaveView.setVisibility(View.VISIBLE);
                            break;
                        case "겨울":
                            snowView.setVisibility(View.VISIBLE);
                            break;
                        default:
                            // No effect is selected
                            break;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                }
            });
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weekViewModel.getWeekDataList().observe(getViewLifecycleOwner(), weekDataList -> {
            for (WeekViewModel.WeekData weekData : weekDataList) {
                if (weekData.getId().equals("week2")) {
                    weekAdapter.setSelectedEmoji(weekData.getSelectedEmoji());
                    weekAdapter.setGoals(weekData.getGoals());

                    String selectedEmoji = weekData.getSelectedEmoji();
                    if (selectedEmoji != null) {
                        switch (selectedEmoji) {
                            case "happy":
                                mainImageView.setImageResource(R.drawable.ic_happy2);
                                break;
                            case "smile":
                                mainImageView.setImageResource(R.drawable.ic_smile);
                                break;
                            case "soso":
                                mainImageView.setImageResource(R.drawable.ic_soso);
                                break;
                            case "bad":
                                mainImageView.setImageResource(R.drawable.ic_bad);
                                break;
                            case "angry":
                                mainImageView.setImageResource(R.drawable.ic_angry);
                                break;
                        }
                    }
                }
            }
        });
    }
}
