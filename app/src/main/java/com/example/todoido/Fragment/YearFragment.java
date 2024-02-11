package com.example.todoido.Fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;

import com.example.todoido.AnimeView.FlowerView;
import com.example.todoido.AnimeView.LeaveView;
import com.example.todoido.AnimeView.RainView;
import com.example.todoido.AnimeView.SnowView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.todoido.R;

import androidx.lifecycle.ViewModelProvider;

import com.example.todoido.ViewModel.YearViewModel;

public class YearFragment extends Fragment {
    private YearViewModel yearViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_year, container, false);
        LinearLayout layout = view.findViewById(R.id.yearlayout);

        yearViewModel = new ViewModelProvider(this).get(YearViewModel.class);

        for (int i = 1; i <= 10; i++) {
            View itemView = view.findViewById(getResources().getIdentifier("item" + i, "id", getActivity().getPackageName()));
            CheckBox checkBox = itemView.findViewById(R.id.checkBox1);
            EditText editText = itemView.findViewById(R.id.editText1);

            final String itemID = "item" + i;

            yearViewModel.getItemList().observe(getViewLifecycleOwner(), items -> {
                for (YearViewModel.Item item : items) {
                    if (item.getId().equals(itemID)) {
                        checkBox.setChecked(item.getChecked());
                        editText.setText(item.getText());
                    }
                }
            });

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                yearViewModel.updateItemCheckStatus(itemID, isChecked);
            });

            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    yearViewModel.updateItemText(itemID, editText.getText().toString());
                }
            });
        }

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

