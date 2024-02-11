package com.example.todoido.AnimeView;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;


class RainFlake {
    private static final float ANGE_RANGE = 0.1f;
    private static final float HALF_ANGLE_RANGE = ANGE_RANGE / 2f;
    private static final float HALF_PI = (float) Math.PI / 2f;
    private static final float ANGLE_SEED = 25f;
    private static final float ANGLE_DIVISOR = 10000f;
    private static final float INCREMENT_LOWER = 2f;
    private static final float INCREMENT_UPPER = 4f;
    private static final float FLAKE_SIZE_LOWER = 15f;
    private static final float FLAKE_SIZE_UPPER = 40f;

    private final Random random;
    private final Point position;
    private float angle;
    private final float increment;
    private final float flakeSize;
    private final Drawable drawable;

    public static RainFlake create(int width, int height, Drawable drawable) {
        Random random = new Random();
        int x = random.getRandom(width);
        int y = random.getRandom(height);
        Point position = new Point(x, y);
        float increment = random.getRandom(INCREMENT_LOWER, INCREMENT_UPPER) * 5/2;
        float flakeSize = 12;
        return new RainFlake(random, position, increment, flakeSize, drawable);
    }

    RainFlake(Random random, Point position, float increment, float flakeSize, Drawable drawable) {
        this.random = random;
        this.position = position;
        this.increment = increment;
        this.flakeSize = flakeSize;
        this.drawable = drawable;
    }

    private void move(int width, int height) {
        double y = position.y + increment;
        position.set(position.x, (int) y);

        if (!isInside(width, height)) {
            reset(width);
        }
    }

    private boolean isInside(int width, int height) {
        int x = position.x;
        int y = position.y;
        return x >= -flakeSize - 1 && x + flakeSize <= width && y >= -flakeSize - 1 && y - flakeSize < height;
    }

    private void reset(int width) {
        position.x = random.getRandom(width);
        position.y = (int) (-flakeSize - 1);
    }

    public void draw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        move(width, height);

        // Drawable의 원본 너비와 높이를 가져옵니다.
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();

        // Drawable의 원본 비율을 유지하면서 크기를 조절합니다.
        int scaledWidth = (int) (intrinsicWidth * flakeSize / Math.max(intrinsicWidth, intrinsicHeight));
        int scaledHeight = (int) (intrinsicHeight * flakeSize / Math.max(intrinsicWidth, intrinsicHeight));

        // 눈송이의 위치를 눈송이 이미지의 중심으로 하려면 Drawable의 왼쪽 상단 위치를 눈송이 위치에서 Drawable의 절반 크기만큼 뺀 값으로 설정해야 합니다.
        int left = (int) (position.x - scaledWidth / 2f);
        int top = (int) (position.y - scaledHeight / 2f);
        int right = left + scaledWidth;
        int bottom = top + scaledHeight;

        drawable.setBounds(left, top, right, bottom);

        drawable.draw(canvas);
    }
}