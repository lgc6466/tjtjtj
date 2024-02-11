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

public class YearViewModel extends ViewModel {
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final DatabaseReference databaseRef = firebaseUser != null ? FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Year").child("items") : null;
    private final MutableLiveData<List<Item>> itemList = new MutableLiveData<>();

    public YearViewModel() {
        fetchItems();
    }

    private void fetchItems() {
        List<Item> tempItemList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            final String itemID = "item" + i;
            databaseRef.child(itemID).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean checked = dataSnapshot.child("checked").getValue(Boolean.class);

                    // checked가 null일 경우 기본값을 false로 설정합니다.
                    if (checked == null) {
                        checked = false;
                    }

                    String text = dataSnapshot.child("text").getValue(String.class);
                    tempItemList.add(new Item(itemID, checked, text));
                    itemList.setValue(tempItemList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }



    public void updateItemCheckStatus(String itemID, boolean isChecked) {
        databaseRef.child(itemID).child("checked").setValue(isChecked);
    }

    public void updateItemText(String itemID, String text) {
        databaseRef.child(itemID).child("text").setValue(text);
    }

    public MutableLiveData<List<Item>> getItemList() {
        return itemList;
    }

    public static class Item {
        private String id;
        private Boolean checked;
        private String text;

        public Item(String id, Boolean checked, String text) {
            this.id = id;
            this.checked = checked;
            this.text = text;
        }

        public String getId() {
            return id;
        }

        public Boolean getChecked() {
            return checked;
        }

        public String getText() {
            return text;
        }
    }
}

