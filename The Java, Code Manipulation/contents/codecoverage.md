# 코드 커버리지는 어떻게 측정할까?

코드 커버리지: 테스트 코드가 확인한 소스 코드를 %

- JaCoCo
- https://www.eclemma.org/jacoco/trunk/doc/index.html
- http://www.semdesigns.com/Company/Publications/TestCoverage.pdf

`build.gradle`

```gradle
plugins {
    id 'java'
}

apply plugin: 'jacoco'

group 'me.ramos'
version '1.0-SNAPSHOT'


repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.7.0'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.7.0'
}

test {
    useJUnitPlatform()
}
```

gradle 빌드

```terminal
./gradlew build
```

`build > jacoco`경로상에 `test.exec` 라는 바이트코드가 생성되는데, IntelliJ에서 `실행 > 커버리지 데이터 표시`(한글 패치) 또는 `Analyze > Show Coverage Data ...` 에서 다음과 같이 `test.exec`를 추가한다.

![](https://images.velog.io/images/songs4805/post/e3f2ceef-41be-4d1b-91da-cd492ddcfc6d/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-13%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.15.24.png)

다음과 같이 코드 커버리지가 IDE 내부에서 측정이 된다.

![](<https://images.velog.io/images/songs4805/post/d6a48567-6996-41f0-a019-14ce98ab13a8/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-13%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.13.09(2).png>)

커버리지 보고서를 생성하면 html 파일이 생성되어 브라우저에서 보기 좋은 형태로 확인할 수 있다.

![](<https://images.velog.io/images/songs4805/post/5ce4a574-b01e-4b53-9008-9413b7946154/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-13%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.18.17(2).png>)

![](https://images.velog.io/images/songs4805/post/1e240914-903e-4aa9-8a61-f6afb5c04eae/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-13%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.18.34.png)

![](https://images.velog.io/images/songs4805/post/d0b311c0-eb17-453d-9ef8-bc44eaae048b/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-13%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.19.03.png)

![](https://images.velog.io/images/songs4805/post/2b7069d8-7545-4c5d-a06f-00d4cea3c1d2/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-13%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.18.50.png)

`build.gradle` 파일에서 커버리지 limit 설정으로 커버리지 테스트가 통과하지 못할 경우, 빌드 실패하도록 설정도 가능하다.

코드 커버리지에 대해 자세한 활용은 다음 글을 확인하자.  
[Gradle 프로젝트에 JaCoCo 설정하기](https://techblog.woowahan.com/2661/)
