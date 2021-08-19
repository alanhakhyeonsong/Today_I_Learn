# JPA 소개
## SQL 중심적 개발의 문제점
1. 중복되는 SQL과 코드작성의 무한 반복
2. SQL에 의존적인 개발
3. 진정한 의미의 계층 분할이 어렵다.
4. 엔티티를 신뢰할 수 없다.

## 객체와 관계형 데이터베이스의 차이
1. 상속     
객체는 상속관계가 있지만, 관계형 데이터베이스는 상속 관계가 없다.
2. 연관관계    
객체는 reference(참조)를 가지고 있다. // 연관된 객체를 getter로 가져올 수 있는 것.  
관계형 데이터베이스는 PK(Primary Key)나 FK(Foreign Key)로 join을 해서 필요한 데이터를 찾을 수 있다.
3. 데이터 타입
4. 데이터 식별 방법

이와 같은 차이점으로부터 패러다임의 불일치가 일어난다. 이 차이를 극복하기 위해 개발자가 너무 많은 시간과 코드를 소비한다는 점과 객체지향적 모델링을 할수록 패러다임의 불일치 문제가 더 커진다는 점이 문제가 된다. 결과적으로 점점 데이터 중심의 모델로 변해가기 때문에 이를 해결하고 정교한 객체 모델링을 유지하게 도와주는 것이 JPA이다.

## JPA
- Java Persistence API
- 자바 진영의 ORM 기술 표준

> ### ORM
> - Object-relational mapping(객체 관계 매핑)
> - 객체는 객체대로 설계
> - 관계형 데이터베이스는 관계형 데이터베이스대로 설계
> - ORM 프레임워크가 중간에서 매핑
> - 대중적인 언어에서는 대부분 ORM 기술이 존재

### 기존의 JDBC API와 SQL 중심 동작
<img src="./images/sql중심.jpg">

### **JPA는 애플리케이션과 JDBC 사이에서 동작**
<img src="./images/jpa.png">
Java 애플리케이션에서 JPA에 명령하면, JPA는 JDBC API를 사용해서 SQL을 만들어 DB에 보낸다.

### JPA 동작 - 저장
<img src="./images/jpainsert.png">

```java
jpa.persist(member); // 저장
```
- JPA에게 Member 객체를 넘기면 JPA가 객체를 분석
- INSERT query 생성(JPA가 query를 만듬)
- JPA가 JDBC API를 사용해서 INSERT query를 DB에 보냄
- 패러다임의 불일치 해결

### JPA 동작 - 조회
<img src="./images/jpafind.png">

```java
Member member = jpa.find(memberId); // 조회
```

- JPA는 PK 값으로 find 요청
- JPA는 SELECT query 생성
- JDBC API를 통해 DB에 보내고 결과를 받음
- ResultSet 매핑
- 패러다임 불일치 해결


## 왜 JPA를 사용해야 하는가?
### 생산성
지루하고 반복적인 코드와 CRUD용 SQL을 개발자가 직접 작성하지 않아도 되며, 더 나아가 JPA에는 `CREATE TABLE` 같은 DDL 문을 자동으로 생성해주는 기능이 있음.

CRUD가 다음과 같이 다 정의 되어 있다. // 코드가 Java의 컬렉션 프레임워크 같이 간결함.
- 저장: `jpa.persist(member)`
- 조회: `Member member = jpa.find(memberId)`
- 수정: `member.setName("변경할 이름")`
- 삭제: `jpa.remove(member)`

### 유지보수
기존에는 필드 변경시 모든 SQL을 수정했어야 했다. 예를 들어, 필드 한 개가 추가되면 그것을 사용하는 모든 곳을 변경해야 한다.

JPA를 사용하면 필드만 추가하면 된다. SQL은 JPA가 처리함.

### JPA와 패러다임의 불일치 해결
1. JPA와 상속
2. JPA와 연관관계
3. JPA와 객체 그래프 탐색
4. JPA와 비교하기

#### JPA와 상속
<img src="./images/jpa와 상속.png">

- 저장     
`jpa.persist(album);`을 명령하면 JPA가 알아서 처리해줌. JPA는 persist 명령을 받고, ITEM 테이블, ALBUM 테이블 두 군데 모두 INSERT 쿼리를 날린다.

- 조회    
`jpa.find(Album.class, albumId);` 라는 명령을 하면 JPA가 알아서 처리해줌. JPA는 Item과 Album을 join해서 찾아옴.

- JPA와 연관관계, 객체 그래프 탐색
```java
// 연관관계 저장
member.setTeam(team);
jpa.persist(member);
```

```java
// 객체 그래프 탐색
Member member = jpa.find(Member.class, memberId);
Team team = member.getTeam(); // 자유로운 객체 그래프 탐색
```
연관 관계를 저장하고, 가져올 때는 마치 java collection에 넣었던 것처럼 꺼내올 수 있다.

#### JPA와 비교하기
```java
String memberId = "100";
Member member1 = jpa.find(Member.class, memberId);
Member member2 = jpa.find(Member.class, memberId);

if (member1 == member2) return true; // 결과는 같다.
else return false;
```
동일한 트랜잭션에서 조회한 엔티티는 같음을 보장한다.    
(1차 캐시를 사용하기 때문)

## JPA와 성능 최적화 기능
### 1. 1차 캐시와 동일성(identity) 보장
- 같은 트랜잭션 안에서는 같은 엔티티를 반환 - 약간의 조회 성능 향상
- DB Isolation Level이 Read Commit이어도 애플리케이션에서 Repeatable Read 보장
```java
String memberId = "100";
Member m1 = jpa.find(Member.class, memberId); // SQL
Member m2 = jpa.find(Member.class, memberId); // 캐시

println(m1 == m2) // true
```

위의 코드에서 SQL은 1번만 실행된다.

### 2. 트랜잭션을 지원하는 쓰기 지연 - INSERT
- 트랜잭션을 커밋할 때까지 INSERT SQL을 모음
- JDBC BATCH SQL 기능을 사용해서 한번에 SQL 전송
```java
transaction.begin(); // 트랜잭션 시작

em.persist(memberA);
em.persist(memberB);
em.persist(memberC);
// 여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.
// 커밋하는 순간 데이터베이스에 INSERT SQL을 모아서 보낸다.
transaction.commit(); // 트랜잭션 커밋
```

### 3. 지연 로딩과 즉시 로딩
- 지연 로딩: 객체가 실제 사용될 때 로딩
- 즉시 로딩: JOIN SQL로 한번에 연관된 객체까지 미리 조회
```java
// 지연 로딩
Member member = memberDAO.find(memberId); // SELECT * FROM MEMBER
Team team = member.getTeam();
String teamName = team.getName(); // SELECT * FROM TEAM
```

```java
// 즉시 로딩
Member member = memberDAO.find(memberId); // SELECT M.*, T.* FROM MEMBER JOIN TEAM ...
Team team = member.getTeam();
String teamName = team.getName();
```

지연 로딩은 member를 조회했을 때, member만 가져온다.   
그리고 3번째 줄에서 team.getName을 했을 때, JPA가 DB에 Team에 대한 query를 날려서 가져온다.     
**즉, 필요한 시점에 쿼리를 날려 값을 가져온다.**

즉시 로딩은 member를 조회했을 때, member와 연관된 객체까지 모두 가져온다.

> member를 가져왔을 때 member만 쓴다고 하면 지연 로딩이 좋고, member와 연관 객체를 같이 쓰는 경우엔 즉시 로딩이 좋다.