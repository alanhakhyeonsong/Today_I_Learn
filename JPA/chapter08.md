# 프록시와 연관관계 관리

## 프록시

엔티티를 조회할 때 연관된 엔티티들이 항상 사용되는 것은 아니다. 예를 들어, Member를 조회할 때 Team도 함께 조회해야 할까?

```java
@Entity
public class Member {
    private String username;

    @ManyToOne
    private Team team;

    // getter, setter
}

@Entity
public class Team {

    private String name;

    // getter, setter
}

// 회원과 팀 정보를 출력
public void printUserAndTeam(String memberId) {
    Member member = em.find(Member.class, memberId);
    Team team = member.getTeam();
    System.out.println("회원 이름: " + member.getUsername());
    System.out.println("소속팀: " + team.getName());
}

// 회원 정보만 출력
public String printUser(String memberId) {
    Member member = em.find(Member.class, memberId);
    System.out.println("회원 이름: " + member.getUsername());
}
```

`printUserAndTeam`는 memberId로 회원 엔티티를 찾아 회원, 회원과 연관된 팀의 이름도 출력한다.

`printUser`는 회원 엔티티만 출력하는 데 사용하고 회원과 연관된 팀 엔티티는 전혀 사용하지 않는다.
`em.find()`로 회원 엔티티를 조회할 때 회원과 연관된 팀 엔티티(Member.team)까지 데이터베이스에서 함께 조회해 두는 것은 효율적이지 않다.

**JPA는 이런 문제를 해결하기 위해 엔티티가 실제 사용될 때까지 데이터베이스 조회를 지연하는 방법을 제공하는데 이것을 지연 로딩이라 한다.**

지연 로딩 기능을 사용하려면 실제 엔티티 객체 대신 데이터베이스 조회를 지연할 수 있는 가짜 객체가 필요한데 이것을 **프록시 객체**라 한다.

### 프록시 기초

![](https://images.velog.io/images/songs4805/post/d56b495d-20e4-4832-93f3-0ff7738da791/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2012.36.34.png)

- `em.find()`: 데이터베이스를 통해서 실제 엔티티 객체 조회
- `em.getReference()`: **데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체 조회**

`getReference()`를 사용하면 데이터베이스를 조회하여 가져온 진짜 객체가 아닌 하이버네이트 내부 로직으로 프록시 엔티티 객체를 반환한다. 이 객체는 데이터베이스 접근을 위임받은 객체이다. 내부 구조는 같지만 내용이 비어있다.

프록시는 다음과 같은 특징을 가지고 있다.
![](https://images.velog.io/images/songs4805/post/82e53020-9605-4521-8c37-4bae26bc7a8e/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2012.56.22.png)

- 실제 클래스를 상속받아서 만들어졌다.
- 실제 클래스와 겉 모양이 같다.
- 사용하는 입장에서는 진짜 객체인지 프록시 객체인지 구분하지 않고 사용하면 된다(이론상).
- 프록시 객체는 실제 객체의 참조(target)를 보관한다.
- 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메소드를 호출한다.
- 프록시 객체는 처음 사용할 때 한 번만 초기화
- 프록시 객체를 초기화 할 때, 프록시 객체가 실제 엔티티로 바뀌는 것이 아니다. 초기화 되면 프록시 객체를 통해 실제 엔티티에 접근이 가능한 것.
- 프록시 객체는 원본 엔티티를 상속받았다. 타입 체크시 주의해야 함.(비교 시, == 대신 instance of 사용)
- 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 `em.getReference()`를 호출해도 실제 엔티티를 반환한다. (이미 1차 캐시에 올라와 있는데 프록시를 반환할 필요가 없음)
- 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화하면 문제가 발생한다. // 실무에서 자주 발생함.
  (org.hibernate.LazyInitializationException)

![](https://images.velog.io/images/songs4805/post/8fac217f-84d0-4cbe-9fae-94ff36ae81b8/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2012.57.58.png)

### 프록시 객체의 초기화

```java
Member member = em.getReference(Member.class, "id1"); // (1)
member.getName(); // (2)
```

![](https://images.velog.io/images/songs4805/post/9ae125fe-9a12-49f3-99a6-da8374124a09/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%201.00.17.png)

1. 프록시 객체에 `member.getName()`을 호출해서 실제 데이터를 조회
2. 프록시 객체는 실제 엔티티가 생성되어 있지 않으면 영속성 컨텍스트에 실제 엔티티 생성을 요청(초기화)
3. 영속성 컨텍스트는 데이터베이스를 조회해서 실제 엔티티 객체를 생성
4. 프록시 객체는 생성된 실제 엔티티 객체의 참조를 Member target 멤버 변수에 보관
5. 프록시 객체는 실제 엔티티 객체의 `getName()`을 호출해서 결과를 반환

### 프록시 확인

- 프록시 인스턴스의 초기화 여부 확인
  : `PersistenceUnitUtil.isLoaded(Object entity)`
- 프록시 클래스 확인 방법
  : `entity.getClass().getName()` 출력 (..javasist.. or HibernateProxy...)
- 프록시 강제 초기화
  : `org.hibernate.Hibernate.initialize(entity);`
- 참고: JPA 표준은 강제 초기화 없음
  강제 호출: member.getName()

## 즉시 로딩과 지연 로딩

### 지연 로딩

단순히 member 정보만 사용하는 비즈니스 로직.

지연로딩을 사용해 연관관계에 있는 다른 엔티티를 사용하는 빈도수가 낮을 경우 지연로딩을 사용해 불필요한 엔티티 조회를 막을 수 있다.

```java
@Entity
public class Member {
    // ...
    @ManyToOne(fetch = FetchType.LAZY) // 지연로딩 사용
    @JoinColumn(name = "TEAM_ID")
    private Team team;
    // ...
}

Member member = em.find(Member.class, member1.getId()); // Member 객체 반환
System.out.println("member = " + member.getTeam().getClass()); // Team$HibernateProxy 객체 반환
member.getTeam().getName(); // team을 실제로 사용하는 시점에 초기화(DB 조회)
```

![](https://images.velog.io/images/songs4805/post/1b34d894-814f-497c-98f2-3c8370442490/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%202.35.48.png)

```sql
// em.find(Member.class, "member1") 호출 시
SELECT * FROM MEMBER
WHERE MEMBER_ID = 'member1'

// team.getName() 호출 시 프록시 객체가 초기화 되면서 다음 SQL 실행됨.
SELECT * FROM TEAM
WHERE TEAM_ID = 'team1'
```

### 즉시 로딩

엔티티를 조회할 때 연관된 엔티티도 함께 조회한다.
Member를 가져오는 시점에서 연관관계에 있는 Team까지 바로 가져오는 것을 즉시 로딩이라 한다.

```java
@Entity
public class Member {
    // ...
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEAM_ID")
    private Team team;
    // ...
}
Member member = em.find(Member.class, "member1");
Team team = member.getTeam(); // 객체 그래프 탐색
```

![](https://images.velog.io/images/songs4805/post/ba3c30ed-6765-4d70-b826-07e9ccb17925/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%202.39.55.png)
대부분의 JPA 구현체는 **즉시 로딩을 최적화하기 위해 가능하면 조인 쿼리를 사용한다.**
여기서는 회원과 팀을 조인해 쿼리 한 번으로 두 엔티티를 모두 조회한다.

```sql
SELECT
    M.MEMBER_ID AS MEMBER_ID,
    M.TEAM_ID AS TEAM_ID,
    M.USERNAME AS USERNAME,
    T.TEAM_ID AS TEAM_ID,
    T.NAME AS NAME
FROM
    MEMBER M LEFT OUTER JOIN TEAM T
        ON M.TEAM_ID=T.TEAM_ID
WHERE
    M.MEMBER_ID='member1'
```

### 프록시와 즉시로딩 주의

- **가급적 지연 로딩만 사용(특히 실무에서)**
- 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생함.
- **즉시 로딩은 JPQL에서 N+1 문제를 일으킨다.**

```java
List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
// SQL: select * from Member
// SQL: select * from Team where TEAM_ID = xxx
```

**위 JPQL을 그대로 쿼리로 번역하게 되면 Member를 가져오기 위한 쿼리 수행 이후 바로 Member 내부의 Team을 가져오기 위한 쿼리를 다시 수행하게 된다. → N+1(1개의 쿼리를 날리면 +N개의 쿼리가 추가 수행)**

- **`@ManyToOne`, `@OneToOne`은 기본이 즉시로딩 → LAZY로 설정**
- `@OneToMany`, `@ManyToMany`는 기본이 지연 로딩

### N+1의 해결책

1. 우선 전부 지연로딩으로 설정
2. 그 다음 가져와야하는 엔티티에 한해서 fetch join을 사용해서 가져온다.

```java
List<Member> members = em.createQuery("select m from Member m join fetch m.team", Member.class).getResultList();
```

## 지연 로딩 활용

![](https://images.velog.io/images/songs4805/post/3384bf3b-67ff-44f4-982b-5a3a5db57808/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%203.11.46.png)

- Member와 Team은 자주 함께 사용: 즉시 로딩
- Member와 Order는 가끔 사용: 지연 로딩
- Order와 Product는 자주 함께 사용: 즉시 로딩

![](https://images.velog.io/images/songs4805/post/0bbc7bc2-e971-478a-a7af-25f45ae614c8/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%203.11.58.png)
![](https://images.velog.io/images/songs4805/post/6eeae6f4-1e3a-408f-a718-b84cdcc6c4d9/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%203.12.13.png)

### 지연 로딩 활용 - 실무

- **모든 연관관계에 지연 로딩을 사용해라**
- **실무에서 즉시 로딩을 사용하지 마라**
- JPQL fetch 조인이나, 엔티티 그래프 기능을 사용해라
- 즉시 로딩은 상상하지 못한 쿼리가 수행된다.

## 영속성 전이(CASCADE)

특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속상태로 만들고 싶을 때 사용
// ex) 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장
![](https://images.velog.io/images/songs4805/post/f8ffa5a9-9fd3-4dab-b160-83f15233a6f9/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%203.18.21.png)

### 영속성 전이: 저장

영속성 전이가 안되는 기본적인 엔티티 저장 방법은 다음과 같다.

```java
@Entity
public class Parent {
    ...
    @OneToMany(mappedBy = "parent")
    private List<Child> childList = new ArrayList<>();

    public void addChild(Child child) {
        childList.add(child);
        child.setParent(this);
    }
}

@Entity
public class Child {
    ...
    @ManyToOne
    @JoinColumn(name "parent_id")
    private Parent parent;
    ...
}

Child child1 = new Child();
Child child2 = new Child();
Parent parent = new Parent();
parent.addChild(child1);
parent.addChild(child2);

// persist를 3번이나 해야 함.
em.persist(parent);
em.persist(child1);
em.persist(child2);
```

**JPA에서 엔티티를 저장할 때 연관된 모든 엔티티는 영속 상태여야 한다.** 영속성 전이를 사용하면 부모만 영속 상태로 만들면 연관된 자식까지 한 번에 영속상태로 만들 수 있다.

```java
@Entity
public class Parent {
    ...
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Child> childList = new ArrayList<>();

    public void addChild(Child child) {
        childList.add(child);
        child.setParent(this);
    }
}

@Entity
public class Child {
    ...
    @ManyToOne
    @JoinColumn(name "parent_id")
    private Parent parent;
    ...
}

Child child1 = new Child();
Child child2 = new Child();
Parent parent = new Parent();
parent.addChild(child1);
parent.addChild(child2);

// parent만 persist하면 child도 같이 persist 된다.
em.persist(parent);
```

![](https://images.velog.io/images/songs4805/post/523d5c93-4c5d-4d2a-8625-f48ba0f8ef30/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%203.24.21.png)

> 📌 주의 사항!

- 영속성 전이는 연관관계를 매핑하는 것과 아무 관련이 없다.
- 엔티티를 영속화할 때 연관된 엔티티도 함께 영속화하는 편리함을 제공할 뿐이다.

> CASCADE의 종류?

- **ALL: 모두 적용**
- **PERSIST: 영속**
- **REMOVE: 삭제**
- MERGE: 병합
- REFRESH: REFRESH
- DETACH: DETACH

### 언제 사용해야 할까?

전이될 대상이 한 군데에서만 사용된다면 써도 된다.
하지만, 해당 엔티티(Child)가 특정 엔티티(Parent)에 종속되지 않고 여러 군데에서 사용된다면 사용하지 않는게 좋다.

- 라이프 사이클이 동일할 때
- 단일 소유자 관계일 때

// ex) 게시판과 첨부파일 관계

## 고아 객체

JPA는 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제하는 기능을 제공하는데, 이것을 **고아 객체(ORPHAN) 제거**라 함.

`orphanRemoval = true`

```java
@Entity
public class Parent {
    ...
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Child> childList = new ArrayList<>();

    public void addChild(Child child) {
        childList.add(child);
        child.setParent(this);
    }
    ...
}

@Entity
public class Child {
    ...
    @ManyToOne
    @JoinColumn(name "parent_id")
    private Parent parent;
    ...
}

Child child1 = new Child();
Child child2 = new Child();
Parent parent = new Parent();
parent.addChild(child1);
parent.addChild(child2);

em.persist(parent);

em.flush();
em.clear();

// 자식 엔티티를 컬렉션에서 제거
Parent findParent = em.find(Parent.class, parent.getId());
findParent.getChildList().remove(0); // orphanRemoval 동작
```

> 📌 주의 사항!

- 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능
- **참조하는 곳이 하나일 때 사용해야 함**
- **특정 엔티티가 개인 소유할 때 사용**
- `@OneToOne`, `@OneToMany`만 가능

> 참고: 개념적으로 부모를 제거하면 자식은 고아가 된다. 따라서 고아 객체 제거 기능을 활성화하면, 부모를 제거할 때 자식도 함께 제거된다. 이것은 `CascadeType.REMOVE`처럼 동작한다.

## 영속성 전이 + 고아객체, 생명주기

`cascade = CascadeType.ALL, orphanRemoval = true`를 동시에 사용할 경우?

- 스스로 생명주기를 관리하는 엔티티는 `em.persist()`로 영속화, `em.remove()`로 제거
- 두 옵션을 모두 활성화 하면 부모 엔티티를 통해서 자식의 생명주기를 관리할 수 있음
- 도메인 주도 설계(DDD)의 Aggregate Root 개념을 구현할 때 유용함
