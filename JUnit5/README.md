# Contents

- [JUnit 5 소개](#JUnit-5-소개)
- [JUnit 5 시작하기](#JUnit-5-시작하기)
- [테스트 이름 표시하기](#테스트-이름-표시하기)
- [Assertion](#Assertion)
- [조건에 따라 테스트 실행하기](#조건에-따라-테스트-실행하기)
- [태깅과 필터링](#태깅과-필터링)
- [커스텀 태그](#커스텀-태그)
- [테스트 반복하기](#테스트-반복하기)

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

# 테스트 이름 표시하기

다음 2가지만 알고 있어도 충분하다.

`@DisplayNameGeneration`

- Method와 Class 레퍼런스를 사용해서 테스트 이름을 표기하는 방법 설정
- 기본 구현체로 ReplaceUnderscores 제공

![](https://images.velog.io/images/songs4805/post/5ae493d1-0440-469f-bf29-e0fc378af404/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-18%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.57.01.png)

`@DisplayName`

- 어떤 테스트인지 테스트 이름을 보다 쉽게 표현할 수 있는 방법을 제공하는 애노테이션
- `@DisplayNameGeneration` 보다 우선순위가 높다.
- 보다 권장되는 방식임

![](https://images.velog.io/images/songs4805/post/253f0345-61db-42c1-bbb6-3724a586925d/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-18%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.58.08.png)

```java
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AppTest {

    @Test
    @DisplayName("스터디 만들기")
    void create_new_study() {
        App app = new App();
        assertNotNull(app);
        System.out.println("create");
    }

    @Test
    void create_new_study_again() {
        System.out.println("create1");
    }
    ...
}
```

# Assertion

`org.junit.jupiter.api.Assertions.*`

- `assertEquals(expected, actual)`: 실제 기대한 값과 같은지 확인
- `assertNotNull(actual)`: 값이 null이 아닌지 확인
- `assertTrue(boolean)`: 다음 조건이 참(true)인지 확인
- `assertAll(executables...)`: 모든 확인 구문 확인
- `assertThrows(expectedType, executable)`: 예외 발생 확인
- `assertTimeout(duration, executable)`: 특정 시간 안에 실행이 완료되는지 확인

마지막 매개변수로 `Supplier<String>` 타입의 인스턴스를 람다 형태로 제공할 수 있다.  
→ 복잡한 메시지 생성해야 하는 경우 사용하면 실패한 경우에만 해당 메시지를 만들게 할 수 있다.

다음 세 코드가 같은 의미를 가진다.

```java
assertEquals(StudyStatus.DRAFT, study.getStatus(), "스터디를 처음 만들면 상태값이 DRAFT여야 한다.");

assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "스터디를 처음 만들면 상태값이 DRAFT여야 한다.");

assertEquals(StudyStatus.DRAFT, study.getStatus(), new Supplier<String>() {
    @Override
    public String get() {
        return "스터디를 처음 만들면 상태값이 DRAFT여야 한다.";
    }
});
```

각 사용법에 대한 예제는 다음과 같다.

```java
@Test
@DisplayName("스터디 만들기")
void create_new_study() {
    Study study = new Study(8);

    assertAll(
            () -> assertNotNull(study),
            () -> assertEquals(StudyStatus.DRAFT, study.getStatus(), "스터디를 처음 만들면 상태값이 DRAFT여야 한다."),
            () -> assertTrue(study.getLimit() > 0, "스터디 최대 참석 가능 인원은 0보다 커야 한다.")
    );
}

@Test
@DisplayName("스터디 만들기 \uD83D\uDE31")
void create_new_study_again() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new Study(-10));
    assertEquals("limit은 0보다 커야 한다.", exception.getMessage());
}
```

```java
assertTimeout(Duration.ofMillis(100), () -> {
            new Study(10);
            Thread.sleep(300);
        });
```

[**AssertJ**](https://joel-costigliola.github.io/assertj/), [Hemcrest](https://hamcrest.org/JavaHamcrest/), [Truth](https://truth.dev/) 등의 라이브러리를 사용할 수도 있다.

# 조건에 따라 테스트 실행하기

특정한 조건을 만족하는 경우에 테스트를 실행하는 방법이다.

`org.junit.jupiter.api.Assumptions.*`

- assumeTure(조건)
- assumingThat(조건, 테스트)

`@Enabled_`, `@Disabled_`

- OnOs
- OnJre
- IfSystemProperty
- IfEnvironmentVariable
- if

```java
@Test
@DisplayName("스터디 만들기")
@EnabledOnOs({OS.MAC, OS.LINUX})
void create_new_study() {
    String test_env = System.getenv("TEST_ENV");
    assumeTrue("LOCAL".equalsIgnoreCase(test_env));

    assumingThat("LOCAL".equalsIgnoreCase(test_env), () -> {
        System.out.println("ramos");
        Study actual = new Study(10);
        Assertions.assertTrue(actual.getLimit() > 0);
    });
}

@Test
@DisplayName("스터디 만들기 \uD83D\uDE31")
@DisabledOnOs(OS.MAC)
void create_new_study_again() {
    System.out.println("create1");
}
```

참고로 환경변수 설정 과정은 다음과 같다.

```bash
vim ~/.zshrc
```

![](https://images.velog.io/images/songs4805/post/1a700add-a698-4fe0-bacf-3acd17e38de3/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-21%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.12.42.png)

이 후 IntelliJ를 껐다 다시 킨 뒤 테스트를 실행하면 위 예제 코드의 첫 번째 테스트 함수가 실행된다.

# 태깅과 필터링

테스트 그룹을 만들고 원하는 테스트 그룹만 테스트를 실행할 수 있는 기능

`@Tag`

- 테스트 메소드에 태그를 추가할 수 있다.
- 하나의 테스트 메소드에 여러 태그를 사용할 수 있다.

자세한 사항은 다음 링크를 참고하자.

- https://maven.apache.org/guides/introduction/introduction-to-profiles.html
- https://junit.org/junit5/docs/current/user-guide/#running-tests-tag-expressions

# 커스텀 태그

JUnit 5 애노테이션을 조합하여 커스텀 태그를 만들 수 있다.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Tag("fast")
@Test
public @interface FastTest { }
```

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Tag("slow")
@Test
public @interface SlowTest { }
```

```java
@FastTest
@DisplayName("스터디 만들기 fast")
void create_new_study() {}

@SlowTest
@DisplayName("스터디 만들기 slow")
void create_new_study_again() {}
```

# 테스트 반복하기

`@RepeatedTest`

- 반복 횟수와 반복 테스트 이름을 설정할 수 있다.
  - {displayName}
  - {currentRepetition}
  - {totalRepetitions}
- RefetitionInfo 타입의 인자를 받을 수 있다.

```java
@RepeatedTest(10)
void repeatTest(RepetitionInfo repetitionInfo) {
    System.out.println("repeat" + repetitionInfo.getCurrentRepetition() + "/" +
            repetitionInfo.getTotalRepetitions());
}
```

![](https://images.velog.io/images/songs4805/post/b02244e9-0dd0-493c-8ca0-879f370a049a/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-21%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.28.34.png)

`@ParameterizedTest`

- 테스트에 여러 다른 매개변수를 대입해가며 반복 실행한다.
  - {displayName}
  - {index}
  - {arguments}
  - {0}, {1}, ...

// [Guide to JUnit 5 Parameterized Tests - baeldung](https://www.baeldung.com/parameterized-tests-junit-5)
