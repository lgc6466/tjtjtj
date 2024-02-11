package com.example.todoido.AnimeView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.example.todoido.R;

import java.util.Random;

public class SnowingActivity extends AppCompatActivity implements Handler.Callback {

    private static final int SNOWING_MESSAGE_ID = 10;

    private boolean isSnowing = true;
    private Handler delayedSnowing = new Handler(Looper.getMainLooper(), this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snowing);

        delayedSnowing.sendEmptyMessageDelayed(SNOWING_MESSAGE_ID, 100);
    }

    private void snowing(AppCompatImageView imageView) {
        ViewGroup container = null;
        container.addView(imageView);

        float startPointX = new Random().nextFloat() * container.getWidth();
        float endPointX = new Random().nextFloat() * container.getWidth();
        ObjectAnimator moverX = ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, startPointX, endPointX);

        float snowHeight = imageView.getMeasuredHeight() * imageView.getScaleY();
        float startPointY = -snowHeight;
        float endPointY = container.getHeight() + snowHeight;
        ObjectAnimator moverY = ObjectAnimator.ofFloat(imageView, View.TRANSLATION_Y, startPointY, endPointY);
        moverY.setInterpolator(new AccelerateInterpolator(1f));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(moverX, moverY);
        set.setDuration((long) (Math.random() * 3000 + 3000));
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                container.removeView(imageView);
            }
        });
        set.start();
    }


    private AppCompatImageView makeSnowObject() {
        AppCompatImageView imageView = new AppCompatImageView(this);
        Drawable snowDrawable = ContextCompat.getDrawable(this, R.drawable.snowflake);
        imageView.setImageDrawable(snowDrawable);

        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 뷰가 완전히 그려진 후에 애니메이션을 시작합니다.
                snowing(imageView);

                // 리스너를 제거합니다.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        return imageView;
    }


    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == SNOWING_MESSAGE_ID && isSnowing) {
            AppCompatImageView imageView = makeSnowObject();
            snowing(imageView);
            delayedSnowing.sendEmptyMessageDelayed(
                    SNOWING_MESSAGE_ID,
                    (long) (new Random().nextFloat() * 1 + 1800)
            );
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSnowing = false;
        delayedSnowing.removeCallbacksAndMessages(null);
    }
}
