package com.example.demo;

import com.example.demo.repository.Child;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Calendar.*;
import static java.util.Calendar.DATE;

public class Utils {
    public static final long KFC_AGE_THRESHOLD = 12;
    public static final long SR_KFC_AGE_THRESHOLD = 8;
    public static final int KFC_SR_CAMP_ALLOWED_PARTICIPANTS = 30;

    public static boolean isSrKfc(Child child) {
        return getAge(child) >= SR_KFC_AGE_THRESHOLD;
    }

    public static int getAge(Child child) {
        if (child.getBirth_date() != null) {
            return getDiffYears(child.getBirth_date(), new Date());
        }
        return 0;
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    private static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }
}
