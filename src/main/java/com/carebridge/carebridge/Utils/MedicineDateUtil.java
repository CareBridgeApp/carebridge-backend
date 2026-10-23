package com.carebridge.carebridge.Utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component
public class MedicineDateUtil {
    public LocalDate calculateEndDate(LocalDate startDate,String duration) {
        if(startDate==null||duration==null) {
            throw new IllegalArgumentException("Start date and duration cannot be null");
        }
        Matcher matcher= Pattern.compile("(\\d+)\\s*(days?|weeks?|months?)",Pattern.CASE_INSENSITIVE)
                .matcher(duration.trim());
        if(!matcher.matches()){
            throw new IllegalArgumentException("Invalid duration");
        }
        long amount=Long.parseLong(matcher.group(1));
        String unit=matcher.group(2).toLowerCase(Locale.ROOT);
        if(amount<=0){
            throw new IllegalArgumentException("duration cannot be less than 1");
        }
        return switch (unit) {
            case "day", "days" ->
                    startDate.plusDays(amount);
            case "week", "weeks" ->
                    startDate.plusWeeks(amount);
            case "month", "months" ->
                    startDate.plusMonths(amount);
            default -> throw new IllegalArgumentException(
                    "Unsupported duration unit");
        };
    }
}
