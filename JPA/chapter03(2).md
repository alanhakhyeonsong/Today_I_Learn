# 영속성 관리

## 엔티티 조회

### 1차 캐시

영속성 컨텍스트는 내부에 캐시를 가지고 있는데 이를 **1차 캐시**라고 한다. 영속 상태의 엔티티는 모두 이곳에 저장된다. 다음 그림과 같이 영속성 컨텍스트 내부에 키는 `@Id`로 매핑한 식별자, 값은 엔티티 인스턴스인 Map이 있다.

> 📌 참고
> 일반적으로 트랜잭션을 시작하고 종료할 때 까지만 1차 캐시가 유효하다. 영속성 컨텍스트는 트랜잭션이 끝날 때 모두 종료된다.
>
> 2차 캐시는 애플리케이션 단위의 캐시로 애플리케이션을 종료할 때까지 캐시가 유지된다. 2차 캐시를 사용하면 엔티티 매니저를 통해 데이터를 조회할 때 우선 2차 캐시에서 찾고 없으면 데이터베이스에서 찾게 된다. 따라서 2차 캐시를 적절히 활용하면 데이터베이스 조회 횟수를 획기적으로 줄일 수 있다.

<img width="489" alt="스크린샷 2022-01-20 오후 11 51 27" src="https://user-images.githubusercontent.com/60968342/150362175-fc06fb54-acd8-4d74-8cf4-c4147c0e2693.png" style="margin-left: auto; margin-right: auto; display: block;">

```java
// 엔티티를 생성한 상태(비영속)
Member member = new Member();
member.setId("member1");
member.setUsername("회원1");

// 엔티티를 영속. 1차 캐시에 저장됨.
em.persist(member);
```

### 1차 캐시에서 조회

1차 캐시의 키는 식별자 값이다. 이 값은 데이터베이스 기본 키와 매핑되어 있다. 따라서 **영속성 컨텍스트에 데이터를 저장하고 조회하는 모든 기준은 데이터베이스 기본 키 값이다.**

`em.find(Member.class, "member1");`

<img width="672" alt="스크린샷 2022-01-20 오후 11 57 58" src="https://user-images.githubusercontent.com/60968342/150363417-1ee1ad4b-8c4a-4417-9b00-ae35e9496aa7.png">

`em.find()`를 호출하면 먼저 메모리에 있는 1차 캐시에서 엔티티를 찾고, 1차 캐시에 없으면 데이터베이스에서 조회한다.

### 데이터베이스에서 조회

`em.find()`를 호출했는데 엔티티가 1차 캐시에 없으면 엔티티 매니저는 데이터베이스를 조회해서 엔티티를 생성한다. 그리고 1차 캐시에 저장한 후에 영속 상태의 엔티티를 반환한다.

`Member findMember2 = em.find(Member.class, "member2");`

<img width="1009" alt="스크린샷 2022-01-21 오전 12 01 57" src="https://user-images.githubusercontent.com/60968342/150364161-d6ee525c-4878-47de-b388-875a506cb69e.png">

### 영속 엔티티의 동일성 보장

```java
Member a = em.find(Member.class, "member1");
Member b = em.find(Member.class, "member1");

System.out.println(a == b); // 동일성 비교 true
```

영속성 컨텍스트는 1차 캐시에 있는 같은 엔티티 인스턴스를 반환하기 때문에 결과가 참이다.
**영속성 컨텍스트는 성능상 이점과 엔티티의 동일성을 보장한다.**

## 엔티티 등록

### 트랜잭션을 지원하는 쓰기 지연

엔티티 매니저는 트랜잭션을 커밋하기 직전까지 데이터베이스에 엔티티를 저장하지 않고 내부 쿼리 저장소에 INSERT SQL을 차곡차곡 모아둔다. 그리고 트랜잭션을 커밋할 때 모아둔 쿼리를 데이터베이스에 보내는데 이 것을 **트랜잭션을 지원하는 쓰기 지연(transactional write-behind)** 이라 한다.

<img width="1003" alt="스크린샷 2022-01-21 오전 12 21 33" src="https://user-images.githubusercontent.com/60968342/150367967-7c63b1ad-b236-423b-8147-0e6a21a3582b.png">

<img width="881" alt="스크린샷 2022-01-21 오전 12 21 45" src="https://user-images.githubusercontent.com/60968342/150367995-cba40b85-77fa-4d6f-8dab-c7d63d332ad0.png">

```java
EntityManager em = emf.createEntityManager();
EntityTransaction transaction = em.getTransaction();
// 엔티티 매니저는 데이터 변경시 트랜잭션을 시작해야 한다.
transaction.begin(); // [트랜잭션] 시작

em.persist(memberA);
em.persist(memberB);
// 여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.

// 커밋하는 순간 데이터베이스에 INSERT SQL을 보낸다.
transaction.commit(); // [트랜잭션] 커밋
```

## 엔티티 수정

### 변경 감지(dirty checking)

```java
EntityManager em = emf.createEntityManager();
EntityTransaction transaction = em.getTransaction();
transaction.begin(); // [트랜잭션] 시작

// 영속 엔티티 조회
Member memberA = em.find(Member.class, "memberA");

// 영속 엔티티 데이터 수정
memberA.setUsername("hi");
memberA.setAge(10);

// em.update(member) 이런 코드는 필요 없음.

transaction.commit(); // [트랜잭션] 커밋
```

이 코드를 실행하면 SELECT 쿼리와 UPDATE 쿼리가 순서대로 발생한다. `em.update(member)` 라는 메소드 없이 엔티티의 데이터만 변경했는데 위와 같이 반영되는 이유가 뭘까?

이것이 **dirty checking**이다.

JPA는 엔티티를 영속성 컨텍스트에 보관할 때, 최초 상태를 복사해서 저장해두는데 이것을 **스냅샷**이라 한다. `flush()` 시점에 스냅샷과 엔티티를 비교해서 변경된 엔티티를 찾는 방식이다.

<img width="909" alt="스크린샷 2022-01-21 오전 12 35 54" src="https://user-images.githubusercontent.com/60968342/150370535-19e51d8a-debc-4cfa-8085-781d65651864.png">

1. 트랜잭션을 커밋하면 엔티티 매니저 내부에서 먼저 `flush()`가 호출된다.
2. 엔티티와 스냅샷을 비교해서 변경된 엔티티를 찾는다.
3. 변경된 엔티티가 있으면 수정 쿼리를 생성해서 쓰기 지연 SQL 저장소에 보낸다.
4. 쓰기 지연 저장소의 SQL을 데이터베이스에 보낸다.
5. 데이터베이스 트랜잭션을 커밋한다.

**변경 감지는 영속성 컨텍스트가 관리하는 영속 상태의 엔티티에만 적용된다.** 비영속, 준영속처럼 영속성 컨텍스트의 관리를 받지 못하는 엔티티는 값을 변경해도 데이터베이스에 반영되지 않는다.

## 엔티티 삭제

엔티티 등록과 비슷하게 즉시 삭제가 아닌 삭제 쿼리를 쓰기 지연 SQL 저장소에 등록하고, 이후 트랜잭션을 커밋해서 플러시를 호출하면 실제 데이터베이스에 삭제 쿼리를 전달한다.

> 참고로 `em.remove(memberA);`를 호출하는 순간 memberA는 영속성 컨텍스트에서 제거된다.

```java
// 삭제 대상 엔티티 조회
Member memberA = em.find(Member.class, "memberA");
em.remove(memberA); // 엔티티 삭제
```

## 플러시(`flush()`)

**플러시는 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영한다.**

- 변경 감지
- 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
- 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송(등록, 수정, 삭제 쿼리)

> 영속성 컨텍스트를 플러시하는 방법

- `em.flush()`를 직접 호출
- 트랜잭션 커밋 시 플러시가 자동 호출된다.
- JPQL 쿼리 실행 시 플러시가 자동 호출된다.

### JPQL 쿼리 실행 시 플러시가 자동 호출되는 이유?

```java
em.persist(memberA);
em.persist(memberB);
em.persist(memberC);

// 중간에 JPQL 실행
query = em.createQuery("select m from Member m", Member.class);
List<Member> members = query.getResultList();
```

memberA, memberB, memberC를 영속 상태로 만들었지만, 데이터베이스에 반영되지 않았기에 쿼리 결과로 조회 되지 않는다. JPA는 쿼리를 실행하기 직전에 영속성 컨텍스트를 플러시해서 변경 내용을 데이터베이스에 반영하여 문제를 예방하기 위해 JPQL을 실행할 때도 플러시를 자동 호출한다.

> 📌 참고
> JPQL은 1차 캐시를 무시하고 DB에 직접 SQL을 실행한다. JPQL은 광범위하게 데이터를 찾기 때문에 우선 DB에서 조회부터 한다. 다만, 이미 1차 캐시에 동일한 식별자의 엔티티가 있으면, DB에서 가져온 엔티티를 버리고 1차 캐시에 있는 엔티티를 유지한다. 그 결과 엔티티의 동일성은 유지된다.

### 플러시 모드 옵션

`em.setFlushMode(FlushModeType.COMMIT)`

- `FlushModeType.AUTO`: 커밋이나 쿼리를 실행할 때 플러시(default)
- `FlushModeType.COMMIT`: 커밋할 때만 플러시

정리하면, 플러시는 **영속성 컨텍스트를 비우지 않으며** **영속성 컨텍스트의 변경내용을 데이터베이스에 동기화**한다. **트랜잭션이라는 작업 단위가 중요하기 때문에 커밋 직전에만 동기화 하면 된다.**

## 준영속 상태

- 영속 → 준영속
- 영속 상태의 엔티티가 영속성 컨텍스트에서 분리(detached)
- 영속성 컨텍스트가 제공하는 기능을 사용 못함

`em.detach(entity)`: 특정 엔티티만 준영속 상태로 전환
`em.clear()`: 영속성 컨텍스트를 완전히 초기화
`em.close()`: 영속성 컨텍스트를 종료
