package com.example.todoido.Fragment;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.todoido.Adapter.CardAdapter;
import com.example.todoido.AnimeView.FlowerView;
import com.example.todoido.AnimeView.LeaveView;
import com.example.todoido.AnimeView.RainView;
import com.example.todoido.R;
import com.example.todoido.AnimeView.SnowView;
import com.example.todoido.ViewModel.MonthViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MonthFragment extends Fragment {

    ArrayList<CardAdapter.CardItem> items = new ArrayList<>();
    CardAdapter adapter;
    private ViewPager2 viewPager;
    private ActivityResultLauncher<Intent> mGetContent;
    MonthViewModel monthViewModel;
    private String newTheme = "Theme1";
    String theme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewModel 초기화
        monthViewModel = new ViewModelProvider(this).get(MonthViewModel.class);

        // 이미지 선택 Intent 결과 받기 위한 콜백 설정
        mGetContent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedImage = data.getData();
                            // 선택한 이미지를 CardAdapter에 설정
                            adapter.setImageUri(selectedImage, viewPager.getCurrentItem());
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_month, container, false);

        // ViewPager2 초기화
        viewPager = view.findViewById(R.id.monthRecyclerView);
        // 페이지 전환 효과 설정
        viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float v = Math.abs(Math.abs(position) - 1);
                page.setScaleX(v / 2 + 0.5f);
                page.setScaleY(v / 2 + 0.5f);
            }
        });

        // CardAdapter 설정
        adapter = new CardAdapter(items, getContext(), viewPager, mGetContent, monthViewModel, newTheme, theme); // newTheme을 전달
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(adapter);

        // '+' 버튼 설정
        ImageButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            adapter.addItem("");
            adapter.notifyDataSetChanged();
        });

        // LiveData 관찰 시작
        monthViewModel.getItemList().observe(getViewLifecycleOwner(), monthItems -> {
            // LiveData가 변경되었을 때 리사이클러뷰 갱신
            items.clear();
            for (MonthViewModel.MonthItem monthItem : monthItems) {
                CardAdapter.CardItem cardItem = new CardAdapter.CardItem(monthItem.getContent(), monthItem.getViewType());
                cardItem.setId(monthItem.getId()); // id 필드 설정
                cardItem.setImageUri(monthItem.getImageUri()); // imageUri 필드 설정
                items.add(cardItem);
            }
            adapter.notifyDataSetChanged();
        });

        // 테마 변경에 대한 관찰
        monthViewModel.getTheme().observe(getViewLifecycleOwner(), newTheme -> {
            // 테마에 따라 UI 갱신
            adapter.onThemeChanged(newTheme);  // 어댑터에 새 테마 설정
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
}