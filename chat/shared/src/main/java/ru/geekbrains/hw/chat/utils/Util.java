package ru.geekbrains.hw.chat.utils;

public class Util {
    @SafeVarargs
    public static <T> T nvl(T... vals) {
        for (T val : vals) {
            if (val != null) {
                return val;
            }
        }
        return null;
    }
}
