# Contents

- [JUnit 5 소개](#JUnit-5-소개)
- [JUnit 5 시작하기](#JUnit-5-시작하기)

# JUnit 5 소개

- Java 개발자가 가장 많이 사용하는 테스팅 프레임워크
- Java 8 이상을 필요로 한다.
- 대체제: TestNG, Spock, ...

![](https://images.velog.io/images/songs4805/post/0a4f8766-62f2-4cf2-b6c4-fc763885ba80/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-18%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.25.20.png)

- Platform: 테스트를 실행해주는 런처 제공. TestEngine API 제공
- Jupiter: TestEngine API 구현체로 JUnit 5를 제공
- Vintage: JUnit 4와 3을 지원하는 TestEngine 구현체
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

# JUnit 5 시작하기

Spring Boot 2.2+ 버전 프로젝트를 만든다면 기본으로 JUnit 5 의존성이 추가된다.

Spring Boot Project가 아닌 경우 다음과 같이 의존성을 추가하면 된다.

- `build.gradle`

```groovy
testImplementation(platform("org.junit:junit-bom:5.8.2"))
testRuntimeOnly("org.junit.platform:junit-platform-launcher") {
  because("Only needed to run tests in a version of IntelliJ IDEA that bundles older versions")
}
testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
```

- `pom.xml`

```xml
<!-- ... -->
<dependencies>
    <!-- Only needed to run tests in a version of IntelliJ IDEA that bundles older versions -->
    <dependency>
        <groupId>org.junit.platform</groupId>
        <artifactId>junit-platform-launcher</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.vintage</groupId>
        <artifactId>junit-vintage-engine</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.junit</groupId>
            <artifactId>junit-bom</artifactId>
            <version>5.8.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

기본 애노테이션은 다음과 같다.

- `@Test`
- `@BeforeAll`, `@AfterAll`: static void 여야 함. 모든 테스트 이전/이후 딱 한번만 실행됨.
- `@BeforeEach`, `@AfterEach`: 모든 테스트 이전/이후마다 한번씩 실행됨.
- `@Disabled`: 테스트를 무시하고 싶을 때 사용한다.

테스트 코드는 다음과 같다.  
`AppTest.java`

```java
package me.ramos.prejavatest;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @Test
    void create() { // public이 아니어도 test 가능함(JUnit 5의 Java Reflection 때문)
        App app = new App();
        assertNotNull(app);
        System.out.println("create");
    }

    @Test
    @Disabled
    void create1() {
        System.out.println("create1");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("before all");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("after all");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("before each");
    }

    @AfterEach
    void afterEach() {
        System.out.println("after each");
    }
}
```

![](https://images.velog.io/images/songs4805/post/f6d68f0f-6180-47b3-9c01-5764dae9b80b/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-18%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.45.54.png)

![](https://images.velog.io/images/songs4805/post/e669552c-ee84-4e79-a5cc-32b4410cf060/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-18%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.46.38.png)
