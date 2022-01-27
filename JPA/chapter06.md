# 다양한 연관관계 매핑

## 연관관계 매핑시 고려사항 3가지

### 다중성

- **다대일**: `@ManyToOne`
- **일대다**: `@OneToMany`
- 일대일: `@OneToOne`
- 다대다: `@ManyToMany`

### 단방향, 양방향

- 테이블
  외래키 하나로 양쪽 조인 가능. 사실 방향이라는 개념이 없다.

- 객체
  참조용 필드가 있는 쪽으로만 참조 가능. 한쪽만 참조하면 단방향, 양쪽이 서로 참조하면 양방향

### 연관관계의 주인

- 테이블은 **외래키 하나**로 두 테이블이 연관관계를 맺음
- 객체 양방향 관계는 a->b, b->a 처럼 **참조가 2곳**
- 객체 양방향 관계는 참조가 2군데 있음. 둘 중 테이블의 외래키를 관리할 곳을 지정해야 한다.
- **연관관계의 주인: 외래키를 관리하는 참조** (외래키를 가진 테이블과 매핑한 엔티티가 연관관계의 주인)
- 주인의 반대편: 외래키에 영향을 주지 않는다. 단순 조회만 가능함. (`mappedBy`)

## 다대일(N:1)

### 다대일 단방향(N:1)

![](https://images.velog.io/images/songs4805/post/fbebec25-4af5-4027-b74e-d163b9c03edd/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-26%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%205.37.15.png)

- **가장 많이 사용**하는 연관관계이다.
- **다대일의 반대는 일대다**

```java
@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    private String username;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID") // 외래키(TEAM_ID)와 team 필드를 매핑
    private Team team;

    // Getter, Setter
    ...
}

@Entity
public class Team {

    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    // Getter, Setter
    ...
}
```

회원은 `Member.team`으로 팀 엔티티를 참조할 수 있지만, 팀에는 회원을 참조하는 필드가 없다.

### 다대일 양방향(N:1, 1:N)

![](https://images.velog.io/images/songs4805/post/76f8f771-9149-407f-82c0-9b0155fc268b/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-26%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%205.47.05.png)

- 외래키가 있는 쪽이 연관관계의 주인
- 양쪽을 서로 참조하도록 개발
- 연관관계의 주인이 아닌 쪽은 단순 조회만 가능하기에 필드만 추가해주면 된다.

```java
@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    private String username;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID") // 외래키(TEAM_ID)와 team 필드를 매핑
    private Team team;

    public void setTeam(Team team) {
        this.team = team;
        if (!team.getMembers().contains(this)) { // 무한루프에 빠지지 않도록 체크
            team.getMembers().add(this);
        }
    }

    // Getter, Setter
    ...
}

@Entity
public class Team {

    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public void addMember(Member member) {
        this.members.add(member);
        if (member.getTeam() != this) { // 무한루프에 빠지지 않도록 체크
            member.setTeam(this);
        }
    }

    // Getter, Setter
    ...
}
```

연관관계 편의 메소드(`setTeam()`, `addMember()`)를 작성해서 항상 서로 참조하게 만들어 둔다.
보통 편의 메소드는 한 곳에만 작성하는데, 양쪽에 다 작성하면 **무한루프**에 빠지므로 주의해야 한다.
위 처럼 무한루프에 빠지지 않도록 검증 로직을 작성하고, 사용 시 둘 중 하나만 호출해서 사용하도록 하자.

## 일대다(1:N)

**일(1)이 연관관계의 주인이다. 권장하는 방법도 아니고 실무에서도 거의 사용되지 않는다고 한다.**

### 일대다 단방향

![](https://images.velog.io/images/songs4805/post/79de05ac-6ef5-4779-afaa-25423cfaf23b/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-26%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.03.33.png)

- 테이블 일대다 관계는 항상 **다(N) 쪽에 외래키가 있다.**
- 객체와 테이블의 차이 때문에 반대편 테이블의 외래키를 관리하는 특이한 구조

권장하지 않는 이유는

- 테이블에선 항상 다(N) 쪽에 외래키가 있기 때문에 패러다임 충돌이 있다.
- `@JoinColumn`을 꼭 사용해야하고, 그렇지 않다면 조인 테이블 방식을 사용해야 한다.(중간에 테이블을 하나 추가)
- 실무에선 테이블이 수십개 이상 운영되는데, 관리 및 트레이싱이 어렵다고 함.
  -> ex) 일대다에서 저장(save)이 될 때 양쪽 객체를 저장한 뒤 추가적인 update query를 통해 외래키 설정(3번이나 수행됨)

**일대다 단방향 매핑보다는 다대일 양방향 매핑을 사용하자.**

// 참고: [JPA - One To Many 단방향의 문제점](https://dublin-java.tistory.com/51)

### 일대다 양방향

이런 매핑은 공식적으로 존재하지 않는다. 대신 **다대일 양방향 매핑을 사용해야 한다.**
![](https://images.velog.io/images/songs4805/post/1953c612-e097-4de8-a6eb-1e6b74c18ca2/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-26%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.14.58.png)

```java
public class Team {
    ...
    @OneToMany
    @JoinColumn(name="TEAM_ID")
    private List<Member> members = new ArrayList<>();
    ...
}

public class Member {
    ...
    @ManyToOne
    @JoinColumn(name="TEAM_ID", insertable=false, updatable=false)
    private Team team;
    ...
}
```

- 읽기 전용 필드를 사용해서 양방향 처럼 사용하는 방법이다.
- `@JoinColumn(insertable=false, updatable=false)`: 다대일 쪽은 읽기만 가능하게 설정

// 더 알아보기: [@ManyToOne 에는 왜 mappedBy 속성이 없을까요?](https://www.inflearn.com/questions/18042)

## 일대일(1:1)

![](https://images.velog.io/images/songs4805/post/a79026a4-c112-42a1-9a3d-bf9c997660b4/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-26%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.25.35.png)
// 주 테이블에 외래키 단방향

- **일대일 관계는 그 반대도 일대일**
- 주 테이블이나 대상 테이블 중에 외래키 선택 가능
- 외래키에 데이터베이스 유니크(UNI) 제약조건 추가
- 다대일(`@ManyToOne`) 단방향 매핑과 유사
- 양방향일 경우도 다대일과 유사: 외래키가 있는 곳이 연관관계의 주인
- 연관관계의 주인이 아닌 곳에 `mappedBy`를 넣어준다.

### 주 테이블에 외래키

- 주 객체가 대상 객체의 참조를 가지는 것처럼 주 테이블에 외래키를 두고 대상 테이블을 찾음
- 객체지향 개발자 선호
- JPA 매핑 편리
- 장점: 주 테이블만 조회해도 대상 테이블에 데이터가 있는지 확인 가능
- 단점: 값이 없으면 외래키에 null 허용

// 일대일에선 대상 테이블에 외래키를 두기보단 이 방식이 차라리 나음

### 대상 테이블에 외래키

![](https://images.velog.io/images/songs4805/post/486baf12-c3eb-4d1d-9600-9d4aa183e806/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-26%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.42.14.png)
// 대상 테이블에 외래키 양방향

- 대상 테이블에 외래키가 존재
- 전통적인 데이터베이스 개발자 선호
- 장점: 주 테이블과 대상 테이블을 일대일에서 일대다 관계로 변경할 때 테이블 구조 유지
- 단점: 프록시 기능의 한계로 **지연 로딩으로 설정해도 항상 즉시 로딩됨**

// 대상 테이블에 외래키 단방향 관계는 JPA 지원 x, 양방향 관계는 지원 o

## 다대다(N:M)

**실전에선 절대 쓰면 안되는 구조이다.**

관계형 데이터베이스는 정규화된 테이블 2개로 다대다 관계를 표현할 수 없다.
중간에 연결 테이블을 추가해서 일대다, 다대일 관계로 풀어내야 한다.
![](https://images.velog.io/images/songs4805/post/73d7d5ed-1931-4f15-a3f9-e5757339852c/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-26%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.45.59.png)

반면 **객체는 컬렉션을 사용해서 객체 2개로 다대다 관계가 가능하다.**
![](https://images.velog.io/images/songs4805/post/9b06bd77-50b0-4a1b-bd07-de721c7ce221/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-26%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.47.02.png)

- `@ManyToMany`를 사용한다.
- `JoinTable`로 연결 테이블을 지정한다.
- 단방향, 양방향 모두 가능하다.

### 다대다 매핑의 한계

![](https://images.velog.io/images/songs4805/post/1af7c6cc-3435-4cbd-bb27-eb0a1271c34f/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-26%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.51.04.png)

- **편리해 보이지만 실무에선 사용하면 안된다.**
- 연결 테이블이 단순히 연결만 하고 끝나지 않는다.
- 주문시간, 수량 같은 데이터가 들어올 수 있는데 이 경우 못들어간다.(컬럼을 추가하면 더는 `@ManyToMany` 사용 불가함. 주문, 상품 엔티티에는 추가한 컬럼들을 매핑할 수 없음.)
- 중간테이블에 추가적인 데이터를 넣을 수 없다는 한계점이 존재한다.
- 중간테이블이 숨겨져 있기 때문에 의도치 않은 쿼리가 생성될 수 있다.

### 다대다 한계 극복

- **연결 테이블용 엔티티를 추가한다.(연결 테이블을 엔티티로 승격)**
  // ex) Order와 Item 사이에 OrderItem 연결 테이블을 엔티티로 추가
- `@ManyToMany` -> `@OneToMany` + `@ManyToOne`로 풀어서 연결 테이블을 매핑하자.
- 아래 그림 처럼 새로운 PK(`ORDER_ID`)를 선언해서 사용하는게 복합키 PK 선언보다 조금 더 선호됨.
  ![](https://images.velog.io/images/songs4805/post/a5d25fd8-7935-410a-804e-6972ad90c1ac/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-26%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.53.00.png)

## References

- [김영한님의 자바 ORM 표준 JPA 프로그래밍 - 기본편(인프런)](https://www.inflearn.com/course/ORM-JPA-Basic#)
- [자바 ORM 표준 JPA 프로그래밍 - 기본편](http://www.yes24.com/Product/Goods/19040233)
- [JPA - One To Many 단방향의 문제점](https://dublin-java.tistory.com/51)
- [@ManyToOne 에는 왜 mappedBy 속성이 없을까요?](https://www.inflearn.com/questions/18042)
