package com.an.view;

public class Utils {


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
}

