package com.an.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 判断点是否在矩形内（在边上也算）
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param x
     * @param y
     * @return
     */
    public static boolean IsPointInRect(int startX, int startY, int endX, int endY, int x, int y) {
        if (x >= startX && x <= endX && y >= startY && y <= endY) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查是否过期
     *
     * @return
     */
    public static boolean checkValid() {
        long currentTime = System.currentTimeMillis();
        String timeNow = Utils.sdf.format(currentTime);
        boolean outofdate = false;
        try {
            Date lisenseDate = Utils.sdf.parse("2020-04-1 00:00:00");  // April Fools' Day
            Date currDate = Utils.sdf.parse(timeNow);
            if (currDate.compareTo(lisenseDate) > 0) {
                outofdate = true;
            }

            return !outofdate;
        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
    }
}

