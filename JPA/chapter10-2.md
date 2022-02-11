# 객체지향 쿼리 언어(2)

## 경로 표현식

.(점)을 찍어 객체 그래프를 탐색하는 것

```java
select m.username // 상태 필드
from Member m
    join m.team t // 단일 값 연관 필드
    join m.orders o // 컬렉션 값 연관 필드
where t.name = '팀A'
```

### 경로 표현식 용어 정리

- **상태 필드**(state field): 단순히 값을 저장하기 위한 필드 (ex: m.username)
- **연관 필드**(association field): 연관관계를 위한 필드
  - **단일 값 연관 필드**: `@ManyToOne`, `@OneToOne`, 대상이 엔티티(ex: m.team)
  - **컬렉션 값 연관 필드**: `@OneToMany`, `@ManyToMany`, 대상이 컬렉션(ex: m.orders)

### 경로 표현식 특징

- **상태 필드**(state field): 경로 탐색의 끝, 탐색 X

```java
String query = "select m.username, m.age From Member m";
```

- **단일 값 연관 경로**: **묵시적 내부 조인(inner join) 발생**, 탐색 O

```java
String query = "select m.team From Member m";

List<Team> result = em.createQuery(query, Team.class)
        .getResultList();
```

![](https://images.velog.io/images/songs4805/post/c3571cd1-f54f-47cd-8ea6-6550e141704c/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-09%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%203.45.06.png)

`select m.team.name From Member m` 처럼 team에서 경로 탐색이 더 가능하다.

- **컬렉션 값 연관 경로**: **묵시적 내부 조인 발생**, 탐색 X
  → FROM 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능

```java
String query = "select t.members From Team t";
// 명시적 조인: "select m.username From Team t join t.members m"

Collection result = em.createQuery(query, Collection.class)
        .getResultList();
```

![](https://images.velog.io/images/songs4805/post/f657f741-a18c-4259-b3f8-f9624ff2ab28/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-09%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%203.50.36.png)

### 명시적 조인, 묵시적 조인

- 명시적 조인: join 키워드 직접 사용
  → `select m from Member m join m.team t`

- 묵시적 조인: 경로 표현식에 의해 묵시적으로 SQL 조인 발생 (내부 조인만 가능)
  → `select m.team from Member m`

**단일 값 연관 필드로 경로 탐색을 하면 SQL에서 내부 조인이 일어나는데 이를 묵시적 조인이라 한다. 참고로, 묵시적 조인은 모두 내부 조인이다.**

### 경로 표현식 - 예제

- `select o.member.team from Order o` → 성공
- `select t.members from Team t` → 성공
- `select t.members.username from Team t` → 실패
- `select m.username from Team t join t.members m` → 성공

### 경로 탐색을 사용한 묵시적 조인 시 주의사항

- 항상 내부 조인
- 컬렉션은 경로 탐색의 끝, 명시적 조인을 통해 별칭을 얻어야 함
- 경로 탐색은 주로 SELECT, WHERE 절에서 사용하지만, 묵시적 조인으로 인해 SQL의 FROM (JOIN) 절에 영향을 줌

**실무에서는 명시적 조인을 사용하자.**
**조인은 SQL 튜닝에 중요 포인트**
**묵시적 조인은 조인이 일어나는 상황을 한눈에 파악하기 어려움**

## JPQL - 페치 조인(fetch join)

- SQL 조인 종류가 아닌 JPA에서 제공하는 기능이다.
- JPQL에서 성능 최적화를 위해 제공하는 기능
- 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회하는 기능
- `join fetch` 명령어 사용
- 페치 조인 ::= [LEFT [OUTER]|INNER] JOIN FETCH 조인 경로

### 엔티티 페치 조인

- 회원을 조회하면서 연관된 팀도 함께 조회

```sql
// JPQL
select m from Member m join fetch m.team

// SQL
select m.*, t.* from Member m inner join Team t on m.team_id = t.id;
```

![](https://images.velog.io/images/songs4805/post/a87929e8-0e4b-490c-ab75-b7094dee601b/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-10%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.39.11.png)

팀이 있는 회원을 조회하고 싶을 때 fetch join을 사용하면 내부적으로 inner join을 사용한다. 팀이 없는 회원은 누락된다.

다음은 일반적인 select로 Member를 조회할 때, 연관관계에 있는 Team을 불러 Team.name 까지 조회하는 예시이다. 이 경우 문제가 있다.

// Member와 Team은`@ManyToOne` 관계에, 지연로딩이 설정되어있다.

```java
String query = "select m From Member m";

List<Member> result = em.createQuery(query, Member.class)
        .getResultList();

for (Member member : result) {
    System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
    // 회원1, 팀A(SQL)
    // 회원2, 팀A(1차캐시)
    // 회원3, 팀B(SQL)

    // 회원 100명 -> N + 1
}
```

![](https://images.velog.io/images/songs4805/post/aeb6a2a2-eea4-449b-ad33-055d8cdc39b5/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-10%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2010.23.31.png)

결과를 보면 for문 안에서 Member를 조회한 뒤, Team의 이름까지 조회할 때, 회원1, 회원2는 SQL과 1차 캐시를 통해 팀을 불러오는 것이지만, 회원3에 해당하는 팀B는 아직 조회하지 않았기 때문에 1차 캐시에 없다.
따라서 select 쿼리를 한 번 더 실행하게 된다. 이 경우 N+1 문제가 발생한다.

정리하자면, 최초 JPQL을 통해 Member를 조회해 올때 Team의 정보는 Proxy 객체로 가지고 있다.(실제로는 존재 x)
그렇기에 실제로 `getTeam().getName()`을 통해 팀의 정보를 조회하려 할 때 SQL을 수행한다. 주석 내용대로 한 번 가져온 Team의 정보는 1차 캐시에 올라가 있기 때문에 더 조회할 필요는 없지만, 회원 N명을 조회하게 되었을 때 최대 N+1번 Team 조회 쿼리가 수행 될 수 있다.

페치 조인을 사용하면 N+1 문제를 해결하는데, 다음과 같이 회원을 조회하면서 연관된 팀도 함께 조회한다. (SQL 1회)

```java
String query = "select m From Member m join fetch m.team";

List<Member> result = em.createQuery(query, Member.class)
        .getResultList();

for (Member member : result) {
    System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
    // 페치 조인으로 회원과 팀을 함께 조회해서 지연 로딩 X
}
```

![](https://images.velog.io/images/songs4805/post/ee543d90-0551-42d9-a0e3-fbfdd146bcdb/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-10%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2010.27.12.png)

페치조인은 조회 당시 실제 엔티티가 담기기 때문에, 지연로딩 없이 바로 사용가능하다.

### 컬렉션 페치 조인

- 일대다 관계, 컬렉션 페치 조인

```sql
// JPQL
select t from Team t join fetch t.members where t.name='팀A'

// SQL
select t.*, m.* from team t inner join member m on t.id=m.team_id where t.name = '팀A'
```

이를 수행하면 Team은 하나지만 Member가 1개 이상일 수 있다. // 일대다 관계에선 데이터가 뻥튀기 될 수 있다.

![](https://images.velog.io/images/songs4805/post/05ac82e1-5155-48a0-86d7-61e6e9d16697/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-10%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.50.04.png)

팀A는 1개지만, 그에 해당하는 멤버는 회원1, 회원2로 2개이기 때문에 조회 결과는 위 표처럼 2개의 row가 된다. 팀은 하나이기에 같은 주소값(0x100)을 가진 결과가 두개가 나오고 팀A의 입장에선 회원1, 회원2를 가진다.
// 이것이 바로 결과상의 뻥튀기가 발생한 것임

다음은 컬렉션 페치 조인 사용 코드 예시이다.

```java
String query = "select t From Team t join fetch t.members";

List<Team> result = em.createQuery(query, Team.class)
        .getResultList();

for (Team team : result) {
    System.out.println("team = " + team.getName() + "|members=" + team.getMembers().size());
    for (Member member : team.getMembers()) {
        System.out.println("-> member = " + member);
    }
}
```

![](https://images.velog.io/images/songs4805/post/67fd39d0-fa9c-4459-81a5-6324acf74225/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-10%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2010.36.42.png)

### 페치 조인과 DISTINCT

- SQL의 DISTINCT는 중복된 결과를 제거하는 명령이다.
- JPQL은 SQL에 DISTINCT를 추가하고 애플리케이션에서 엔티티 중복까지 제거한다.

```
select distinct t from Team t join fetch t.members where t.name = '팀A';
```

위 코드를 실행하면 SQL에 DISTINCT를 추가하지만 데이터가 다르므로 SQL 결과상 중복 제거를 실패한다.

![](https://images.velog.io/images/songs4805/post/30a89f28-82b8-4037-a06c-62e107f382ee/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-10%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.58.29.png)

단순히 쿼리만으로는 중복제거가 안되기 때문에 JPA에선 DISTINCT가 추가로 애플리케이션에서 중복 제거를 시도한다.

- 같은 식별자를 가진 **Team 엔티티 제거**

![](https://images.velog.io/images/songs4805/post/b28daf57-a656-4247-83e5-4dda0f57f40a/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-11%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2012.00.51.png)

코드를 통해 확인해보면 다음과 같다.

```java
String query = "select distinct t From Team t join fetch t.members";

List<Team> result = em.createQuery(query, Team.class)
        .getResultList();

System.out.println("result = " + result.size());

for (Team team : result) {
    System.out.println("team = " + team.getName() + "|members=" + team.getMembers().size());
    for (Member member : team.getMembers()) {
       System.out.println("-> member = " + member);
    }
}
```

![](https://images.velog.io/images/songs4805/post/4a6ba94c-dce0-437c-bade-46431ecc1471/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-11%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2012.03.51.png)

**참고로 반대로 다대일(N:1), 일대일(1:1)은 결과가 뻥튀기 되지 않는다.**

### 페치 조인과 일반 조인의 차이

- 일반 조인 실행 시 연관된 엔티티를 함께 조회하지 않음

```sql
// JPQL
select from Team t join t.members m where t.name = '팀A';

// SQL
select t.* from Team t inner join member m on t.id = m.team_id where t.name = '팀A';
```

- JPQL은 결과를 반환할 때 연관관계를 고려하지 않는다.
- 단지 SELECT 절에 지정한 엔티티만 조회한다.
- 위 예시에선 팀 엔티티만 조회하고, 회원 엔티티는 조회하지 않는다.
- **페치 조인을 사용할 때만 연관된 엔티티도 함께 조회(즉시 로딩)**
- **페치 조인은 객체 그래프를 SQL 한번에 조회하는 개념**

![](https://images.velog.io/images/songs4805/post/620b3c55-081e-469f-90d0-a0ab80d361b5/IMG_BD9BC1A5124F-1.jpeg)

> 참고로 즉시로딩과 fetch 조인에 관련된 의문은 다음을 확인하자.
> → [fetch 조인, 엔티티 그래프 질문입니다. - inflearn](https://www.inflearn.com/questions/39516)

### 페치 조인의 특징과 한계

#### **페치 조인 대상에는 별칭을 줄 수 없다.** (그냥 배제하자)

- 하이버네이트는 가능하지만, 가급적 사용하지 않는 것이 좋다.
- JPA의 설계 사상은 객체 그래프를 탐색한다는 것은 연관된 엔티티 모두를 가져온다는 것을 가정하고 만들어 졌다.
- fetch join에 별칭을 붙이고 where절을 더해 필터해서 결과를 가져오게 되면 모든걸 가져온 결과와 비교하여 다른 갯수에 대해 정합성을 보장하지 않는다.

```java
// as m 이라는 별칭(alias)는 fetch join에서 사용할 수 없다.
String query = "select t from Team t join fetch t.members as m";
```

팀을 조회하는 상황에서 멤버가 5명인데 3명만 조회한 경우, 3명만 따로 조작하는 것은 몹시 위험하다.

```java
String query = "select t from Team t join fetch t.members as m where m.age > 10";
```

#### **둘 이상의 컬렉션은 페치 조인 할 수 없다.**

```java
String query = "select t from Team t join fetch t.members, t.orders";
```

#### **컬렉션을 페치 조인하면 페이징 API를 사용할 수 없다.**

- 일대일, 다대일 같은 단일 값 연관 필드들은 페치 조인해도 페이징이 가능함.
- 하이버네이트는 경고 로그를 남기고 메모리에서 페이징(매우 위험함)

```java
String query = "select t From Team t join fetch t.members m";

List<Team> result = em.createQuery(query, Team.class)
        .setFirstResult(0)
        .setMaxResults(1)
        .getResultList();
```

![](https://images.velog.io/images/songs4805/post/a92c9196-7a3e-43e9-af2e-022bfe78eeda/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-11%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%203.19.40.png)

로그를 보면 경고 로그가 출력된 것을 확인할 수 있고, 메모리에서 페이징을 하면서 쿼리상에는 limit offset이 없다.  
해결 방안은 다음과 같다.

- 일대다를 다대일로 방향을 전환하여 해결한다.

```java
String query = "select m From Member m join fetch m.team t";
```

- `@BatchSize()`

```java
public class Team {
...
@BatchSize(size = 100)
@OneToMany(mappedBy = "team")
private List<Member> members;
...
}

String query = "select t From Team t";
```

지연 로딩 상태이지만, 조회 시 members를 BatchSize의 size 만큼 조회해온다.

`BatchSize()`는 글로벌 설정으로도 할 수 있다. (실무에서 이렇게 관리하신다고 함)

```xml
<!-- persistence.xml -->
<property name="hibernate.default_batch_fetch_size" value="100"/>
```

- 연관된 엔티티들을 SQL 한 번으로 조회 - 성능 최적화
- 엔티티에 직접 적용하는 글로벌 로딩 전략보다 우선함
  - `@OneToMany(fetch = FetchType.LAZY)` (글로벌 로딩 전략)
- 실무에서 글로벌 로딩 전략은 모두 지연로딩
- 최적화가 필요한 곳은 페치 조인 적용

### 페치 조인 - 정리

- 모든 것을 페치 조인으로 해결할 수는 없다.
- 페치 조인은 객체 그래프를 유지할 때 사용하면 효과적이다.
- **여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야 하면, 페치 조인 보다는 일반 조인을 사용하고 필요한 데이터들만 조회해서 DTO로 반환하는 것이 효과적이다.**

## 다형성 쿼리

![](https://images.velog.io/images/songs4805/post/a00168c2-266b-41cb-8592-80d9e5b9e166/image.png)

### TYPE

- 조회 대상을 특정 자식으로 한정
- ex) Item 중에 Book, Movie를 조회해라

```sql
// JPQL
select i from Item i where type(i) IN(Book, Movie);

// SQL
select i from Item i where i.DTYPE in ('B', 'M');
```

### TREAT(JPA 2.1)

- 자바의 타입 캐스팅과 유사함(형변환)
- 상속 구조에서 부모 타입을 특정 자식 타입으로 다룰 때 사용
- FROM, WHERE, SELECT(하이버네이트 지원) 사용
- ex) 부모인 Item과 자식 Book이 있다.

```sql
// JPQL
select i from Item i where treat(i as Book).auther = 'kim';

// SQL
select i.* from Item i where i.DTYPE = 'B' and i.auther = 'kim';
```

## 엔티티 직접 사용

### 기본 키 값

- JPQL에서 엔티티를 직접 사용하면 SQL에서 해당 엔티티의 기본 키 값을 사용

```sql
// JPQL
select count(m.id) from Member m; // 엔티티의 아이디를 사용
select count(m) from Member m;    // 엔티티를 직접 사용

// SQL(JPQL 둘 다 같은 다음 SQL 실행)
select count(m.id) as cnt from Member m;
```

- 엔티티를 파라미터로 전달하거나 식별자를 직접 전달하더라도 실행된 SQL은 같다.

```java
// 엔티티를 파라미터로 전달
String query = "select m from Member m where m = :member";
Member findMember = em.createQuery(query, Member.class)
        .setParameter("member", member1)
        .getSingleResult();

// 식별자를 직접 전달
String query = "select m from Member m where m.id = :memberId";
Member findMember = em.createQuery(query, Member.class)
        .getParameter("memberId", member1.getId())
        .getSingleResult();
```

위 두 JPQL의 실행된 SQL은 아래와 같다.

```sql
select m.* from Member m where m.id=?
```

### 외래 키 값

```java
Team team = em.find(Team.class, 1L);

String query = "select m from Member m where m.team = :team";
List<Member> members = em.createQuery(query, Member.class)
        .getParameter("team", teamA)
        .getResultList();

String query = "select m from Member m where m.team.id = :teamId";
List<Member> members = em.createQuery(query, Member.class)
        .getParameter("teamId", teamA.getId)
        .getResultList();
```

위 두 JPQL의 실행된 SQL은 아래와 같다.

```sql
select m.* from Member m where m.team_id=?
```

## Named 쿼리

- 미리 정의해서 이름을 부여해두고 사용하는 JPQL
- 정적 쿼리
- 어노테이션, XML에 정의
- **애플리케이션 로딩 시점에 초기화 후 재사용**
  → JPA는 결국 SQL로 parsing 되어 사용되는데 로딩 시점에 초기화가 된다면 parsing cost를 절약 가능
- **애플리케이션 로딩 시점에 쿼리를 검증**

### 어노테이션에 정의

```java
@Entity
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username")
public class Member { ... }

List<Member> resultList = em.createQuery("Member.findByUsername", Member.class)
        .setParameter("username", "회원1")
        .getResultList();
```

![](https://images.velog.io/images/songs4805/post/189ee16b-1ee6-436a-b74e-f17b63a81d8d/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-11%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%208.12.16.png)

### XML에 정의

`META_INF/persistence.xml`

```xml
<persistnece-unit name="jpabook">
  <mapping-file>META_INF/ormMember.xml</mapping-file>
```

`META_INF/ormMember.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm" version="2.1">

  <named-query name="Member.findByUsername">
    <query>
      <![CDATA[select m from Member m where m.username = :username]]>
    </query>
  </named-query>

  <named-query name="Member.count">
    <query>
      select count(m) from Member m
    </query>
  </named-query>
</entity-mappings>
```

### Named 쿼리 환경에 따른 설정

- XML이 항상 우선권을 가진다.
- 애플리케이션 운영 환경에 따라 다른 XML을 배포할 수 있다.

### 📌 SpringData JPA를 사용한다면, NamedQuery를 이미 사용하고 있는 것이다.

```java
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select u from User u where u.username = ?1")
    Member findByUsername(String username);
}
```

`@Repository` 어노테이션이 등록된 인터페이스에서 사용되는 `@Query` 어노테이션에 있는 JPQL(or native)들이 NamedQuery로써 컴파일 시에 등록되는 것이다.  
// 실무에서 이 방식이 많이 쓰인다.

## 벌크 연산

- 재고가 10개 미만인 모든 상품의 가격을 10% 상승하려면?
- JPA 변경 감지 기능으로 실행하려면 너무 많은 SQL 실행
  1. 재고가 10개 미만인 상품을 리스트로 조회한다.
  2. 상품 엔티티의 가격을 10% 증가한다.
  3. 트랜잭션 커밋 시점에 변경감지가 동작한다.
- 변경된 데이터가 100건이라면 100번의 UPDATE SQL 실행

### 벌크 연산 예제

- 쿼리 한 번으로 여러 테이블 로우 변경(엔티티)
- **`executeUpdate()`의 결과는 영향받은 엔티티 수 반환**
- **UPDATE, DELETE 지원**
- **INSERT(insert into .. select, 하이버네이트 지원)**

```java
String query = "update Product p " +
                "set p.price = p.price * 1.1 where p.stockAmount < :stockAmount";

int resultCount = em.createQuery(query)
                    .setParameter("stockAmount", 10)
                    .executeUpdate();
```

### 벌크 연산 주의

- 벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리한다.
  - 벌크 연산을 먼저 실행
  - **벌크 연산 수행 후 영속성 컨텍스트 초기화**
    → 엔티티 조회 후 벌크연산으로 엔티티 업데이트가 되버리면 DB의 엔티티와 영속성 컨텍스트의 엔티티가 서로 다른 값이 되게 한다.

// 추가로 읽어볼 자료 - [Spring Data JPA의 `@Modifying`](https://velog.io/@dnjscksdn98/JPA-Hibernate-Spring-Data-JPA-Modifying)
