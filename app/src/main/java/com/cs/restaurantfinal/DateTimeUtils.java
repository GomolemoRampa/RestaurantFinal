package com.cs.restaurantfinal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
        public static String formatTime(long timestamp) {
            // Example: format to "dd MMM yyyy, hh:mm a"
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            Date date = new Date(timestamp);
            return sdf.format(date);
        }
}