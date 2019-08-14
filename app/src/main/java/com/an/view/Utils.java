package com.an.view;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

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

    /**
     * Color 到十六进制的转换
     *
     * @param color
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String colorToHexValue(Color color) {
        return intToHexValue((int) color.alpha()) + intToHexValue((int) color.red()) + intToHexValue((int) color.green()) + intToHexValue((int) color.blue());
    }

    /**
     * int 到十六进制的转换
     *
     * @param number
     * @return
     */
    public static String intToHexValue(int number) {
        String result = Integer.toHexString(number & 0xff);
        while (result.length() < 2) {
            result = "0" + result;
        }
        return result.toUpperCase();
    }

    /**
     * 十六进制到 Color 的转换
     *
     * @param str
     * @return
     */
    public static int fromStrToARGB(String str) {
        String str1 = str.substring(0, 2);
        String str2 = str.substring(2, 4);
        String str3 = str.substring(4, 6);
        String str4 = str.substring(6, 8);
        int alpha = Integer.parseInt(str1, 16);
        int red = Integer.parseInt(str2, 16);
        int green = Integer.parseInt(str3, 16);
        int blue = Integer.parseInt(str4, 16);
        return Color.argb(alpha, red, green, blue);  // new Color(red, green, blue, alpha);
    }

    public static int fromStrToRGB(String str) {
        String redStr = str.substring(0, 2);
        String greenStr = str.substring(2, 4);
        String blueStr = str.substring(4, 6);
        return Color.rgb(Integer.parseInt(redStr), Integer.parseInt(greenStr), Integer.parseInt(blueStr));
    }
}

