package com.example.todoido.ViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DayTask {
    private String id;
    private String date;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String text;
    private String spinnerSelection;
    private boolean isChecked;
    private boolean isHeader;

    public DayTask() {
        // 기본 생성자
    }

    public DayTask(String date) {
        this.date = date;
        this.dayOfWeek = getDayOfWeek(date);
        this.isHeader = true; // 헤더를 생성하는 경우에만 true
    }

    public DayTask(String date, String startTime, String endTime, String text, String spinnerSelection, boolean isChecked) {
        this(date); // 생성자 체이닝을 사용하여 중복 코드 제거
        this.startTime = startTime;
        this.endTime = endTime;
        this.text = text;
        this.spinnerSelection = spinnerSelection;
        this.isChecked = isChecked;
        this.isHeader = false;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getText() {
        return text;
    }

    public String getSpinnerSelection() {
        return spinnerSelection;
    }

    public boolean isChecked() {
        return isChecked;
    }

    // Getter 및 Setter 추가
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        this.dayOfWeek = getDayOfWeek(date);
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }
    public static String getDayOfWeek(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
            Date parsedDate = sdf.parse(date);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            // Calendar.DAY_OF_WEEK의 값: 일요일(1), 월요일(2), ..., 토요일(7)
            // 여기서는 간단히 '월요일', '화요일', ..., '일요일'로 반환
            switch (dayOfWeek) {
                case Calendar.SUNDAY:
                    return "일요일";
                case Calendar.MONDAY:
                    return "월요일";
                case Calendar.TUESDAY:
                    return "화요일";
                case Calendar.WEDNESDAY:
                    return "수요일";
                case Calendar.THURSDAY:
                    return "목요일";
                case Calendar.FRIDAY:
                    return "금요일";
                case Calendar.SATURDAY:
                    return "토요일";
                default:
                    return "";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // 날짜 형식이 잘못된 경우 빈 문자열 반환
        }
    }

}
