# JVM 구조

![](https://images.velog.io/images/songs4805/post/02113f95-5b69-414c-a4c0-380f7334d3e9/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-12%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%203.42.34.png)

## 클래스 로더

- .class 파일에서 바이트코드를 읽고 메모리에 저장
- 로딩: 클래스를 읽어오는 과정
- 링크: 레퍼런스를 연결하는 과정
- 초기화: static 값들 초기화 및 변수에 할당

다음은 클래스 로더를 확인하기 위한 java파일 및 class 파일이다.  
`App.java`를 다음과 같이 작성해둔다.

```java
package me.ramos;

import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println(App.class.getClassLoader());
        System.out.println(List.class.getClassLoader());
    }
}
```

디컴파일 된 `App.class` 파일은 다음과 같다.

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.ramos;

import java.util.List;

public class App {
    public App() {
    }

    public static void main(String[] args) {
        System.out.println(App.class.getClassLoader());
        System.out.println(List.class.getClassLoader());
    }
}
```

실제 바이트코드는 다음과 같다.

![](https://images.velog.io/images/songs4805/post/fa73ebab-f8ec-448d-b946-961a0688a930/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-12%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%204.09.26.png)

## 메모리

- 메모리 영역
  - 클래스 수준의 정보(클래스 이름, 부모 클래스 이름, 메소드, 변수, package path)를 저장함
  - 공유 자원으로, 다른 영역에서도 참조 가능함
- 힙 영역
  - 객체(인스턴스)를 저장함
  - 공유 자원으로, 다른 영역에서도 참조 가능함
- 스택 영역
  - 쓰레드마다 런타임 스택을 만들고, 그 안에 메소드 호출을 스택 프레임이라 부르는 블럭으로 쌓는다.
  - 쓰레드 종료 시 런타임 스택도 함께 사라진다.
- PC(Program Counter) 레지스터
  - 쓰레드마다 쓰레드 내 현재 실행할 스택 프레임을 가리키는 포인터가 생성됨
  - 스택에 맞물려 생성됨
- 네이티브 메소드 스택
  - 네이티브 메소드를 호출할 때 사용하는 별도의 스택
  - 네이티브 메소드 라이브러리를 사용하기 위해 JNI를 사용하는 메소드 스택은 네이티브 메소드 스택에 저장된다.
  - 네이티브 메소드: 메소드에 Native라는 키워드가 붙어 있고, 그 구현을 Java가 아닌 C/C++로 구현을 한 상태 (ex. `Thread.currentThread()`)
  - [Java JVM Run-time Data Areas](https://javapapers.com/core-java/java-jvm-run-time-data-areas/#Program_Counter_PC_Register)

## 실행 엔진

- 인터프리터: 바이트 코드를 한줄 씩 실행하는 역할
  - 인터프리터가 이해할 수 있어야 하는데, 한 줄씩 실행하면서 네이티브 코드로 바꿔서 기계가 이해할 수 있게 만들어 실행한다.
  - 똑같은 코드가 여러 번 나와도 매번 실행하는 것은 비효율적이다.
- JIT 컴파일러: 바이트 코드를 네이티브 코드로 컴파일 해주는 역할
  - 인터프리터 효율을 높이기 위해, 인터프리터가 반복되는 코드를 발견하면 해당 코드 모두를 네이티브 코드로 바꿔둔다.
  - 이 후, 인터프리터가 해당 라인을 만나면 인터프리팅 하지 않고, 네이티브 코드를 바로 사용한다.
- GC(Garbage Collector): 더 이상 참조되지 않는 객체를 모아서 정리한다.
  - 이에 대해 잘 이해하고 있어야 하고, 경우에 따라 커스터마이징을 해야 할 수도 있다.(옵션 조정)
  - 프로파일링을 할때도 사용하는 GC가 어떤 GC인지
  - 애플리케이션을 실행하기 전에 우리가 사용할 GC를 선택해야 할 경우도 있음
- 대표적인 GC
  - throughput 위주
  - stop-the world 위주
    - 서버 운영 중에 많은 객체를 생성하고 반응 시간이 중요한 경우
    - GC를 할 때 발생하는 멈춤 현상을 최소화 할 수 있는 GC

## JNI(Java Native Interface)

- 자바 애플리케이션에서 C, C++, Assembly로 작성된 함수를 사용할 수 있는 방법 제공
- Native 키워드를 사용한 메소드 호출
- [A Simple Java Native Interface (JNI) example in Java and Scala](https://schlining.medium.com/a-simple-java-native-interface-jni-example-in-java-and-scala-68fdafe76f5f)

![](https://images.velog.io/images/songs4805/post/95416492-5662-4adf-afdc-21b581d3e61e/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-12%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%204.23.38.png)

## 네이티브 메소드 라이브러리

- C, C++로 작성된 라이브러리

## 전체적인 동작 방식

1. 바이트코드를 전달 받아 클래스 로더가 메모리 영역에 배치  
   → 힙, 메소드 영역에 적절하게 배치
2. 실행 엔진에서 쓰레드가 만들어짐에 따라 스택, PC, 네이티브 스택 생성  
   → JIT을 써서 컴파일이 되고 GC도 이루어짐
   → 만약 네이티브 메소드를 사용하게 되면 JNI를 통해 네이티브 메소드를 사용한다.

## 읽어볼 자료

- [How JVM Works – JVM Architecture?](https://www.geeksforgeeks.org/jvm-works-jvm-architecture/)
- [The JVM Architecture Explained](https://dzone.com/articles/jvm-architecture-explained)
- [JVM Internals](https://blog.jamesdbloom.com/JVMInternals.html)
