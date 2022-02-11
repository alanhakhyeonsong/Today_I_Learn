# 자바, JVM, JDK, JRE

![](https://images.velog.io/images/songs4805/post/0121e1b9-20de-4f5a-807a-17e69e00b2ef/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-12%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2012.12.30.png)

## JVM (Java Virtual Machine)

- 자바 가상 머신
- 자바 바이트 코드(.class 파일)를 OS에 특화된 코드로 변환(인터프리터와 JIT 컴파일러)하여 실행한다.
- 바이트 코드를 실행하는 표준(JVM 자체는 표준)이자 구현체(특정 밴더가 구현한 JVM)다.
- [JVM 스펙](https://docs.oracle.com/javase/specs/jvms/se11/html/)은 정해져 있고, 이에 맞춘 구현체가 다양하다. (ex. Oracle, Amazon, Azul 등)
- 특정 플랫폼에 종속적이다.  
  → 실행시 네이티브 코드로 변경을 하게 되는데 이를 OS에 맞추어 실행해야 되기 때문

## JRE (Java Runtime Environment): JVM + 라이브러리

- 자바 애플리케이션을 실행할 수 있도록 구성된 최소한의 구성이자 배포판.
- JVM과 핵심 라이브러리 및 자바 런타임 환경에서 사용하는 프로퍼티 세팅이나 리소스 파일을 가지고 있다.
- 개발 관련 도구는 포함하지 않는다. (이는 JDK에서 제공)
- 오라클은 Java 11 부터는 JDK만 제공하며 JRE를 따로 제공하지 않는다.

## JDK (Java Development Kit): JRE + 개발 툴

- 소스 코드를 작성할 때 사용하는 자바 언어는 플랫폼에 독립적이다.
- Write Once Run Anywhere

## Java

- 프로그래밍 언어
- JDK에 들어있는 자바 컴파일러(javac)를 사용하여 바이트코드(.class 파일)로 컴파일 할 수 있다.
- 자바 유료화?  
  → Oracle JDK 11 부터 상용으로 사용할 때 유료

## JVM 기반 언어

- JVM은 Java와 의존성이 타이트하지 않다.
- .class 파일만 있으면 실행이 되기 때문이다.
- 즉, 다른 언어에서 컴파일시 .class 파일이 만들어지거나 java 파일을 만들어주기만 하면 JVM을 활용 가능하다.
- 클로저, 그루비, JRuby, Jython, Kotlin, Scala, ...

다음은 위 내용을 확인하기 위한 .class 파일 관련 테스트이다.  
우선, 다음과 같이 java 파일을 하나 만든 뒤 컴파일을 해보고 실행을 한다. 그 결과 문제 없이 실행된다.

![](https://images.velog.io/images/songs4805/post/2dae595a-2e05-48c9-95b9-a5d5656fbbfb/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-11%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.24.13.png)

![](https://images.velog.io/images/songs4805/post/13116775-fb73-48fd-a84f-083625a291cd/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-11%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.25.11.png)

![](https://images.velog.io/images/songs4805/post/54fb0bb9-2ef3-4698-b32e-3b911c81fe22/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-11%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.28.04.png)

vi 에디터로 kotlin 파일을 하나 생성 한 뒤 컴파일을 한다. 이 후, `javap -c`로 해당 kotlin 파일을 컴파일 하면 .class로 생성되는데 이를 보면 java의 class 파일과 비슷한 구조임을 확인할 수 있다.

![](https://images.velog.io/images/songs4805/post/82a206ba-8680-42c2-b3d5-733293faca83/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-11%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.50.45.png)

또한 다음과 같이 kotlin 파일을 .jar 파일로 만들 수 있는데, 이 후 해당 파일을 `java -jar`로 실행하면 역시 정상적으로 동작한다.

![](https://images.velog.io/images/songs4805/post/492661b1-70a0-46d8-9f0b-75d4bbc87a32/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-12%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2012.24.54.png)

## 📌 참고

- [jar 파일 포맷](<https://ko.wikipedia.org/wiki/JAR_(%ED%8C%8C%EC%9D%BC_%ED%8F%AC%EB%A7%B7)>)
- [JIT 컴파일러](https://aboullaite.me/understanding-jit-compiler-just-in-time-compiler/)
