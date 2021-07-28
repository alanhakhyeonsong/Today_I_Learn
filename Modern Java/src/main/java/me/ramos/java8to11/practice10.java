package me.ramos.java8to11;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class practice10 {

    public static void main(String[] args) throws InterruptedException {
//        Date date = new Date();
//        long time = date.getTime();
//        System.out.println(date);
//        System.out.println(time);
//
//        Thread.sleep(1000 * 3);
//        Date after3Seconds = new Date();
//        System.out.println(after3Seconds);
//        after3Seconds.setTime(time);
//        System.out.println(after3Seconds);
        /**
         * java.util.Date를 사용한 위 코드는 mutable 하기 때문에 thread safe 하지 않다.
         * 클래스 이름이 명확하지 않다.(Date인데 시간까지 다룸)
         * 버그 발생할 여지가 많다. (타입 안정성이 없고, 월이 0부터 시작한다거나)
         * 날짜 시간 처리가 복잡한 애플리케이션에서는 보통 Joda Time을 쓰곤했다.
         * => Java 8에 다음과 같은 새로운 날짜/시간 API가 나오게 됨.
         */

        // 기계 시간
        Instant instant = Instant.now();
        System.out.println(instant); // 기준시 UTC, GMT

        ZoneId zone = ZoneId.systemDefault();
        System.out.println(zone);
        ZonedDateTime zonedDateTime = instant.atZone(zone);
        System.out.println(zonedDateTime);

        // 인류용 시간
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        LocalDateTime birthDay = LocalDateTime.of(1996, Month.JANUARY, 19, 0, 0, 0);
        ZonedDateTime nowInUsa = ZonedDateTime.now(ZoneId.of("America/New_York"));
        System.out.println(nowInUsa);

        DateTimeFormatter MMddyyyy = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        System.out.println(now.format(MMddyyyy));

        LocalDate parse = LocalDate.parse("01/19/1996", MMddyyyy);
        System.out.println(parse);

        Instant nowInstant = Instant.now();
        ZonedDateTime zonedDateTime1 = nowInstant.atZone(ZoneId.of("Asia/Tokyo"));
        System.out.println(zonedDateTime1);

        LocalDate today = LocalDate.now();
        LocalDate nextYearBirthday = LocalDate.of(2022, Month.JANUARY, 19);

        Period period = Period.between(today, nextYearBirthday);
        System.out.println(period.getDays());

        Period until = today.until(nextYearBirthday);
        System.out.println(until.get(ChronoUnit.MONTHS));
    }
}
