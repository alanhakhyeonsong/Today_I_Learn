# 객체지향 쿼리 언어(1)

## 객체지향 쿼리 언어 소개

JPA는 다양한 쿼리 방법을 지원한다.

- **JPQL**
- JPA Criteria
- **QueryDSL**
- 네이티브 SQL
- JDBC API 직접 사용, MyBatis, SpringJdbcTemplate 함께 사용

### JPQL(Java Persistence Query Language)

기존 방식대로 `EntityManager.find()`, 객체 그래프 탐색(ex: `a.getB().getC()`) 방식으로 가장 단순하게 조회할 수 있다.

하지만, **나이가 18살 이상인 회원을 모두 검색하고자 하는 등의 경우** 좀 더 현실적이고 복잡한 검색 방법이 필요하다.

- JPA를 사용하면 엔티티 객체를 중심으로 개발한다.
- 문제는 검색 쿼리
- 검색을 할 때도 **테이블이 아닌 엔티티 객체를 대상으로 검색**한다.
- 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능하다.
- 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요하다.

이런 문제를 해결하기 위해 JPQL이 만들어졌다.

#### 특징

- JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어를 제공
- SQL과 문법이 유사함. (SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원)

```java
// 검색
String jpql = "select m From Member m where m.age > 18";
List<Member> result = em.createQuery(jpql, Member.class).getResultList();
```

```log
// 실행된 SQL
select
    m.id as id,
    m.age as age,
    m.USERNAME as USERNAME,
    m.TEAM_ID as TEAM_ID
from
    Member m
where
    m.age>18
```

- JPQL은 엔티티 객체를 대상으로 쿼리
- SQL은 데이터베이스 테이블을 대상으로 쿼리
- JPQL은 SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않는다. (객체 지향 SQL)
- 동적 쿼리 생성이 쉽지 않다.

### Criteria

- 문자가 아닌 자바코드로 JPQL을 작성할 수 있다.

```java
//Criteria 사용 준비
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Member> query = cb.createQuery(Member.class);

// 루트 클래스(조회를 시작할 클래스)
Root<Member> m = query.from(Member.class);

// 쿼리 생성
CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "kim"));
List<Member> resultList = em.createQuery(cq).getResultList();
```

- JPQL 빌더 역할
- JPQ 공식 기능

**하지만 실무에선 거의 사용되지 않는다.** 쿼리를 동적으로 생성할 수는 있지만, 구현이 너무 복잡하고 실용성이 없다. 이 대신 **QueryDSL** 사용을 권장한다.  
// JPQL + QueryDSL 조합이 실무에선 거의 95% 정도라고 함

### QueryDSL

QueryDSL은 JPA 표준은 아니고 오픈소스 프로젝트이다. JPA 뿐만 아니라 MongoDB, Java Collection, Lucene, Hibernate Search, JDO도 거의 같은 문법으로 지원하며 현재 스프링 데이터 프로젝트가 지원할 정도로 많이 기대되는 프로젝트라고 한다.

```java
// JPQL
// select m from Member m where m.age > 18
JPAFactoryQuery query = new JPAQueryFactory(em);
QMember m = QMember.member;
List<Member> list = query.selectFrom(m)
                        .where(m.age.gt(18))
                        .orderBy(m.name.desc())
                        .fetch();
```

- 문자가 아닌 자바코드로 JPQL을 작성할 수 있다.
- JPQL 빌더 역할
- 컴파일 시점에 문법 오류를 찾을 수 있다.  
  // JPQL의 경우, String으로 짠 query가 틀려도 오류 찾기 힘듬(런타임 오류 때문)
- 동적쿼리 작성이 편리함
- **단순하고 쉬움**
- **실무 사용 권장**

### 네이티브 SQL

- JPA가 제공하는 SQL을 직접 사용하는 기능
- JPQL로 해결할 수 없는 특정 데이터베이스에 의존적인 기능을 사용해야 할 때 사용
  - ex) 오라클의 CONNECT BY, 특정 DB만 사용하는 SQL 힌트

```java
String sql = "SELECT ID, AGE, TEAM_ID, NAME FROM MEMBER WHERE NAME = 'kim'";
List<Member> resultList = em.createNativeQuery(sql, Member.class).getResultList();
```

### JDBC 직접 사용, SpringJdbcTemplate 등

- JPA를 사용하면서 JDBC 커넥션을 직접 사용하거나, 스프링 JdbcTemplate, MyBatis 등을 함께 사용 가능
- 단, 영속성 컨텍스트를 적절한 시점에 강제로 플러시 필요함
  - ex) JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트를 수동 플러시 한다.

```java
Member member = new Member();
member.setName("Sergio Ramos");

conn.createQuery("select * from Member where username = 'Sergio Ramos'");
```

member는 Jdbc가 쿼리를 수행하는 시점에서 영속성 컨텍스트에만 있고 DB에 아직 저장되지 않았기 때문에 조회 결과가 없다. 따라서 쿼리 수행 전 수동으로 플러시를 해줘야 한다.

## 기본 문법과 쿼리 API

### JPQL 소개

- JPQL은 객체지향 쿼리 언어다. 따라서 테이블을 대상으로 쿼리하는 것이 아니라 **엔티티 객체를 대상으로 쿼리**한다.
- JPQL은 SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않는다.  
  → 조회 기능을 만들 때 특정 DB에 의존하는 SQL을 따로 만들지 않아도 됨
- JPQL은 결국 SQL로 변환된다.

### 객체/DB 모델 예제

![](https://images.velog.io/images/songs4805/post/232d0190-459b-4604-a888-c4c06d0a611f/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2010.51.55.png)

```java
@Entity
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String username;
    private int age;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    // 연관관계 편의 메소드
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

    // getter, setter
}

@Entity
public class Team {

    @Id @GeneratedValue
    private Long id;
    private String name;

    @OneToMany
    private List<Member> members = new ArrayList<>();

    // getter, setter
}

@Entity
@Table(name = "ORDERS")
public class Order {

    @Id @GeneratedValue
    private Long id;
    private int orderAmount;

    @Embedded
    private Address address;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    // getter, setter
}

@Embeddable
public class Address {
    private String city;
    private String street;
    private String zipcode;
}


@Entity
public class Product {

    @Id @GeneratedValue
    private Long id;
    private String name;
    private int price;
    private int stockAmount;

    // getter, setter
}
```

### JPQL 문법

```
select_문 ::=
  select_절
  from_절
  [where_절]
  [groupby_절]
  [having_절]
  [orderby_절]

update_문 ::= update_절 [where_절]
delete_문 ::= delete_절 [where_절]
```

- `select m from Member as m where m.age > 18`
- 엔티티와 속성은 대소문자 구분을 한다. (Member, age)
- JPQL 키워드는 대소문자 구분을 하지 않는다. (SELECT, FROM, where)
- 엔티티 이름 사용, 테이블 이름이 아님(Member)
- **별칭(alias)은 필수(m)** (as는 생략 가능)

#### 집합과 정렬

- 집합

```java
select
    COUNT(m), // 회원 수
    SUM(m.age), // 나이 합
    AVG(m.age), // 평균 나이
    MAX(m.age), // 최대 나이
    MIN(m.age) // 최소 나이
from Member m
```

- GROUP BY, HAVING

```java
select t.name, COUNT(m.age), SUM(m.age), AVG(m.age), MAX(m.age), MIN(m.age)
from Member m LEFT JOIN m.team t
GROUP BY t.name
HAVING AVG(m.age) >= 10
```

- 정렬(ORDER BY)

```java
select m from Member m order by m.age DESC, m.username ASC

select t.name, COUNT(m.age) as cnt
from Member m LEFT JOIN m.team t
GROUP BY t.name
ORDER BY cnt
```

### TypeQuery, Query

- TypeQuery: 반환 타입이 명확할 때 사용
- Query: 반환 타입이 명확하지 않을 때 사용

```java
TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m", Member.class);

Query query = em.createQuery("SELECT m.username, m.age FROM Member m");
```

### 결과 조회 API

- `query.getResultList()`: **결과가 하나 이상일 때,** 리스트 반환
  - 결과가 없으면 빈 리스트 반환
- `query.getSingleResult()`: **결과가 정확히 하나,** 단일 객체 반환
  - 결과가 없으면: `javax.persistence.NoResultException` 예외 발생
  - 둘 이상이면: `javax.persistence.NonUniqueResultException` 예외 발생

> 참고로 Spring Data JPA에선 결과가 없으면 try-catch로 optional 또는 null 을 반환 해준다.

### 파라미터 바인딩 - 이름 기준, 위치 기준

```java
// 이름 기준 파라미터
String sql = "select m from Member m where m.username = :username";
TypedQuery<Member> query = em.createQuery(sql, Member.class);
query.setParameter("username", usernameParam);
List<Member> result = query.getResultList();

// 메소드 체이닝 지원
String sql = "select m from Member m where m.username = :username";
List<Member> result = em.createQuery(sql, Member.class)
                        .setParameter("username", usernameParam)
                        .getResultList();

// 위치 기준 파라미터
String sql = "select m from Member m where m.username = ?1";
List<Member> result = em.createQuery(sql, Member.class)
                        .setParameter("1", usernameParam)
                        .getResultList();
```

**파라미터 바인딩 방식은 선택이 아닌 필수다.** SQL 인젝션 공격 위험성도 있고, 성능 이슈도 있기 때문이다. 다만, **이름 기준 파라미터 바인딩 방식을 사용하는 것을 좀 더 권장한다.** (버그 문제 때문)

## 프로젝션(SELECT)

- **SELECT 절에 조회할 대상을 지정하는 것**
- 프로젝션 대상: 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자 등 기본 데이터 타입)
- `SELECT m FROM Member m`: 엔티티 프로젝션
- `SELECT m.team FROM Member m`: 엔티티 프로젝션
- `SELECT m.address FROM Member m`: 임베디드 타입 프로젝션
- `SELECT m.username, m.age FROM Member m`: 스칼라 타입 프로젝션
- DISTINCT로 중복 제거

### 엔티티 프로젝션

```java
SELECT m FROM Member m // 회원
SELECT m.team FROM Member m // 팀
```

- 원하는 객체를 바로 조회한 것으로 컬럼을 하나하나 나열해서 조회해야 하는 SQL과는 차이가 있다.
- **이렇게 조회한 엔티티는 영속성 컨텍스트에서 관리된다.**

### 임베디드 타입 프로젝션

JPQL에서 임베디드 타입은 엔티티와 거의 비슷하게 사용된다.
다만, **임베디드 타입은 조회의 시작점이 될 수 없다는 제약이 있다.**

```java
// 임베디드 타입인 Address를 조회의 시작점으로 사용한 잘못된 예시
String query = "SELECT a FROM Address a";

// Order 엔티티가 시작점이다. 엔티티를 통해 임베디드 타입을 조회해야 한다.
String query = "SELECT o.address FROM Order o";
List<Address> addresses = em.createQuery(query, Address.class).getResultList();
```

**임베디드 타입은 엔티티 타입이 아닌 값 타입이다. 따라서 이렇게 직접 조회한 임베디드 타입은 영속성 컨텍스트에서 관리되지 않는다.**

### 스칼라 타입 프로젝션

숫자, 문자, 날짜와 같은 기본 데이터 타입들을 스칼라 타입이라 한다.

```java
em.createQuery("SELECT DISTINCT username FROM Member m").getSingleResult();
```

### 여러 값 조회

엔티티를 대상으로 조회하면 편리하겠지만, **꼭 필요한 데이터들만 선택해서 조회**해야 할 때도 있다.
프로젝션에 여러 값을 선택하면 `TypeQuery`를 사용할 수 없고 `Query`를 사용해야 한다.

#### `Query` 타입으로 조회

```java
Member member = new Member();
member.setUsername("member1");
member.setAge(10);
em.persist(member);

em.flush();
em.clear();

List resultList = em.createQuery("select m.username, m.age from Member m")
                    .getResultList();

Object o = resultList.get(0);
Object[] result = (Object[]) o;
System.out.println("result = " + result[0]);
System.out.println("result = " + result[1]);
```

![](https://images.velog.io/images/songs4805/post/b7525eab-7b90-4455-bbb9-ee76a133886e/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-04%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%205.50.44.png)

#### `Object[]` 타입으로 조회

```java
List<Object[]> resultList = em.createQuery("select m.username, m.age from Member m")
                    .getResultList();

Object[] result = resultList.get(0);
System.out.println("username = " + result[0]);
System.out.println("age = " + result[1]);
```

#### new 명령어로 조회

- 제일 깔끔한 방법이다.
- 단순 값을 DTO로 바로 조회
- 패키지 명을 포함한 전체 클래스명 입력해야 한다.
- 순서와 타입이 일치하는 생성자가 필요하다.

```java
// MemberDTO.java
public class MemberDTO {
    private String username;
    private int age;

    public MemberDTO(String username, int age) {
        this.username = username;
        this.age = age;
    }
    // getter, setter
}

// Main.java
List<MemberDTO> resultList = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();

MemberDTO memberDTO = resultList.get(0);
System.out.println("memberDTO = " + memberDTO.getUsername());
System.out.println("memberDTO = " + memberDTO.getAge());
```

![](https://images.velog.io/images/songs4805/post/182dbbf7-7e73-4607-8c3b-073c06f93bae/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-04%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.00.33.png)

## 페이징

페이징 처리용 SQL을 작성하는 일은 지루하고 반복적이다. 더 큰 문제는 데이터베이스마다 페이징을 처리하는 SQL 문법이 다르다는 점이다.

JPA의 페이징은 다음과 같은 특징이 있다.

- JPA는 페이징을 다음 두 API로 추상화
- `setFirstResult(int startPosition)`: 조회 시작 위치(0부터 시작)
- `setMaxResults(int maxResult)`: 조회할 데이터 수

예시는 다음과 같다.

```java
for (int i = 0; i < 100; i++) {
    Member member = new Member();
    member.setUsername("member" + i);
    member.setAge(i);
    em.persist(member);
}

em.flush();
em.clear();

List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();

System.out.println("result.size = " + result.size());
for (Member member1 : result) {
    System.out.println("member1 = " + member1);
}
```

결과는 다음과 같이 확인할 수 있다.

![](https://images.velog.io/images/songs4805/post/a49d4a5d-8a35-4a1f-970d-d5cbabe591a8/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-04%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.49.37.png)

데이터베이스 별로 페이징 API에 대한 방언은 다음과 같다.

```sql
// MySQL
SELECT
    M.ID AS ID,
    M.AGE AS AGE,
    M.TEAM_ID AS TEAM_ID,
    M.NAME AS NAME
FROM
    MEMBER M
ORDER BY
    M.NAME DESC LIMIT ?, ?

// Oracle
SELECT * FROM
    ( SELECT ROW_.*, ROWNUM ROWNUM_
    FROM
        ( SELECT
            M.ID AS ID,
            M.AGE AS AGE,
            M.TEAM_ID AS TEAM_ID,
            M.NAME AS NAME
        FROM MEMBER M
        ORDER BY M.NAME
        ) ROW
    WHERE ROWNUM <= ?
    )
WHERE ROWNUM_ > ?
```

**기존에는 Diarect별로 방언을 맞춰서 쿼리를 하나하나 구현해야 했는데, 이제는 두 개의 함수로 해결할 수 있어서 몹시 편리하다.**

만약, 페이징 SQL을 더 최적화하고 싶다면 JPA가 제공하는 페이징 API가 아닌 네이티브 SQL을 직접 사용해야 한다.

## 조인

### 내부 조인

```java
// @ManyToOne(fetch = FetchType.LAZY)가 되어있어야 함.

Team team = new Team();
team.setName("teamA");
em.persist(team);

Member member = new Member();
member.setUsername("member1");
member.setAge(10);
member.setTeam(team);
em.persist(member);

em.flush();
em.clear();

String query = "select m from Member m inner join m.team t";
List<Member> result = em.createQuery(query, Member.class)
    .getResultList();

tx.commit();
```

![](https://images.velog.io/images/songs4805/post/732dc037-4f6f-4a7c-a071-d53cbd7f02c3/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-04%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%208.31.35.png)

### 외부 조인

```java
String query = "select m from Member m left outer join m.team t";
List<Member> result = em.createQuery(query, Member.class)
    .getResultList();
```

![](https://images.velog.io/images/songs4805/post/61a3ddf3-6ad4-4927-b4ef-d4a47b861fb4/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-04%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%208.32.34.png)

### 세타 조인

```java
String query = "select m from Member m, Team t where m.username = t.name";
List<Member> result = em.createQuery(query, Member.class)
    .getResultList();
```

![](https://images.velog.io/images/songs4805/post/c50cdd41-15aa-4e90-aace-dfe81a657ab4/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-04%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%208.35.07.png)

### 조인 - ON 절

#### 조인 대상 필터링

ex) 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인

```sql
// JPQL
SELECT m, t FROM Member m LEFT JOIN m.team t on t.name='A'

// SQL
SELECT m.*, t.* FROM
Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and t.name='A'
```

![](https://images.velog.io/images/songs4805/post/fa8d9d06-f14a-457f-8c38-90cbb18ea862/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-04%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%209.05.46.png)

#### 연관관계 없는 엔티티 외부 조인

ex) 회원의 이름과 팀의 이름이 같은 대상 외부조인

```sql
// JPQL
SELECT m, t FROM Member m LEFT JOIN Team t on m.username=t.name

// SQL
SELECT m.*, t.* FROM
Member m LEFT JOIN Team t ON m.username=t.name
```

![](https://images.velog.io/images/songs4805/post/3eee9905-87cb-49f9-bc44-4d1756aa5afb/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-04%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%209.08.47.png)

## 서브 쿼리

ex) 나이가 평균보다 많은 회원

```java
select m from Member m
where m.age > (select avg(m2.age) from Member m2)
```

ex) 한 건이라도 주문한 고객

```java
select m from Member m
where (select count(o) from Order o where m = o.member) > 0
```

### 서브 쿼리 지원 함수

- [NOT] EXISTS(subquery): 서브쿼리에 결과가 존재하면 참
  - {ALL | ANY | SOME} (subquery)
  - ALL: 모두 만족하면 참
  - ANY, SOME: 같은 의미, 조건을 하나라도 만족하면 참
- [NOT] IN (subquery): 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참

#### 서브 쿼리 - 예제

- 팀 A 소속인 회원

```java
select m from Member m
where exists (select t from m.team t where t.name = '팀A')
```

- 전체 상품 각각의 재고보다 주문량이 많은 주문들

```java
select o from Order o
where o.orderAmount > ALL (select p.stockAmount from Product p)
```

- 어떤 팀이든 팀에 소속된 회원

```java
select m from Member m
where m.team = ANY (select t from Team t)
```

### JPA 서브 쿼리 한계

- JPA는 **WHERE, HAVING 절에서만 서브 쿼리 사용 가능**
- SELECT 절도 가능(하이버네이트에서 지원)
- **FROM 절의 서브 쿼리는 현재 JPQL에서 불가능**
  - **조인으로 풀 수 있으면 풀어서 해결**

## JPQL 타입 표현과 기타식

### JPQL 타입 표현

- 문자: 'HELLO', 'She''s'
- 숫자: 10L(Long), 10D(Double), 10F(Float)
- Boolean: TRUE, FALSE
- ENUM: jpabook.MemberType.Admin (패키지명 포함)

```java
select m.username, 'HELLO', true from Member m
where m.type = jpql.MemberType.ADMIN
```

- 엔티티 타입: TYPE(m) = Member (상속 관계에서 사용)

```java
em.createQuery("select i from Item i where type(i) = Book", Item.class);
```

### JPQL 기타

- SQL과 문법이 같은 식
- EXISTS, IN
- AND, OR, NOT
- =, >, >=, <, <=, <>
- BETWEEN, LIKE, **IS NULL**

## 조건식(CASE 등)

### 기본 CASE 식

```java
String query =
        "select " +
                "case when m.age <= 10 then '학생요금' " +
                "     when m.age >= 60 then '경로요금' " +
                "     else '일반요금'"+
                " end " +
        "from Member m";
List<String> result = em.createQuery(query, String.class)
        .getResultList();

for (String s : result) {
    System.out.println("s = " + s);
}
```

![](https://images.velog.io/images/songs4805/post/2aedb9dc-7bba-4719-9696-5591818b841e/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-04%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2010.02.17.png)

### 단순 CASE 식

```java
String query =
        "select " +
                "case t.name when 'teamA' then '인센티브110%' " +
                "     when 'teamB' then '인센티브120%' " +
                "     else '인센티브105%'"+
                " end " +
        "from Team t";
List<String> result = em.createQuery(query, String.class)
        .getResultList();
```

![](https://images.velog.io/images/songs4805/post/fb0b3767-983c-4979-9603-12271edcc17a/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-04%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2010.07.24.png)

### COALESCE

하나씩 조회해서 null이 아니면 반환

```java
select coalesce(m.username, '이름 없는 회원') from Member m
```

### NULLIF

두 값이 같으면 null 반환, 다르면 첫 번째 값 반환

```java
select NULLIF(m.username, '관리자') from Member m
```

## JPQL 함수

- CONCAT
- SUBSTRING
- TRIM
- LOWER, UPPER
- LENGTH
- LOCATE
- ABS, SQRT, MOD
- SIZE, INDEX(JPA 용도)

```java
// CONCAT
select concat('a', 'b') // ab

// SUBSTRING: firstParam의 값을 secondParam 위치부터 thirdParam 갯수만큼 잘라서 반환
select substring('abcd', 2, 3) // bc

// TRIM
select trim(' sergio ramos ') // sergio ramos

// LOWER, UPPER
select LOWER()
select UPPER()

// LENGTH
select LENGTH('sergioramos') // 11

// LOCATE
select LOCATE('r', 'ramos') // 1

// ABS, SQRT, MOD
select ABS(-30) // 30
select SQRT(4) // 2
select MOD(4, 2) // 0

// SIZE, INDEX(JPA 용도)
select SIZE(t.members) from Team t // 0
```

### 사용자 정의 함수 호출

하이버네이트는 사용전 방언에 추가해야 한다.  
→ 사용하는 DB 방언을 상속받고, 사용자 정의 함수를 등록한다.  
(실제 소스 코드 내부에 정의되어 있는 함수들을 참고해서 작성해주면 된다.)

```java
// group_concat이라는 함수를 만들어서 등록한다 가정
public class MyH2Dialect extends H2Dialect {
    public MyH2Dialect() {
        registerFunction("group_concat", new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));
    }
    ...
}

// 설정 파일 등록
<property name="hibernate.dialect" value="hello.MyH2Dialect"/>
```

```java
// 하이버네이트 구현체 사용
select function('group_concat', i.name) from Item i
```
