# 영속성 관리

## 엔티티 매니저(`EntityManager`)

엔티티를 저장하고, 수정하고, 삭제하고, 조회하는 등 엔티티와 관련된 모든 일을 처리한다.
개발자 입장에서 엔티티 매니저는 엔티티를 저장하는 가상의 데이터베이스로 생각하면 된다.

## 엔티티 매니저 팩토리(`EntityManagerFactory`)

엔티티 매니저 팩토리는 엔티티 매니저를 만드는 공장인데, 비용 문제 때문에 **애플리케이션 전체에서 한 개만 만들어서 공유하는 구조로 설계**되어 있다. 반면, 공장에서 엔티티 매니저를 생성하는 비용은 거의 들지 않는다.

> **엔티티 매니저 팩토리는 여러 스레드가 동시에 접근해도 안전하므로 서로 다른 스레드 간에 공유해도 되지만, 엔티티 매니저는 여러 스레드가 동시에 접근하면 동시성 문제가 발생하므로 스레드 간에 절대 공유하면 안된다.**

<img width="800" alt="스크린샷 2022-01-20 오후 9 46 02" src="https://user-images.githubusercontent.com/60968342/150341339-5883d8fb-877e-4564-81fe-8a308020e75b.png">

엔티티 매니저는 DB 연결이 꼭 필요한 시점까지 커넥션을 얻지 않고, 보통 트랜잭션을 시작할 때 커넥션을 획득한다.

## 영속성 컨텍스트

- **JPA를 이해하는데 가장 중요한 용어**
- **"엔티티를 영구 저장하는 환경"**
- `EntityManager.persist(entity);`
- 위 코드는 엔티티 매니저를 사용해서 엔티티를 영속성 컨텍스트에 저장한다.
- 영속성 컨텍스트는 눈에 보이지 않는 논리적인 개념이다.

// 영속성: 기본적으로 컴퓨터 공학에서 영속성이라고 하면 비휘발성이라 보면 되는 개념

<img width="755" alt="스크린샷 2022-01-20 오후 10 07 28" src="https://user-images.githubusercontent.com/60968342/150344668-b30bbc4c-56d4-4f59-aa0f-305d5490b8a0.png">

## 엔티티의 생명주기

- **비영속(new/transient)**: 영속성 컨텍스트와 전혀 관계가 없는 **새로운** 상태
- **영속(managed)**: 영속성 컨텍스트에 **관리**되는 상태
- **준영속(detached)**: 영속성 컨텍스트에 저장되었다가 **분리**된 상태
- **삭제(removed)**: **삭제**된 상태

<img width="747" alt="스크린샷 2022-01-20 오후 10 15 31" src="https://user-images.githubusercontent.com/60968342/150345844-901cfae4-bbdb-4718-a5c9-c08ca547b2d6.png">

### 비영속

순수한 객체 상태이며 아직 저장하지 않았다. 따라서 영속성 컨텍스트나 데이터베이스와는 전혀 관련이 없는 상태이다.

```java
// 객체를 생성한 상태(비영속)
Member member = new Member();
member.setId("member1");
member.setUsername("회원1");
```

<img width="756" alt="스크린샷 2022-01-20 오후 10 20 35" src="https://user-images.githubusercontent.com/60968342/150346584-cffb1d94-9446-41ca-bd52-01ecaf9134e2.png">

### 영속

**영속성 컨텍스트가 관리하는 엔티티를 영속 상태**라고 한다.
// `em.find()`나 JPQL을 사용해서 조회한 엔티티도 영속성 컨텍스트가 관리하는 영속 상태임.

```java
EntityManagerFatory emf = Persistence.createEntityManagerFactory("db");
EntityManager em = emf.createEntityManager();
EntityTransaction tx = em.getTransaction();

// 엔티티 매니저에서 수행하는 모든 로직은 트랜잭선 안에서 수행돼야 한다.
tx.begin();

try {
    // 객체를 생성한 상태(비영속)
    Member member = new Member();
    member.setId("member1");
    member.setUsername("회원1");

    // 객체를 저장한 상태(영속), 쿼리는 전송되지 않음.
    em.persist(member);

    // 커밋하는 시점에 쿼리가 전송된다.
    tx.commit();

} catch (Exception e) {
    // 어떤 이유에서 오류가 났다면 트랜잭션을 롤백 시켜줘야한다.
    tx.rollback();
} finally {
    em.close();
}

emf.close();
```

<img width="497" alt="스크린샷 2022-01-20 오후 10 30 13" src="https://user-images.githubusercontent.com/60968342/150348128-bc16b07e-6375-4529-9383-45feb01dcfb5.png" style="margin-left: auto; margin-right: auto; display: block;">

### 준영속, 삭제

영속성 컨텍스트가 관리하던 영속 상태의 엔티티를 영속성 컨텍스트가 관리하지 않으면 준영속 상태가 된다.

`em.detach()`를 호출하면 준영속 상태가 된다. `em.close()`를 호출해서 영속성 컨텍스트를 닫거나 `em.clear()`를 호출해서 영속성 컨텍스트를 초기화해도 영속성 컨텍스트가 관리하던 영속 상태의 엔티티는 준영속 상태가 된다.

엔티티를 영속성 컨텍스트와 데이터베이스에서 삭제하면 삭제 상태가 된다.

```java
// 회원 엔티티를 영속성 컨텍스트에서 분리, 준영속 상태
em.detach(member);

// 객체를 상태한 상태(삭제)
em.remove(member);
```

## 영속성 컨텍스트의 특징

- 영속성 컨텍스트와 식별자 값
  영속성 컨텍스트는 엔티티를 식별자 값(`@Id`로 테이블의 기본 키와 매핑한 값)으로 구분한다. 따라서 **영속 상태는 식별자 값이 반드시 있어야 한다.** 식별자 값이 없으면 예외가 발생한다.
  // 기본 키 매핑 전략이 따로 있을 정도의 이유가 이렇기 때문이라 생각함.

- 영속성 컨텍스트와 데이터베이스 저장
  JPA는 보통 트랜잭션을 커밋하는 순간 영속성 컨텍스트에 새로 저장된 엔티티를 데이터베이스에 반영하는데 이것을 플러시(flush)라 한다.

## 영속성 컨텍스트의 이점

- 1차 캐시
- 동일성(identity) 보장
- 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
- 변경 감지(Dirty Checking)
- 지연 로딩(Lazy Loading)
