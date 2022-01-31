# 고급 매핑

## 상속관계 매핑

- 관계형 데이터베이스에는 상속관계가 없다.
- 슈퍼타입 서브타입 관계라는 모델링 기법이 객체의 상속 개념과 가장 유사하다.
- 상속관계 매핑: 객체의 상속 구조와 데이터베이스의 슈퍼타입 서브타입 관계를 매핑하는 것이다.

![](https://images.velog.io/images/songs4805/post/4c483279-cb39-44a3-b722-8541be4ca97a/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-30%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.27.27.png)

Album, Movie, Book은 모두 공통적으로 Item 테이블의 컬럼들을 가지고 있다.

// 따로 읽어볼 내용: [슈퍼타입 서브타입?](https://eehoeskrap.tistory.com/57)

슈퍼타입 서브타입 논리 모델을 실제 물리 모델로 구현하는 방법은 3가지가 있다.

- 각각 테이블로 변환: 조인전략
- 통합 테이블로 변환: 단일 테이블 전략
- 서브타입 테이블로 변환: 구현 클래스마다 테이블 전략

## 조인 전략

![](https://images.velog.io/images/songs4805/post/575834c6-ecc7-46ad-a970-9f5a38678b3c/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-30%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011.48.22.png)

### 엔티티 매핑

```java
// Item.java
@Entity
public class Item {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;
    // getter, setter
}

// Album.java
@Entity
public class Album extends Item {
    private String artist;
    // getter, setter
}

// Movie.java
@Entity
public class Movie extends Item {
    private String director;
    private String actor;
    // getter, setter
}

// Book.java
@Entity
public class Book extends Item {
    private String author;
    private String isbn;
    // getter, setter
}
```

이를 그냥 실행 시 결과는 다음과 같이 단일 테이블로 create 된다.
![](https://images.velog.io/images/songs4805/post/2929c492-990e-41b5-812b-82c324b35dc1/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2012.01.03.png)
![](https://images.velog.io/images/songs4805/post/1861bb0f-eec4-48e0-b6c0-3bcafa3c0391/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2012.01.16.png)

### 조인 전략 매핑

`@Inheritance`, `@DiscriminatorColumn`을 Item 엔티티에 추가하여 실행하면 아래와 같이 조인 전략으로 매핑할 수 있다.

```java
// Item.java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
public class Item {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;
    // getter, setter
}

// Album.java
@Entity
public class Album extends Item {
    private String artist;
    // getter, setter
}

// Movie.java
@Entity
public class Movie extends Item {
    private String director;
    private String actor;
    // getter, setter
}

// Book.java
@Entity
public class Book extends Item {
    private String author;
    private String isbn;
    // getter, setter
}
```

![](https://images.velog.io/images/songs4805/post/b53e7a64-1d89-4b3a-b4d6-ab92a0a97908/InheritanceType.JOINED.png)

![](https://images.velog.io/images/songs4805/post/5223a3be-18e4-4329-bcc5-cb724e6968e2/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2010.25.28.png)

### insert 실행

```java
import domain.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello3");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Movie movie = new Movie();
            movie.setDirector("Christopher Nolan");
            movie.setActor("Heath Ledger");
            movie.setName("Dark Night");
            movie.setPrice(10000);

            em.persist(movie);

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
```

위와 같이 값을 넣어보면 Item과 Movie에 각각 한 번씩 insert query가 실행된다.

![](https://images.velog.io/images/songs4805/post/5fdf871d-1134-4506-84bd-8ee7a57d9687/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2010.32.01.png)

![](https://images.velog.io/images/songs4805/post/fefcc7c6-98c8-4b3b-9471-47887a5865f1/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2010.41.25.png)

### 주요 어노테이션

- `@Inheritance(strategy=InheritanceType.XXX)`: 부모 클래스에 상속 매핑 지정
  - **JOINED**: 조인 전략
  - **SINGLE_TABLE**: 단일 테이블 전략
  - **TABLE_PER_CLASS**: 구현 클래스마다 테이블 전략
- `@DiscriminatorColumn(name="DTYPE")`: 부모 클래스에 구분 컬럼 지정. 저장된 자식 테이블을 구분할 수 있다.
- `@DiscriminatorValue("XXX")`: 엔티티를 저장할 때 구분 컬럼에 입력할 값을 지정.

기본 값으로 자식 테이블은 부모 테이블의 ID 컬럼명을 그대로 사용하는데, 만약 자식 테이블의 기본 키 컬럼명을 변경하고 싶다면, `@PrimaryKeyJoinColumn` 애노테이션을 사용하면 된다.

조인 전략은 다음과 같은 특징이 있다.

- 장점
  - 테이블이 정규화된다.
  - 외래 키 참조 무결성 제약조건 활용 가능
  - 저장공간 효율화
- 단점
  - 조회시 조인이 많이 사용되어 성능이 저하될 수 있다.
  - 조회 쿼리가 복잡함
  - 데이터 저장시 INSERT SQL 2번 호출

## 단일 테이블 전략

![](https://images.velog.io/images/songs4805/post/a5a6ada7-eca0-4037-884d-37bc98c76fc2/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2011.20.51.png)

`@Inheritance(strategy = InheritanceType.SINGLE_TABLE)`로 지정하면 된다.

실행 결과는 다음과 같다.

![](https://images.velog.io/images/songs4805/post/61281c89-e578-4d83-89e5-7f9a6df69c43/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2011.23.15.png)

![](https://images.velog.io/images/songs4805/post/6650d44a-b736-4190-9e27-cd5cad4a9e6d/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2011.25.14.png)

테이블은 Item 테이블 하나로(단일 테이블)로 매핑되고, insert query가 1번 만 실행된다. 또한 `@DiscriminatorColumn`을 기입하지 않아도 자동으로 들어간다.

정리하자면,

- 장점
  - 조인이 필요 없어 일반적으로 조회 성능이 빠름.
  - 조회 쿼리가 단순함.
- 단점
  - 자식 엔티티가 매핑한 컬럼은 모두 null 허용
  - 단일 테이블에 모든 것을 저장하므로 테이블이 커질 수 있다. 상황에 따라서 조회 성능이 오히려 느려질 수 있음.(임계점을 넘을 정도의 상황은 거의 오지 않는다.)

## 구현 클래스마다 테이블 전략

![](https://images.velog.io/images/songs4805/post/d2d13675-d84e-4ecf-8ebf-815f253012f9/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2011.31.04.png)
자식 엔티티마다 테이블을 만드는 방식이다.

```java
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;

    // getter, setter
}

@Entity
public class Album extends Item { ... }

@Entity
public class Moive extends Item { ... }

@Entity
public class Book extends Item { ... }
```

![](https://images.velog.io/images/songs4805/post/b66d8d12-d527-4a8e-b6cd-077b98fbdb50/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2011.33.35.png)

![](https://images.velog.io/images/songs4805/post/83af42bb-26bf-4154-bb4a-bf916ff4a449/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2011.33.50.png)

Item 테이블을 생성하지 않고, Album, Book, Movie 테이블에서 각각 id, name, price 필드를 가지고 있다. Item을 조회하면 Album, Book, Movie 세 개의 테이블을 `UNION ALL`로 전부 조회해서 가져온다.

```log
Hibernate:
    select
        item0_.ITEM_ID as ITEM_ID1_2_0_,
        item0_.name as name2_2_0_,
        item0_.price as price3_2_0_,
        item0_.artist as artist1_0_0_,
        item0_.actor as actor1_3_0_,
        item0_.director as director2_3_0_,
        item0_.author as author1_1_0_,
        item0_.isbn as isbn2_1_0_,
        item0_.clazz_ as clazz_0_
    from
        ( select
            ITEM_ID,
            name,
            price,
            artist,
            null as actor,
            null as director,
            null as author,
            null as isbn,
            1 as clazz_
        from
            Album
        union
        all select
            ITEM_ID,
            name,
            price,
            null as artist,
            actor,
            director,
            null as author,
            null as isbn,
            2 as clazz_
        from
            Movie
        union
        all select
            ITEM_ID,
            name,
            price,
            null as artist,
            null as actor,
            null as director,
            author,
            isbn,
            3 as clazz_
        from
            Book
    ) item0_
where
    item0_.ITEM_ID=?
item = domain.Movie@2370ac7a
1월 31, 2022 11:45:57 오전 org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl stop
INFO: HHH10001008: Cleaning up connection pool [jdbc:h2:tcp://localhost/~/test3]
```

**이 전략은 데이터베이스 설계자와 ORM 전문가 둘 다 추천하지 않는 방식이다.(쓰면 안됨)**
// 조인 전략(정석)이나 단일 테이블 전략(데이터가 많지 않고 단순할 때)을 고려하자.

- 장점
  - 서브 타입을 명확하게 구분해서 처리할 때 효과적
  - not null 제약조건 사용 가능
- 단점
  - 여러 자식 테이블을 함께 조회할 때 성능이 느림(UNION SQL 필요함)
  - 자식 테이블을 통합해서 쿼리하기 힘들다.

## @MappedSuperclass

테이블과 관계 없고, 단순히 엔티티가 공통 매핑 정보가 필요할 때 사용한다.(id, name) 주로 등록일, 수정일, 등록자, 수정자 같은 전체 엔티티에서 공통으로 적용하는 정보를 모을 때 사용한다.
// ex) 모든 테이블에 row 생성일, 수정일을 등록해야 하는 경우(createdAt, updatedAt)

![](https://images.velog.io/images/songs4805/post/89243a22-8d7d-46da-bbe6-f7366848d84d/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202022-01-31%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%2011.48.04.png)

```java
@MappedSuperclass
public abstract class BaseEntity {

    @Id @GeneratedValue
    private Long id;
    private String name;
    ...
}

@Entity
public class Member extends BaseEntity {

    // ID 상속
    // Name 상속
    private String email;
    ...
}

@Entity
public class Seller extends BaseEntity {

    // ID 상속
    // Name 상속
    private String shopName;
    ...
}
```

- 상속관계 매핑이 아니다.
- 엔티티도 아니고, 테이블과 매핑되지도 않는다.
- 부모 클래스를 상속 받는 **자식 클래스에 매핑 정보만 제공**
- 부모 타입으로 조회, 검색 불가 (`em.find(BaseEntity))` 불가)
- 직접 생성해서 사용할 일이 없으므로 **추상 클래스 권장**

// 참고: `@Entity` 클래스는 `@MappedSuperclass`로 지정한 클래스만 상속 가능함.
