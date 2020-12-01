package dev.fr13.util;

import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class DateConverter {

    public Date minusDaysFromNow(int number) {
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -number);
        return c.getTime();
    }
}