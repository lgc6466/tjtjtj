package com.example.todoido.Fragment;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoido.Adapter.DayTaskAdapter;
import com.example.todoido.AlarmReceiver;
import com.example.todoido.AnimeView.FlowerView;
import com.example.todoido.AnimeView.LeaveView;
import com.example.todoido.AnimeView.RainView;
import com.example.todoido.R;
import com.example.todoido.AnimeView.SnowView;
import com.example.todoido.ViewModel.DayTask;
import com.example.todoido.ViewModel.DayViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DayFragment extends Fragment {
    private BottomSheetBehavior bottomSheetBehavior;
    private FrameLayout bottomSheet;
    private FrameLayout bottomSheetCalendar;  // Add this line
    private BottomSheetBehavior bottomSheetBehaviorCalendar;  // Add this line
    private FrameLayout blackBackground;
    private Spinner spinner;
    private DayViewModel dayViewModel;
    private RecyclerView recyclerView;
    private boolean isSheetVisible = false;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final DatabaseReference databaseRef = firebaseUser != null ? FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("day") : null;
    private int selectedTaskPosition = -1;

    private void createBlackBackground() {
        blackBackground.setVisibility(View.VISIBLE);
    }

    private void removeBlackBackground() {
        blackBackground.setVisibility(View.GONE);
    }

    // 알림
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        bottomSheet = view.findViewById(R.id.sheet_day);
        bottomSheetCalendar = view.findViewById(R.id.sheet_day_calendar);  // Add this line
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehaviorCalendar = BottomSheetBehavior.from(bottomSheetCalendar);  // Add this line
        EditText day_txt = view.findViewById(R.id.day_txt);
        blackBackground = view.findViewById(R.id.blackBackground);
        CheckBox smartNotification = view.findViewById(R.id.smart_notification);
        // ViewModel 초기화
        dayViewModel = new ViewModelProvider(this).get(DayViewModel.class);

        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.dayRecyclerView);


        spinner = view.findViewById(R.id.spinner);

        String[] items = new String[]{"선택 안 함", "5분 전", "10분 전", "30분 전", "1시간 전", "3시간 전"};

        ArrayAdapter<String> spinnerAdapter;
        spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                switch(item) {
                    case "5분 전":
                        calendar.add(Calendar.MINUTE, -5);
                        break;
                    case "10분 전":
                        calendar.add(Calendar.MINUTE, -10);
                        break;
                    case "30분 전":
                        calendar.add(Calendar.MINUTE, -30);
                        break;
                    case "1시간 전":
                        calendar.add(Calendar.HOUR_OF_DAY, -1);
                        break;
                    case "3시간 전":
                        calendar.add(Calendar.HOUR_OF_DAY, -3);
                        break;
                    default:
                        // "선택 안 함"의 경우 알림을 취소
                        if(alarmManager != null && pendingIntent != null) {
                            alarmManager.cancel(pendingIntent);
                        }
                        return;
                }

                // 현재 시간보다 알람 시간이 이전이라면 다음날로 설정
                if(calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                }

                // 알람 설정
                if (alarmManager == null) {
                    alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                }
                Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                intent.putExtra("channel_name", "spinnerSelection");
                intent.putExtra("channel_description", "text");

                // System.currentTimeMillis()를 사용하여 요청 코드 생성
                int requestCode = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무것도 선택되지 않은 경우 처리
            }
        });

        Button timePickerButton = view.findViewById(R.id.timePickerButton);
        Button timePickerButton2 = view.findViewById(R.id.timePickerButton2);
        MaterialButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSheetVisible) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    isSheetVisible = false;
                    selectedTaskPosition = -1;  // BottomSheet가 닫힐 때마다 selectedTaskPosition 초기화
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    isSheetVisible = true;

                    // 아이템 선택이 아닌 addButton을 통해 BottomSheet를 열 경우, 기존 내용 초기화
                    if (selectedTaskPosition == -1) {
                        timePickerButton.setText("시작 시간");
                        timePickerButton2.setText("종료 시간");
                        day_txt.setText("");
                        spinner.setSelection(0);
                        smartNotification.setChecked(false);
                    }

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItem = parent.getItemAtPosition(position).toString();
                            Toast.makeText(getActivity(), selectedItem + " 선택됨", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }
        });

        View.OnClickListener timePickerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;

                MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        .setTheme(R.style.CustomTimePickerTheme)
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(00)
                        .setMinute(00)
                        .build();

                materialTimePicker.show(getChildFragmentManager(), "TIME_PICKER");

                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int hour = materialTimePicker.getHour();
                        int minute = materialTimePicker.getMinute();
                        clickedButton.setText(String.format("%02d:%02d", hour, minute));
                        clickedButton.setTextColor(getResources().getColor(R.color.selected_tab_text_color));
                    }
                });
            }
        };

        timePickerButton.setOnClickListener(timePickerClickListener);
        timePickerButton2.setOnClickListener(timePickerClickListener);

        DayViewModel dayViewModel = new ViewModelProvider(this).get(DayViewModel.class);
        RecyclerView recyclerView = view.findViewById(R.id.dayRecyclerView);
        DayTaskAdapter adapter = new DayTaskAdapter(new ArrayList<>(), null);
        adapter.setOnItemClickListener(new DayTaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DayTask task) {
                // 항목 클릭 이벤트를 처리합니다.
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                isSheetVisible = true;
                timePickerButton.setText(task.getStartTime());
                timePickerButton2.setText(task.getEndTime());
                day_txt.setText(task.getText());
                spinner.setSelection(((ArrayAdapter<String>)spinner.getAdapter()).getPosition(task.getSpinnerSelection()));
                smartNotification.setChecked(task.isChecked());
                selectedTaskPosition = adapter.getTaskList().indexOf(task);

                // 토스트 메시지 추가
                Toast.makeText(getContext(), "카드를 길게 누르면 일정을 공유할 수 있습니다", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnHeaderClickListener(new DayTaskAdapter.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(DayTask header) {
                View includedLayout = view.findViewById(R.id.sheet_day_calendar);
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(includedLayout);
                FrameLayout blackBackground = view.findViewById(R.id.blackBackground);

                if (behavior != null) {
                    if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        blackBackground.setVisibility(View.GONE);
                    } else {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        blackBackground.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        blackBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View includedLayout = view.findViewById(R.id.sheet_day_calendar);
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(includedLayout);
                FrameLayout blackBackground = view.findViewById(R.id.blackBackground);

                if (behavior != null) {
                    if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        blackBackground.setVisibility(View.GONE);
                    } else if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        blackBackground.setVisibility(View.GONE);
                    }
                }
            }
        });

        CalendarView calendarView = view.findViewById(R.id.calendarView);

// 날짜 선택 리스너 설정
        calendarView.setOnDateChangeListener((calendar, year, month, dayOfMonth) -> {
            Calendar selection = Calendar.getInstance();
            selection.set(year, month, dayOfMonth);
            scrollToSelectedDate(selection);
        });




        bottomSheetBehaviorCalendar.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // BottomSheet가 열렸을 때, 검은 배경을 표시
                    createBlackBackground();
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    // BottomSheet가 닫혔을 때, 검은 배경을 감추기
                    removeBlackBackground();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // 슬라이드 중에 추가적인 작업이 필요하면 여기에 구현
                if (slideOffset == 0.0) {
                    // 슬라이드가 완전히 닫혔을 때, 검은 배경을 감추기
                    removeBlackBackground();
                }
            }
        });


        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dayViewModel.getTaskList().observe(getViewLifecycleOwner(), tasks -> {
            adapter.setTaskList(tasks);
            adapter.notifyDataSetChanged();
        });



        Button submitButton = view.findViewById(R.id.submit_btn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startTime = timePickerButton.getText().toString();
                String endTime = timePickerButton2.getText().toString();
                String text = day_txt.getText().toString();
                String spinnerSelection = spinner.getSelectedItem().toString();
                boolean isChecked = smartNotification.isChecked();

                // 텍스트가 비어 있는지 확인
                if (text.isEmpty()) {
                    // 텍스트가 비어 있다면 토스트 메시지를 표시하고 함수를 종료
                    Toast.makeText(getContext(), "일정을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String currentDate;
                if (selectedTaskPosition != -1) {
                    // 수정하는 경우에는 원래의 날짜를 사용
                    currentDate = adapter.getTaskList().get(selectedTaskPosition).getDate();
                } else {
                    // 새로운 일정을 추가하는 경우에는 현재 날짜를 사용
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
                    currentDate = sdf.format(new Date());
                }

                DayTask task = new DayTask(currentDate, startTime, endTime, text, spinnerSelection, isChecked);

                if (selectedTaskPosition != -1) {
                    task.setId(adapter.getTaskList().get(selectedTaskPosition).getId());
                    dayViewModel.updateTask(task);

                    selectedTaskPosition = -1;
                } else {
                    // 날짜 정보를 헤더 생성에 전달
                    dayViewModel.addTask(task);
                }
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                isSheetVisible = false;

                timePickerButton.setText("00:00");
                timePickerButton2.setText("00:00");
                day_txt.setText("");
                spinner.setSelection(0);
                smartNotification.setChecked(false);
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

    private void scrollToSelectedDate(Calendar selection) {
        // 선택된 날짜를 'yyyy년 MM월 dd일' 형식의 문자열로 변환
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
        String selectedDateString = dateFormat.format(selection.getTime());

        // DayViewModel로부터 가져온 데이터를 이용하여 해당 날짜의 아이템으로 리사이클러뷰를 스크롤
        for (int i = 0; i < dayViewModel.getTaskList().getValue().size(); i++) {
            DayTask task = dayViewModel.getTaskList().getValue().get(i);
            if (task.getDate().equals(selectedDateString)) {
                ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPositionWithOffset(i, 0);
                break;
            }
        }
    }


}
