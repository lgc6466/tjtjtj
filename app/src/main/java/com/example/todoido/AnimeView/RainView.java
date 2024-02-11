package com.example.todoido.AnimeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.todoido.R;

public class RainView extends View {

    private static final int NUM_SNOWFLAKES = 80;  // 눈송이 개수
    private static final long DELAY = 5L;  // 딜레이

    private RainFlake[] rainFlakes = new RainFlake[NUM_SNOWFLAKES];  // SnowFlake를 RainFlake로 변경

    public RainView(Context context) {
        super(context);
    }

    public RainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void resize(int width, int height) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.rain1);  // 눈송이 이미지를 가져옵니다.

        for (int i = 0; i < NUM_SNOWFLAKES; i++) {
            rainFlakes[i] = RainFlake.create(width, height, drawable);  // SnowFlake.create를 RainFlake.create로 변경
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            resize(w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (RainFlake rainFlake : rainFlakes) {  // SnowFlake를 RainFlake로 변경
            if (rainFlake != null) {
                rainFlake.draw(canvas);
            }
        }
        getHandler().postDelayed(runnable, DELAY);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };
}

