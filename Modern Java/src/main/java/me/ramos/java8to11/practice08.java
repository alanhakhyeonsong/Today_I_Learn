package me.ramos.java8to11;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class practice08 {

    public static void main(String[] args) {
        List<OnlineClass> springClasses = new ArrayList<>();
        springClasses.add(new OnlineClass(1, "spring boot", true));
        springClasses.add(new OnlineClass(2, "spring data jpa", true));
        springClasses.add(new OnlineClass(3, "spring mvc", false));
        springClasses.add(new OnlineClass(4, "spring core", false));
        springClasses.add(new OnlineClass(5, "rest api development", false));

        OnlineClass spring_boot = new OnlineClass(1, "spring boot", true);
        // 다음은 에러를 만들기 좋은 코드
        // (null check에 대해 실수 가능)
//        Progress progress = spring_boot.getProgress();
//        if (progress != null) {
//            System.out.println(progress.getStudyDuration());
//        }
        Optional<OnlineClass> optional = springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("jpa"))
                .findFirst();
/**
 * optional.get() 보단 다른 메소드들을 활용하도록 하자.
 */
        // 다음 두 코드는 같다.
        Optional<Progress> progress = optional.flatMap(OnlineClass::getProgress);

        Optional<Optional<Progress>> progress1 = optional.map(OnlineClass::getProgress);
        Optional<Progress> progress2 = progress1.orElseThrow();


//        Optional<OnlineClass> onlineClass = optional.filter(OnlineClass::isClosed);

//        System.out.println(onlineClass.isEmpty());
//        boolean present = optional.isPresent();
//        System.out.println(present);
//
//        optional.ifPresent(oc -> System.out.println(oc.getTitle()));
//
//        OnlineClass onlineClass = optional.orElse(createNewClasses());
//        OnlineClass onlineClass = optional.orElseGet(practice08::createNewClasses);
//        OnlineClass onlineClass = optional.orElseThrow(IllegalStateException::new);
//        System.out.println(onlineClass.getTitle());

    }

    private static OnlineClass createNewClasses() {
        System.out.println("creating new online class");
        return new OnlineClass(10, "New class", false);
    }
}
