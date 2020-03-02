package com.epsilon.agilityevents.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.util.CollectionUtils;

public class RulesUtils {

    private RulesUtils() {
    }

    public static String buildUUID() {
        return UUID.randomUUID().toString();
    }

    public static boolean messageCountIsWithinLimit(Object inputItems, int limit, int days) {
        List<LocalDateTime> messageSendDates = getDatesFromObject(inputItems);
        if (CollectionUtils.isNullOrEmpty(messageSendDates))
            return false;
        // Returns a boolean of whether the limit of messages sent has been met or exceeded
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        long messageCount = messageSendDates.stream()
                .filter(messageSentDate -> (Duration.between(messageSentDate, now).toDays() <= days)).count();
        return limit >= messageCount;
    }

    public static long getOccurrencesForPeriod(Object inputDates, int period, ChronoUnit unit) {
        long count = 0;
        List<LocalDateTime> dates = getDatesFromObject(inputDates);
        if (CollectionUtils.isNullOrEmpty(dates))
            return count;
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Stream<LocalDateTime> dateStream = dates.stream();
        switch (unit) {
            case SECONDS:
                count = dateStream.filter(messageSentDate -> (Duration.between(messageSentDate, now).toMillis () / 1000 <= period)).count();
                break;
            case MINUTES:
                count = dateStream.filter(messageSentDate -> (Duration.between(messageSentDate, now).toMinutes() <= period)).count();
                break;
            case HOURS:
                count = dateStream.filter(messageSentDate -> (Duration.between(messageSentDate, now).toHours() <= period)).count();
                break;
            case DAYS:
                count = dateStream.filter(messageSentDate -> (Duration.between(messageSentDate, now).toDays() <= period)).count();
                break;
            case WEEKS:
                count = dateStream.filter(messageSentDate -> (Duration.between(messageSentDate, now).toDays() / 7 <= period)).count();
                break;
            case MONTHS:
                count = dateStream.filter(messageSentDate -> (Duration.between(messageSentDate, now).toDays() / 30 <= period)).count();
                break;
            case FOREVER:
            default:
                count = dateStream.count();
        }
        return count;
    }

    public static boolean messageWithinTimeLimit(Object inputItems, int hours) {
        List<LocalDateTime> messageSendDates = getDatesFromObject(inputItems);
        if (CollectionUtils.isNullOrEmpty(messageSendDates))
            return false;
        // Returns a boolean of whether a message was sent in the past given hours
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime mostRecentMessage = Collections.max(messageSendDates);
        return Duration.between(mostRecentMessage, now).toHours() <= hours;
    }

    private static List<LocalDateTime> getDatesFromObject(Object inputItems) {
        @SuppressWarnings("unchecked")
        List<Long> input = (List<Long>) inputItems;
        if (CollectionUtils.isNullOrEmpty(input))
            return Collections.emptyList();
        return input.stream().map(item -> LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) item), ZoneId.of("UTC"))).collect(Collectors.toList());
    }

    public static boolean anyMatchIgnoreCase(String matchingStr, List<String> targetLst) {
        return targetLst.stream().anyMatch(matchingStr::equalsIgnoreCase);
    }

    public static boolean isTimeStampWithinRange(Long eventTime, Long startTime, Long endTime){
       return (eventTime >= startTime && eventTime <= endTime);
    }

}
