# 값 타입

## 기본값 타입

### 엔티티 타입

- `@Entity`로 정의하는 객체
- 데이터가 변해도 식별자로 지속해서 추적 가능
- ex) 회원 엔티티의 키나 나이 값을 변경해도 식별자로 인식 가능

### 값 타입

- int, Integer, String 처럼 단순히 값으로 사용하는 자바 기본 타입이나 객체
- 식별자가 없고 값만 있으므로 변경시 추적 불가
- ex) 숫자 100을 200으로 변경하면 완전히 다른 값으로 대체

### 값 타입 분류

1. 기본값 타입: 자바가 제공하는 기본 데이터 타입
   자바 기본 타입(int, double)
   래퍼 클래스(Integer, Long)
   String
2. 임베디드 타입(embedded type, 복합 값 타입): JPA에서 사용자가 직접 정의한 값 타입
   ex) 우편번호, 좌표같은 복합 값을 Position 클래스로 만들어 쓰려고 하는 것
3. 컬렉션 값 타입(collection value type): 하나 이상의 값 타입을 저장할 때 사용
   Java collections(Array, Map, Set)에 값을 넣을 수 있는 것

### 값 타입: 기본값 타입

```java
@Entity
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private int age;
    ...
}
```

- 위 Member에서 String, int가 값 타입이다.
- Member 엔티티는 id라는 식별자 값도 가지고 생명주기도 있지만, 값 타입인 name, age 속성은 식별자 값도 없고 생명주기를 엔티티에 의존한다.  
  -> 회원을 삭제하면 이름, 나이 필드도 함께 삭제됨
- 값 타입은 공유하면 안된다.  
  ex) 다른 회원 엔티티의 이름을 변경시 나의 이름까지 변경되면 안됨(side effect)

> 📌 참고: 자바의 기본 타입은 절대 공유가 되지 않는다.

- int, double 같은 기본 타입(primitive type)은 절대 공유되지 않는다.
- 기본 타입은 항상 값을 복사한다. (`a=b` 는 b의 값을 복사해서 a에 입력)
- Integer 같은 래퍼 클래스나 String 같은 특수한 클래스는 공유 가능한 객체이지만 변경할 수 없다.

## 임베디드 타입(복합 값 타입)

- 새로운 값 타입을 직접 정의할 수 있다.
- JPA는 임베디드 타입(embedded type)이라 한다.
- 주로 기본 값 타입을 모아서 만들어서 복합 값 타입이라고도 한다.
- **int, String과 같은 값 타입**

### 예제

회원 엔티티는 이름, 근무 시작일, 근무 종료일, 주소 도시, 주소 번지, 주소 우편번호를 가진다.

- city, street, zipcode는 주소로 합칠 수 있을 것 같다.
- 근무 시작일, 근무 종료일은 근무시간으로 합칠 수 있을 것 같다.

![](https://images.velog.io/images/songs4805/post/9c1b96be-c779-4180-a003-e1aa86c19608/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-01%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%201.45.08.png)

회원 엔티티의 몇몇 값을 주소, 근무시간으로 합치면 어떻게 될까?
![](https://images.velog.io/images/songs4805/post/5b34e8e1-6cd2-4cf1-a56d-ab00958ab54b/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-01%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%201.45.23.png)

![](https://images.velog.io/images/songs4805/post/14142ade-882a-4fa4-8811-9c70ef2e26ec/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-01%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%201.46.33.png)

```java
@Entity
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String name;

    @Embedded
    private Period workPeriod; // 근무 기간

    @Embedded
    private Address homeAddress; // 집 주소
}

@Embeddable
public class Period {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    // getter, setter, 기본 생성자

    public boolean isWork(Date date) {
        // .. 값 타입을 위한 메소드를 정의할 수 있다.
    }
}

@Embeddable
public class Address {

    @Column(name = "city") // 매핑할 컬럼 정의 가능
    private String city;
    private String street;
    private String zipcode;
    // getter, setter, 기본 생성자
}
```

실행 결과 다음과 같이 생성되는 것을 확인할 수 있다. 테이블은 그대로 유지되고 코드는 객체지향스럽게 짤 수 있다.

![](https://images.velog.io/images/songs4805/post/62e5099f-5764-45d0-b529-8d480083658f/embedded%20type.png)

### 임베디드 타입 사용법

- `@Embeddable`: 값 타입을 정의하는 곳에 표시
- `@Embedded`: 값 타입을 사용하는 곳에 표시
- 기본 생성자는 필수이다.

### 임베디드 타입의 장점

- 재사용: Period나 Address는 다른 객체에서도 사용할 수 있어 재사용성을 높인다.
- 높은 응집도
- Period.isWork() 처럼 해당 값 타입만 사용하는 의미있는 메소드를 만들 수 있음
- 임베디드 타입을 포함한 모든 값 타입은, 값 타입을 소유한 엔티티에 생명주기를 의존함

엔티티와 임베디드 타입의 관계를 UML로 표현하면 **컴포지션(compositon) 관계**가 된다.

**임베디드 타입을 통해 객체를 분리하더라도 테이블은 하나만 매핑된다.**

![](https://images.velog.io/images/songs4805/post/872ae58c-40fa-4a92-95bc-23e1c46db8f0/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-01%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%202.08.37.png)

### 임베디드 타입과 테이블 매핑

- 임베디드 타입은 엔티티의 값일 뿐이다.
- 임베디드 타입을 사용하기 전과 후에 **매핑하는 테이블은 같다.**
- 객체와 테이블을 아주 세밀하게(find-grained) 매핑하는 것이 가능하다.
- 잘 설계한 ORM 애플리케이션은 매핑한 테이블의 수보다 클래스의 수가 더 많다.

### 임베디드 타입과 연관관계

![](https://images.velog.io/images/songs4805/post/86ce93f5-7311-4855-84b1-8338bddfbdd2/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-01%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%202.10.22.png)

Member 엔티티는 Address라는 임베디드 타입을 가질 수 있고, Address 역시 zipcode라는 값 타입을 가질 수 있다. 또한 Member 엔티티는 PhoneNumber라는 임베디드 타입을 가질 수 있고, **PhoneNumber는 PhoneEntity 라는 엔티티를 가질 수 있다.**

임베디드 타입 클래스 안에서 Column도 사용 가능하다.

```java
@Entity
public class Member {
    // ...
    @Embedded private Address address; // 임베디드 타입 포함
    @Embedded private PhoneNumber phoneNumber; // 임베디드 타입 포함
    // ...
}

@Embeddable
public class Address {
    private String street;
    private String city;
    private String state;

    @Embedded
    @Column(name = "ZIPCODE") // 이것 역시 가능함
    private Zipcode zipcode; // 임베디드 타입 포함
    ...
}

@Embeddable
public class Zipcode {
    private String zip;
    private String plusFour;
    ...
}

@Embeddable
public class PhoneNumber {
    private String areaCode;
    private String localNumber;

    @ManyToOne
    private PhoneEntity phoneEntity; // 엔티티 참조
    ...
}

@Entity
public class PhoneEntity {
    @Id
    private String name;
    ...
}
```

### @AttributeOverride: 속성 재정의

한 엔티티에서 같은 값 타입을 사용하면 컬럼 명이 중복된다.

이 때, `@AttributeOverrides`, `@AttributeOverride`를 사용해서 컬럼 명 속성을 재정의하면 해결할 수 있다.

```java
@Entity
public class Member {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @Embedded
    private Address homeAddress;

    @Embedded
    private Address companyAddress;
    // ...
}
```

위와 같은 코드에선 컬럼명 중복으로 인해 `error MappingException: Repeated column in mapping for entity`가 발생한다.

```java
@Embedded
@AttributeOverrides({
    @AttributeOverride(name="city", column=@Column(name="COMPANY_CITY")),
    @AttributeOverride(name="street", column=@Column(name="COMPANY_STREET")),
    @AttributeOverride(name="zipcode", column=@Column(name="COMPANY_ZIPCODE")),
})
private Address companyAddress;
```

위와 같이 `@AttributeOverride`를 사용해서 컬럼 명 속성을 재정의하면 Address의 field명들이 재정의 되어 정상 동작한다.

// 참고로 임베디드 타입의 값이 null이면 매핑한 컬럼 값은 모두 null이 된다.

## 값 타입과 불변 객체

값 타입은 복잡한 객체 세상을 조금이라도 단순화하려고 만든 개념이다. 따라서 값 타입은 단순하고 안전하게 다룰 수 있어야 한다.

### 값 타입 공유 참조

임베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면 위험하다.

![](https://images.velog.io/images/songs4805/post/2a0f1e53-b2f3-4117-94f6-bd91b37f6f0f/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-01%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%203.21.32.png)

city 값을 NewCity로 바꿔버린다면 부작용(side effect)가 발생한다.

```java
Address address = new Address("city", "street", "100000");

// member와 member2가 같은 address를 바라보고 있음.
Member member = new Member();
member.setName("member1");
member.setHomeAddress(address);
em.persist(member);

Member member2 = new Member();
member2.setName("member2");
member2.setHomeAddress(address); // 회원 1의 주소를 그대로 참조해서 사용
em.persist(member2);

member.getHomeAddress().setCity("newCity"); // member가 주소지를 변경하고 싶어 Address를 수정함.

tx.commit();
```

이렇게 되면 member2의 주소만 "newCity"로 바뀌길 기대했지만, member의 주소 역시 "newCity"로 변경되어 버린다. 이는 **member와 member2가 같은 address 인스턴스를 참조하기 때문**이다. **실행하면 영속성 컨텍스트는 member, member2 둘 다 속성이 변경된 것으로 판단하여 결과적으로 update query가 2번 날아가게 된다.**

```log
INFO: HHH000476: Executing import script 'org.hibernate.tool.schema.internal.exec.ScriptSourceInputNonExistentImpl@5ea4300e'
Hibernate:
    call next value for hibernate_sequence
Hibernate:
    call next value for hibernate_sequence
Hibernate:
    /* insert domain.Member
        */ insert
        into
            Member
            (city, street, zipcode, name, endDate, startDate, MEMBER_ID)
        values
            (?, ?, ?, ?, ?, ?, ?)
Hibernate:
    /* insert domain.Member
        */ insert
        into
            Member
            (city, street, zipcode, name, endDate, startDate, MEMBER_ID)
        values
            (?, ?, ?, ?, ?, ?, ?)
Hibernate:
    /* update
        domain.Member */ update
            Member
        set
            city=?,
            street=?,
            zipcode=?,
            name=?,
            endDate=?,
            startDate=?
        where
            MEMBER_ID=?
Hibernate:
    /* update
        domain.Member */ update
            Member
        set
            city=?,
            street=?,
            zipcode=?,
            name=?,
            endDate=?,
            startDate=?
        where
            MEMBER_ID=?
1월 13, 2022 6:13:20 오후 org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl stop
INFO: HHH10001008: Cleaning up connection pool [jdbc:h2:tcp://localhost/~/test3]
```

![](https://images.velog.io/images/songs4805/post/3fbe0907-39f1-40d0-9884-525265b45309/%E1%84%80%E1%85%A1%E1%86%B9%20%E1%84%90%E1%85%A1%E1%84%8B%E1%85%B5%E1%86%B8%20%E1%84%80%E1%85%A9%E1%86%BC%E1%84%8B%E1%85%B2%20%E1%84%8E%E1%85%A1%E1%86%B7%E1%84%8C%E1%85%A9%20side%20effect.png)

이러한 공유 참조로 인해 발생하는 버그는 정말 찾아내기 어렵다.

### 값 타입 복사

위와 같은 부작용을 막으려면 값을 복사해서 사용하면 된다.

- 값 타입의 실제 인스턴스인 값을 공유하는 것은 위험하다.
- 대신 값(인스턴스)를 복사해서 사용해야 한다.

![](https://images.velog.io/images/songs4805/post/a167fd94-a701-42c1-b610-89b186583d27/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-01%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%203.32.49.png)

```java
Address address = new Address("city", "street", "100000");

Member member = new Member();
member.setName("member1");
member.setHomeAddress(address);
em.persist(member);

// 값을 복사해서 새로운 newAddress 값을 생성
Address newAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());

Member member2 = new Member();
member2.setName("member2");
member2.setHomeAddress(newAddress);
em.persist(member2);

member.getHomeAddress().setCity("newCity");

tx.commit();
```

실행하면, 의도한 대로 member2의 주소만 변경되며, 영속성 컨텍스트는 member2의 주소만 변경된 것으로 판단하여 update query는 1번만 날아간다.

### 객체 타입의 한계

- 항상 값을 복사해서 사용하면 공유 참조로 인해 발생하는 부작용을 피할 수 있다.
- 문제는 임베디드 타입처럼 **직접 정의한 값 타입은 자바의 기본 타입이 아니라 객체 타입**이다.
- 자바 기본 타입에 값을 대입하면 값을 복사한다.
- **객체 타입은 참조 값을 직접 대입하는 것을 막을 방법이 없다.**
- **객체의 공유 참조는 피할 수 없다.**

```java
// 기본 타입
int a = 10;
int b = a; // 기본 타입은 값을 복사
b = 4; // a: 10, b: 4

// 객체 타입
Address a = new Address("Old");
Address b = a; // 객체 타입은 참조를 전달
b.setCity("New"); // a: New, b: New (인스턴스가 하나이기에 같이 변경됨)
```

`Address b = a.clone()` 처럼 항상 복사해서 넘겨야 한다.

### 불변 객체

- 객체 타입을 수정할 수 없게 만들면 **부작용을 원천 차단**
- **값 타입은 불변 객체(immutable object)로 설계해야 함**
- **불변 객체: 생성 시점 이후 절대 값을 변경할 수 없는 객체**
- 생성자로만 값을 설정하고 수정자(Setter)를 만들지 않으면 됨
- 참고: Integer, String은 자바가 제공하는 대표적인 불변 객체

예제를 보면 다음과 같다.

```java
@Embeddable
public clas Address {
    private String city;
    private String street;
    private String zipcode;

    public Address() {} // JPA에서 기본 생성자는 필수

    // 생성자로 초기 값을 설정한다.
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    // Getter는 만들고, Setter는 만들지 않는다.
}
```

이 후 값을 변경해야 하는 경우엔 다음과 같이 새로운 객체를 생성해서 사용해야 한다.

```java
Address address = member1.getHomeAddress();
Address newAddress = new Address("newCity", address.getStreet(), address.getZipcode());
member2.setHomeAddress(newAddress);
```

**정리하자면, 불변이라는 작은 제약으로 부작용이라는 큰 재앙을 막을 수 있다.**

> 📌 참고로 Java의 `clone()`을 정확하게 구현하기는 매우 까다롭다.  
> ([Effective Java 3/E - item 13 참고](https://github.com/alanhakhyeonsong/LetsReadBooks/blob/master/Effective%20Java%203E/contents/chapter03/item13.md)) 불변 객체를 새로 만들고자 한다면, 생성자나 별도의 생성 메서드를 사용하는 것을 추천한다.

## 값 타입의 비교

인스턴스가 달라도 그 안에 값이 같으면 같은 것으로 봐야 한다.

```java
int a = 10; int b = 10;
Address a = new Address("city");
Address b = new Address("city");
```

`int a`의 숫자 10과 `int b`의 숫자 10은 같다고 표현한다. 마찬가지로 `Address a`와 `Address b`는 같다고 표현한다.

따라서 값 타입을 비교할 때는 **동등성 비교**를 해야 한다.

- **동일성(identity) 비교**: 인스턴스의 참조 값을 비교, `==` 사용
- **동등성(equivalence) 비교**: 인스턴스의 값을 비교, `equals()` 사용

값 타입의 `equals()` 메소드를 적절하게 재정의 해야 한다. (주로 모든 필드 사용)  
// 참고: [Effective Java 3/E - item 11. equals를 재정의하려거든 hashCode도 재정의하라](https://github.com/alanhakhyeonsong/LetsReadBooks/blob/master/Effective%20Java%203E/contents/chapter03/item11.md)

## 값 타입 컬렉션

![](https://images.velog.io/images/songs4805/post/aaaded18-9aea-43ef-afac-ebef793ae3be/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-02-01%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%204.07.28.png)

- 값 타입을 하나 이상 저장할 때 사용
- `@ElementCollection`, `@CollectionTable` 사용
- 데이터베이스는 컬렉션을 같은 테이블에 저장할 수 없다.
- 컬렉션을 저장하기 위한 별도의 테이블이 필요하다.

참고로 **값 타입 컬렉션은 영속성 전이(CASCADE) + 고아 객체 제거 기능을 필수로 가진다.**

```java
@Entity
public class Member {
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @Embedded
    private Address homeAddress;

    @ElementCollection
    @CollectionTable(name = "FAVORITE_FOOD", joinColumns =
        @JoinColumn(name = "MEMBER_ID") // 값으로 사용되는 컬럼이 하나인 경우 예외적으로 허용
    )
    @Column(name = "FOOD_NAME")
    private Set<String> favoriteFoods = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "ADDRESS", joinColumns =
        @JoinColumn(name = "MEMBER_ID")
    )
    private List<Address> addressHistory = new ArrayList<>();

    // getter, setter
}
```

### 값 타입 저장 예제

```java
Member member = new Member();
member.setName("member1");
member.setHomeAddress(new Address("homeCity", "street", "100000"));

member.getFavoriteFoods().add("치킨");
member.getFavoriteFoods().add("족발");
member.getFavoriteFoods().add("피자");

member.getAddressHistory().add(new Address("old1", "street", "100000"));
member.getAddressHistory().add(new Address("old2", "street", "100000"));

em.persist(member);

tx.commit();
```

실행 결과는 다음과 같다.

```log
Hibernate:
    call next value for hibernate_sequence
Hibernate:
    /* insert domain.Member
        */ insert
        into
            Member
            (city, street, zipcode, name, endDate, startDate, MEMBER_ID)
        values
            (?, ?, ?, ?, ?, ?, ?)
Hibernate:
    /* insert collection
        row domain.Member.addressHistory */ insert
        into
            ADDRESS
            (MEMBER_ID, city, street, zipcode)
        values
            (?, ?, ?, ?)
Hibernate:
    /* insert collection
        row domain.Member.addressHistory */ insert
        into
            ADDRESS
            (MEMBER_ID, city, street, zipcode)
        values
            (?, ?, ?, ?)
Hibernate:
    /* insert collection
        row domain.Member.favoriteFoods */ insert
        into
            FAVORITE_FOOD
            (MEMBER_ID, FOOD_NAME)
        values
            (?, ?)
Hibernate:
    /* insert collection
        row domain.Member.favoriteFoods */ insert
        into
            FAVORITE_FOOD
            (MEMBER_ID, FOOD_NAME)
        values
            (?, ?)
Hibernate:
    /* insert collection
        row domain.Member.favoriteFoods */ insert
        into
            FAVORITE_FOOD
            (MEMBER_ID, FOOD_NAME)
        values
            (?, ?)
1월 13, 2022 11:37:18 오후 org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl stop
INFO: HHH10001008: Cleaning up connection pool [jdbc:h2:tcp://localhost/~/test3]
```

![](https://images.velog.io/images/songs4805/post/a52e7fb3-0dbf-461c-a04a-34719576cc49/image.png)

![](https://images.velog.io/images/songs4805/post/dbb1ac21-f331-4c2f-b772-c5b5c3e85f06/image.png)

### 값 타입 조회 예제

```java
em.persist(member);

em.flush();
em.clear();

System.out.println("============= START =============");
Member findMember = em.find(Member.class, member.getId());

List<Address> addressHistory = findMember.getAddressHistory(); // LAZY
for (Address address : addressHistory) {
    System.out.println("address = " + address.getCity());
}

Set<String> favoriteFoods = findMember.getFavoriteFoods(); // LAZY
for (String favoriteFood : favoriteFoods) {
    System.out.println("favoriteFood = " + favoriteFood);
}

tx.commit();
```

**값 타입 컬렉션도 지연 로딩 전략을 사용한다.**

`@ElementCollection(fetch = FetchType.LAZY)`

```log
============= START =============
Hibernate:
    select
        member0_.MEMBER_ID as MEMBER_I1_6_0_,
        member0_.city as city2_6_0_,
        member0_.street as street3_6_0_,
        member0_.zipcode as zipcode4_6_0_,
        member0_.name as name5_6_0_,
        member0_.endDate as endDate6_6_0_,
        member0_.startDate as startDat7_6_0_
    from
        Member member0_
    where
        member0_.MEMBER_ID=?
Hibernate:
    select
        addresshis0_.MEMBER_ID as MEMBER_I1_0_0_,
        addresshis0_.city as city2_0_0_,
        addresshis0_.street as street3_0_0_,
        addresshis0_.zipcode as zipcode4_0_0_
    from
        ADDRESS addresshis0_
    where
        addresshis0_.MEMBER_ID=?
address = old1
address = old2
Hibernate:
    select
        favoritefo0_.MEMBER_ID as MEMBER_I1_4_0_,
        favoritefo0_.FOOD_NAME as FOOD_NAM2_4_0_
    from
        FAVORITE_FOOD favoritefo0_
    where
        favoritefo0_.MEMBER_ID=?
favoriteFood = 족발
favoriteFood = 치킨
favoriteFood = 피자
```

### 값 타입 수정 예제

```java
// 잘못된 수정 방식이다.
// findMember.getHomeAddress().setCity("newCity");

// 임베디드 값 타입 수정
Address a = findMember.getHomeAddress();
findMember.setHomeAddress(new Address("newCity", a.getStreet(), a.getZipcode()));

// 기본값 타입 컬렉션 수정
findMember.getFavoritFoods().remove("치킨");
findMember.getFavoritFoods().add("김밥");

// 임베디드 값 타입 컬렉션 수정
findMember.getAddressHistory().remove(new Address("old1", "street", "10000"));
findMember.getAddressHistory().add(new Address("old1", "street", "10000"));
```

## 값 타입 컬렉션의 제약사항

- 값 타입은 엔티티와 다르게 식별자 개념이 없다.
- 값은 변경하면 추적이 어렵다.
- 값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다.
- 값 타입 컬렉션을 매핑하는 테이블은 모든 컬럼을 묶어서 기본키를 구성해야 한다. **(null 입력 x, 중복 저장 x)**
- **이러한 이유로 실무에선 사용하지 않는 것을 추천한다.**
  - 실무에서는 상황에 따라 **값 타입 컬렉션 대신에 일대다 관계를 고려**
  - 일대다 관계를 위한 엔티티를 만들고, 여기에서 값 타입을 사용
  - 영속성 전이(CASCADE) + 고아 객체 제거를 사용해서 값 타입 컬렉션처럼 사용
    // ex) AddressEntity

```java
@Entity
public class AddressEntity {

    @Id @GeneratedValue
    private Long id;

    @Embedded
    private Address address;
    ...
}

public class Member {
    ...
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MEMBER_ID")
    private List<AddressEntity> addressHistory = new ArrayList<AddressEntity>();
    ...
}
```

## 정리

- 엔티티 타입의 특징은 식별자가 있고 생명 주기가 있으며 공유할 수 있다.
- 반면, 값 타입의 특징은 식별자가 없고 생명 주기가 엔티티에 의존하며 공유하지 않는 것이 안전하다. 또한 불변 객체로 만드는 것이 안전하다.
- 값 타입은 정말 값 타입이라 판단될 때만 사용한다.
- 엔티티와 값 타입을 혼동해서 엔티티를 값 타입으로 만들면 안된다.
- 식별자가 필요하고, 지속해서 값을 추적, 변경해야 한다면 그것은 값 타입이 아닌 엔티티
