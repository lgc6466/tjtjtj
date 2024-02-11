package com.example.todoido;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth Auth;
    ProgressDialog pd;
    EditText et_id, et_pass;
    Button btn_login, btn_register;
    private boolean isChecked = false;
    private boolean isAutoLoginChecked = false;
    ImageButton eye, checkbox, checkbox2;
    private boolean isEyeOff = true;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // activity_login은 레이아웃 XML 파일의 이름이어야 합니다.

        et_id = findViewById(R.id.et_id);
        btn_login = findViewById(R.id.btn_login);
        et_pass = findViewById(R.id.et_pass);
        btn_register = findViewById(R.id.btn_register);
        checkbox = findViewById(R.id.checkbox);
        checkbox2 = findViewById(R.id.checkbox2);
        eye = findViewById(R.id.eye);

        Auth = FirebaseAuth.getInstance();

        // SharedPreferences에서 로그인 정보와 체크박스 상태를 불러옵니다.
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String autoLoginId = sharedPreferences.getString("autoLoginId", "");
        String autoLoginPassword = sharedPreferences.getString("autoLoginPassword", "");
        isAutoLoginChecked = sharedPreferences.getBoolean("isAutoLoginChecked", false);
        // 저장된 로그인 정보가 있고, 체크박스가 체크되어 있는 경우
        if(isAutoLoginChecked && !TextUtils.isEmpty(autoLoginId) && !TextUtils.isEmpty(autoLoginPassword)) {
            // 로그인 과정을 건너뛰고 바로 메인 화면으로 이동합니다.
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_id = et_id.getText().toString();
                String str_password = et_pass.getText().toString();

                // 체크박스 상태에 따라 아이디를 저장
                if(isChecked) {
                    editor.putString("id", str_id);
                    editor.putBoolean("isChecked", true);
                } else {
                    editor.remove("id");
                    editor.putBoolean("isChecked", false);
                }

                if(isAutoLoginChecked) {
                    editor.putString("autoLoginId", str_id);
                    editor.putString("autoLoginPassword", str_password); // 비밀번호는 암호화하여 저장하는 것이 좋습니다.
                    editor.putBoolean("isAutoLoginChecked", true);
                } else {
                    editor.remove("autoLoginId");
                    editor.remove("autoLoginPassword");
                    editor.putBoolean("isAutoLoginChecked", false);
                }
                editor.apply();

                if (TextUtils.isEmpty(str_id) || TextUtils.isEmpty(str_password)) {
                    Toast.makeText(LoginActivity.this, "모든 입력란을 채워주세요", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    ref.orderByChild("id").equalTo(str_id.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                    // 아이디에 해당하는 이메일을 가져옵니다.
                                    String email = snapshot.child("email").getValue(String.class);
                                    Auth.signInWithEmailAndPassword(email, str_password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        });

        EditText editText = findViewById(R.id.et_id);
        MaterialCardView cardView = findViewById(R.id.id_cardview);

        cardView.setStrokeColor(Color.parseColor("#FFDA29"));
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    cardView.setStrokeWidth(4);
                } else {
                    cardView.setStrokeWidth(2);
                }
            }
        });

        EditText passwordEditText = findViewById(R.id.et_pass);
        MaterialCardView passwordCardView = findViewById(R.id.pw_cardview);

        passwordCardView.setStrokeColor(Color.parseColor("#FFDA29"));
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    passwordCardView.setStrokeWidth(4);
                } else {
                    passwordCardView.setStrokeWidth(2);
                }
            }
        });

        checkbox.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
        checkbox2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // 체크박스 상태와 아이디를 불러옴
        isChecked = sharedPreferences.getBoolean("isChecked", false);
        isAutoLoginChecked = sharedPreferences.getBoolean("isAutoLoginChecked", false);
        String savedId = sharedPreferences.getString("id", "");

        // 체크박스 상태에 따라 아이디를 설정하고 체크박스 모양 변경
        if(isChecked) {
            et_id.setText(savedId);
            checkbox.setBackgroundResource(R.drawable.checked);
        } else {
            checkbox.setBackgroundResource(R.drawable.unchecked);
        }

        if(isAutoLoginChecked) {
            checkbox2.setBackgroundResource(R.drawable.checked);
        } else {
            checkbox2.setBackgroundResource(R.drawable.unchecked);
        }

        //체크박스 모양 버튼
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked) {
                    checkbox.setBackgroundResource(R.drawable.unchecked);
                } else {
                    checkbox.setBackgroundResource(R.drawable.checked);
                }
                isChecked = !isChecked;

                // 체크박스 상태를 SharedPreferences에 저장
                editor.putBoolean("isChecked", isChecked);
                editor.apply();
            }
        });

        checkbox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAutoLoginChecked) {
                    checkbox2.setBackgroundResource(R.drawable.unchecked);
                } else {
                    checkbox2.setBackgroundResource(R.drawable.checked);
                }
                isAutoLoginChecked = !isAutoLoginChecked;

                // 체크박스 상태를 SharedPreferences에 저장
                editor.putBoolean("isAutoLoginChecked", isAutoLoginChecked);
                editor.apply();
            }
        });


        // 보이기 숨기기 이미지버튼
        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEyeOff) {
                    // 비밀번호 보이기
                    eye.setImageResource(R.drawable.eye);
                    et_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // 비밀번호 숨기기
                    eye.setImageResource(R.drawable.eyeoff);
                    et_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                isEyeOff = !isEyeOff;
                et_pass.setSelection(et_pass.getText().length()); // 커서를 마지막으로 이동
            }
        });
    }
}

