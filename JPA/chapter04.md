# 엔티티 매핑

## 객체와 테이블 매핑

### `@Entity`

`@Entity`가 붙은 클래스는 JPA가 관리하는 것으로, 엔티티라 한다. JPA를 사용해서 테이블과 매핑할 클래스는 이 어노테이션이 필수이다.

> 주의 사항
>
> - 기본 생성자는 필수(파라미터가 없는 public 또는 protected 생성자)
> - final 클래스, enum, interface, inner 클래스에는 사용 불가
> - 저장할 필드에 final을 사용하면 안됨.

`@Entity` 속성 정리

- 속성: **name**
  - JPA에서 사용할 엔티티 이름을 지정
  - 기본값: 클래스 이름을 그대로 사용
  - 같은 클래스 이름이 없으면 가급적 기본값을 사용

### `@Table`

`@Table`은 엔티티와 매핑할 테이블을 지정한다.

`@Table` 속성 정리

| 속성                   | 기능                                                                                                                                             | 기본값                  |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ | ----------------------- |
| name                   | 매핑할 테이블 이름                                                                                                                               | 엔티티 이름을 사용한다. |
| catalog                | catalog 기능이 있는 데이터베이스에서 catalog를 매핑                                                                                              |                         |
| schema                 | schema 기능이 있는 데이터베이스에서 schema를 매핑                                                                                                |                         |
| uniqueConstraints(DDL) | DDL 생성 시에 유니크 제약조건을 만든다. 2개 이상의 복합 유니크 제약조건도 만들 수 있다.(스키마 자동 생성 기능을 사용해서 DDL을 만들 때만 사용됨) |                         |

## 데이터베이스 스키마 자동 생성

JPA는 데이터베이스 스키마를 자동으로 생성하는 기능을 지원한다.

```xml
# persistence.xml
<property name="hibernate.hbm2ddl.auto" value="create" />
<property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect" /> <!--SQL 방언 옵션-->
<property name="hibernate.show_sql" value="true" /> <!--DDL 출력 옵션-->
```

위 속성을 추가하면 **애플리케이션 실행 시점에 데이터베이스 테이블을 자동으로 생성한다.**
spring project 내의 `application.yml`에선 다음과 같이 설정하면 된다.

```yml
# application.yml
spring:
  (중략)
  jpa:
    hibernate:
      ddl-auto: create
```

- 이 기능을 사용함으로써 개발자는 테이블 중심에서 객체 중심으로 관심사가 달라지게 된다.
- 데이터베이스 방언을 활용해서 데이터베이스에 맞는 적절한 DDL 생성
- **생성된 DDL은 개발 장비에서만 사용**
- 생성된 DDL은 운영서버에서는 사용하지 않거나, 적절히 다듬은 후 사용한다.

### 속성

`hibernate.hbm2ddl.auto`

| 옵션        | 설명                                         |
| ----------- | -------------------------------------------- |
| create      | 기존 테이블 삭제 후 다시 생성(DROP + CREATE) |
| create-drop | create와 같으나 종료시점에 테이블 DROP       |
| update      | 변경분만 반영(운영DB에는 사용하면 안됨)      |
| validate    | 엔티티와 테이블이 정상 매핑되었는지만 확인   |
| none        | 사용하지 않음                                |

**운영 서버에서 create, create-drop, update처럼 DDL을 수정하는 옵션은 절대 사용하면 안된다.** 오직 개발 서버나 개발 단계에서만 사용해야 한다. 이 옵션들은 운영 중인 데이터베이스의 테이블이나 컬럼을 삭제할 수 있다.

// 참고로 호돌맨님이 배민에서 이 옵션때문에 대재앙이 발생했다는 것은 너무나도 유명하다...ㅋㅋ;
// [재난급 서버 장애내고 개발자 인생 끝날뻔 한 썰 - 납량특집! DB에 테이블이 어디로 갔지? - Youtube 개발바닥](https://www.youtube.com/watch?v=SWZcrdmmLEU)

> 📌 추천 전략
>
> - 개발 초기 단계: create 또는 update
> - 테스트 서버: update 또는 validate
> - 스테이징과 운영 서버: validate 또는 none

## 필드와 컬럼 매핑

```java
package hellojpa;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "name")
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob
    private String description;
}
```

### `@Enumerated`

- 자바 Enum 타입을 매핑할 때 사용
- `EnumType.ORDINAL`: Enum 순서를 데이터베이스에 저장
- `EnumType.STRING`: Enum 이름을 데이터베이스에 저장
- **ORDINAL 타입을 사용하지 말자.**
  enum 타입이 추가, 변경, 삭제되어 순서가 달라질 경우 사이드이펙트가 발생.

### `@Temporal`

- 날짜 타입(Date, Calendar)을 매핑할 때 사용
- LocalDate, LocalDateTime을 사용할 때는 생략 가능(최신 하이버네이트 지원)

`TemporalType.DATE`: 날짜, 데이터베이스 date 타입과 매핑(2022-01-22)
`TemporalType.TIME`: 시간, 데이터베이스 time 타입과 매핑(11:11:11)
`TemporalType.TIMESTAMP`: 날짜와 시간, 데이터베이스 timestamp 타입과 매핑(2022-01-22 11:43:11)

### `@Lob`

- 데이터베이스 BLOB, CLOB 타입과 매핑
- 매핑하는 필드타입이 문자면 CLOB, 나머지는 BLOB
- CLOB: `String`, `char[]`, `java.sql.CLOB`
- BLOB: `byte[]`, `java.sql.BLOB`

### `@Transient`

- 필드 매핑이 안되게 하는 어노테이션
- 데이터베이스에 저장x, 조회x
- 주로 메모리상에서만 임시로 어떤 값을 보관하고 싶을 때 사용

### `@Column`

**제일 자주 쓰이는 어노테이션**

| 속성                  | 설명                                                                                | 기본값           |
| --------------------- | ----------------------------------------------------------------------------------- | ---------------- |
| **name**              | 필드와 매핑할 테이블의 컬럼 이름                                                    | 객체의 필드 이름 |
| insertable, updatable | 등록, 변경 가능 여부                                                                | TRUE             |
| **nullable(DDL)**     | null 허용 여부 설정, false로 설정 시 DDL 생성시에 not null 제약 조건이 붙음         |                  |
| unique(DDL)           | `@Table`의 uniqueConstrainst와 같지만 한 컬럼에 간단히 유니크 제약조건을 걸 때 사용 |                  |
| columnDefinition(DDL) | 데이터베이스 컬럼 정보를 직접 줄 수 있다.                                           |                  |
| length(DDL)           | 문자 길이 제약조건, String 타입에서만 사용                                          |                  |
| precision, scale(DDL) | BigDecimal 타입에서 사용(아주 큰 숫자)                                              |                  |

## 기본 키 매핑

JPA는 데이터베이스마다 기본 키를 생성하는 방식이 서로 다른 문제를 해결하기 위해 기본키 생성 전략을 제공한다.

```java
@Id @GeneratedValue(strategy = GenerationType.AUTO)
private Long id;
```

- 직접 할당: `@Id`만 사용. 기본 키를 애플리케이션에서 직접 할당하는 방식.
- 자동 생성: `@GeneratedValue` 추가. 대리 키 사용 방식.

### IDENTITY 전략

`@GeneratedValue(strategy = GenerationType.IDENTITY)`
기본 키 생성을 데이터베이스에 위임하는 전략이다.
주로 **MySQL**에서 사용된다.(AUTO_INCREMENT)

데이터베이스에 값을 저장할 때 기본 키 컬럼인 ID을 비워두면 데이터베이스가 순서대로 값을 채워준다.

- JPA는 보통 트랜잭션 커밋 시점에 INSERT SQL을 실행한다.
- AUTO_INCREMENT는 데이터베이스에 INSERT SQL을 실행한 이후에 ID 값을 알 수 있다.
- IDENTITY 전략은 `em.persist()` 시점에 즉시 INSERT SQL을 실행하고 DB에서 식별자를 조회한다.

> 📌 주의
> 엔티티가 영속 상태가 되려면 식별자가 반드시 필요하다. 하지만 IDENTITY 식별자 생성 전략은 엔티티를 데이터베이스에 저장해야 식별자를 구할 수 있으므로 `em.persist()`를 호출하는 즉시 INSERT SQL이 데이터베이스에 전달된다. **따라서 이 전략은 트랜잭션을 지원하는 쓰기 지연이 동작하지 않는다.**
> (엔티티를 DB에 저장 -> 식별자 조회 -> 식별자를 엔티티 식별자에 할당)

### SEQUENCE 전략

`@GeneratedValue(strategy = GenerationType.SEQUENCE)`
데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트다. SEQUENCE 전략은 이 시퀀스를 사용해서 기본 키를 생성한다.
주로 **ORACLE**에서 사용된다.

```sql
CREATE TABLE BOARD (
    ID BIGINT NOT NULL PRIMARY KEY,
    DATA VARCHAR(255)
)

// 시퀀스 생성
CREATE SEQUENCE BOARD_SEQ START WITH 1 INCREMENT BY 1;
```

```java
@Entity
@SequenceGenerator(
    name = "BOARD_SEQ_GENERATOR",
    sequenceName = "BOARD_SEQ",
    initialValue = 1, allocationSize = 1)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "BOARD_SEQ_GENERATOR")
    private Long id;
}
```

DB 시퀀스를 통해 식별자 조회 -> 조회한 식별자를 엔티티에 할당 + 엔티티를 영속성 컨텍스트에 저장 -> 트랜잭션 커밋(플러시) -> 엔티티를 DB에 저장

- 영속화(persist())시 시퀀스에서 next value를 가져와 해당 값을 @Id에 가지고 1차 캐싱을 해준다.
- 쓰기 지연이 가능하다.

| 속성            | 설명                                                                                                                                                   | 기본값             |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------ |
| name            | 식별자 생성기 이름                                                                                                                                     | 필수               |
| sequenceName    | 데이터베이스에 등록되어 있는 시퀀스 이름                                                                                                               | hibernate_sequence |
| initialValue    | DDL 생성 시에만 사용됨. 시퀀스 DDL을 생성할 때 처음 1 시작하는 수를 지정한다.                                                                          | 1                  |
| allocationSize  | 시퀀스 한 번 호출에 증가하는 수(성능 최적화에 사용됨) **데이터베이스 시퀀스 값이 하나씩 증가하도록 설정되어 있으면 이 값을 반드시 1로 설정해야 한다.** | **50**             |
| catalog, schema | 데이터베이스 catalog, schema 이름                                                                                                                      |                    |

### TABLE 전략

키 생성 전용 테이블을 하나 만들고 여기에 이름과 값으로 사용할 컬럼을 만들어 데이터베이스 시퀀스를 흉내내는 전략이다.
이 전략은 모든 데이터베이스에 적용할 수 있지만, 성능은 떨어진다.

`@TableGenerator`가 필요함.

```java
@Entity
@TableGenerator(
    name = "MEMBER_SEQ_GENERATOR",
    table = "MY_SEQUENCE",
    pkColumnName = "MEMBER_SEQ", allocationSize = 1)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.Table, generator = "MEMBER_SEQ_GENERATOR")
    @Column(name = "MEMBER_ID")
    private Long id;
}
```

속성은 다음과 같다.

- name: 식별자 생성기 이름(필수)
- table: 키생성 테이블 명(MEMBER_SEQ_GENERATOR)
- pkColumnName: 시퀀스 컬럼명
- valueColumnName: 시퀀스 값 컬럼명
- pkColumnValue: 키로 사용할 값 이름(엔티티 이름)
- initialValue: 초기 값
- **allocationSize**: 시퀀스 한 번 호출에 증가하는 수(성능 최적화에 사용됨)
- catalog, schema: 데이터베이스 catalog, schema 이름
- uniqueConstraints(DDL): 유니크 제약 조건을 지정할 수 있다.

### AUTO 전략

방언에 따라 자동 지정, 기본값

### 권장하는 식별자 선택 전략?

데이터베이스 기본 키는 다음 3가지 조건을 모두 만족해야 한다.

1. null 값은 허용하지 않는다.
2. 유일해야 한다.
3. 변해선 안된다.

이 조건을 계속 만족하는 자연키는 찾기 힘들기 때문에 대리키(대체키)를 사용하자.

권장: Long형 + 대체키 + 키 생성전략 사용
