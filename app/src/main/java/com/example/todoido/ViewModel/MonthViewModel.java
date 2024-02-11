package com.example.todoido.ViewModel;

import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.todoido.Adapter.CardAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MonthViewModel extends ViewModel {
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final DatabaseReference databaseRef = firebaseUser != null ? FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Month").child("items") : null;
    private final MutableLiveData<List<MonthItem>> itemList = new MutableLiveData<>();
    private final MutableLiveData<String> theme = new MutableLiveData<>();  // 테마를 저장하는 LiveData 객체

    public MonthViewModel() {
        fetchItems();
        fetchTheme();  // 테마를 가져오는 메서드를 호출
    }

    private void fetchItems() {
        databaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MonthItem> tempItemList = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String id = childSnapshot.getKey();
                    String content = childSnapshot.child("content").getValue(String.class);
                    int viewType = childSnapshot.child("viewType").getValue(Integer.class);
                    String imageUriStr = childSnapshot.child("imageUri").getValue(String.class);
                    Uri imageUri = TextUtils.isEmpty(imageUriStr) ? null : Uri.parse(imageUriStr);

                    tempItemList.add(new MonthItem(id, content, viewType, imageUri));
                }
                itemList.setValue(tempItemList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchTheme() {
        DatabaseReference themeRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("theme");
        themeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String themeValue = dataSnapshot.getValue(String.class);
                theme.setValue(themeValue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateItem(String itemID, String content, int viewType, Uri imageUri) {
        databaseRef.child(itemID).child("content").setValue(content);
        databaseRef.child(itemID).child("viewType").setValue(viewType);
        databaseRef.child(itemID).child("imageUri").setValue(imageUri == null ? "" : imageUri.toString());
    }

    public String addItem(CardAdapter.CardItem item) {
        String id = databaseRef.push().getKey();
        if (id != null) {
            MonthItem monthItem = new MonthItem(id, item.getContent(), item.getViewType(), item.getImageUri());
            databaseRef.child(id).setValue(monthItem);
        }
        return id;
    }


    public void removeItem(String id) {
        databaseRef.child(id).removeValue();
    }

    public MutableLiveData<List<MonthItem>> getItemList() {
        return itemList;
    }
    public MutableLiveData<String> getTheme() {
        return theme;  // 테마를 가져오는 메서드
    }

    public static class MonthItem {
        private String id;
        private String content;
        private int viewType;
        private Uri imageUri;

        public MonthItem(String id, String content, int viewType, Uri imageUri) {
            this.id = id;
            this.content = content;
            this.viewType = viewType;
            this.imageUri = imageUri;
        }

        public String getId() {
            return id;
        }

        public String getContent() {
            return content;
        }

        public int getViewType() {
            return viewType;
        }

        public Uri getImageUri() {
            return imageUri;
        }
    }
}
