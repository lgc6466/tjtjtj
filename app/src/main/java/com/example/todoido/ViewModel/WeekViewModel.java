package com.example.todoido.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WeekViewModel extends ViewModel {
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final DatabaseReference databaseRef = firebaseUser != null ? FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Week") : null;
    private final MutableLiveData<List<WeekData>> weekDataList = new MutableLiveData<>();

    public WeekViewModel() {
        fetchWeekData();
    }

    private void fetchWeekData() {
        databaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<WeekData> tempWeekDataList = new ArrayList<>();
                for (DataSnapshot weekSnapshot : dataSnapshot.getChildren()) {
                    String selectedEmoji = weekSnapshot.child("selectedEmoji").getValue(String.class);
                    List<String> goals = (List<String>) weekSnapshot.child("goals").getValue();

                    // goals가 null일 경우 기본값을 빈 리스트로 설정합니다.
                    if (goals == null) {
                        goals = new ArrayList<>();
                    }

                    tempWeekDataList.add(new WeekData(weekSnapshot.getKey(), selectedEmoji, goals));
                }
                weekDataList.setValue(tempWeekDataList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateSelectedEmoji(String weekID, String selectedEmoji) {
        databaseRef.child(weekID).child("goals").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> goals = (List<String>) dataSnapshot.getValue();

                // 'goals' 필드에 아이템이 하나라도 있을 경우에만 이모지를 저장합니다.
                if (goals != null && !goals.isEmpty()) {
                    databaseRef.child(weekID).child("selectedEmoji").setValue(selectedEmoji);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 취소된 경우에 대한 처리를 여기에 작성합니다.
            }
        });
    }


    public void updateGoals(String weekID, List<String> goals) {
        databaseRef.child(weekID).child("goals").setValue(goals);
    }

    public MutableLiveData<List<WeekData>> getWeekDataList() {
        return weekDataList;
    }

    public static class WeekData {
        private String id;
        private String selectedEmoji;
        private List<String> goals;

        public WeekData(String id, String selectedEmoji, List<String> goals) {
            this.id = id;
            this.selectedEmoji = selectedEmoji;
            this.goals = goals;
        }

        public String getId() {
            return id;
        }

        public String getSelectedEmoji() {
            return selectedEmoji;
        }

        public List<String> getGoals() {
            return goals;
        }
    }
}
