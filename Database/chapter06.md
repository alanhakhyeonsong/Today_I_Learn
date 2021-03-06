# 관계 데이터 연산
## 1. 데이터 모델?
> 데이터 모델 = 데이터 구조 + 제약조건 + 연산

## 2. 관계 데이터 연산
원하는 데이터를 얻기 위해 릴레이션에 필요한 처리를 요구하는 것. 관계 대수와 관계 해석이 있는데, 이 둘은 기능과 표현력 모두에서 능력이 동등하다.   
📌 데이터 언어 유용성 검증의 기준 역할을 함.
> - 관계 대수: 원하는 결과를 얻기 위해 데이터의 처리 과정을 순서대로 기술하는 절차 언어
> - 관계 해석: 원하는 결과를 얻기 위해 처리를 원하는 데이터가 무엇인지만 기술하는 비절차 언어

## 3. 관계 대수
원하는 결과를 얻기 위해 데이터의 처리 과정을 순서대로 기술한 언어로, 다시 말해 릴레이션을 처리하는 연산자들의 모임이다.
> - 수학의 집합 관련 연산자를 차용함.
> - 관계 대수의 피연산자인 릴레이션에 연산자를 적용한 결과도 릴레이션이라는 폐쇄 특성이 있음.

## 4. 일반 집합 연산자
릴레이션이 튜플의 집합이라는 개념을 이용하는 연산자. 두 릴레이션을 대상으로 연산을 수행함. 합집합, 교집합, 차집합의 경우 피연산자인 두 릴레이션이 합병 가능해야 연산을 수행할 수 있다.     
📌 합병 가능의 조건?
1. 두 릴레이션의 차수가 같다. 즉, 두 릴레이션은 속성 개수가 같다.
2. 2개의 릴레이션에서 서로 대응되는 속성의 도메인이 같다. 단, 도메인이 같으면 속성의 이름은 달라도 됨.
<img src="https://user-images.githubusercontent.com/60968342/130900169-ede77577-883f-4519-b638-caead087f205.jpg">

> - 합집합(⋃): 두 릴레이션의 합집합 튜플을 구한다.
> - 교집합(⋂): 두 릴레이션의 교집합 튜플을 구한다.
> - 차집합(-): 두 릴레이션의 차집합 튜플을 구한다.
> - 카티션 프로덕트(×): 두 릴레이션의 모든 튜플을 각각 연결하여 생성된 튜플을 구한다.
> <img src="https://user-images.githubusercontent.com/60968342/130900178-b943e6b7-e828-489c-b990-31e1333eb52e.jpg">

### 1) 합집합
> <img src="https://user-images.githubusercontent.com/60968342/130900185-4368e1f1-9902-47ef-bad9-b783627d3a9d.jpg">

> - 두 릴레이션에 모두 존재하는 튜플은 합집합 연산의 결과 릴레이션에선 중복되지 않고 한 번만 나타난다.

### 2) 교집합
> <img src="https://user-images.githubusercontent.com/60968342/130900193-c48a3cbe-d308-428e-affe-b4603f1a70bf.jpg">

> - 결과 릴레이션의 차수는 피연산자인 릴레이션들의 차수와 같으며, 카디널리티는 피연산자의 것보다 크지 않다.
> - 수학의 집합연산과 같이 교환법칙, 결합법칙이 성립한다.

### 3) 차집합
> <img src="https://user-images.githubusercontent.com/60968342/130900201-ccd9bd53-adc2-40c0-af2d-2f169a1690be.jpg">

> - R에는 존재하지만 S에는 존재하지 않는 튜플들로 결과 릴레이션을 구성함.
> - 교환법칙, 결합법칙이 성립하지 않음.

### 4) 카티션 프로덕트
> <img src="https://user-images.githubusercontent.com/60968342/130900218-f9717672-1b88-4422-9015-2c4ab2ef74f7.jpg">

> - 릴레이션 R에 속한 각 튜플과 릴레이션 S에 속한 각 튜플을 모두 연결하여 만들어진 새로운 튜플로 결과 릴레이션을 구성함.
> - 두 릴레이션이 합병 불가능한 경우에도 해당 연산은 가능함.
> - 결과 릴레이션의 속성은 어느 소속인지를 나타내기 위해 '릴레이션이름.속성이름' 형식으로 표기됨.
> - 카티션 프로덕트 연산의 결과 릴레이션 차수는 피연산자들의 차수를 더한 것과 같다.
> - 카디널리티는 릴레이션 R과 S의 카디널리티를 곱한 것과 같다.
> - 교환법칙과 결합법칙이 성립.

## 5. 순수 관계 연산자
릴레이션의 구조와 특성을 이용하는 연산자.
> - 셀렉트(σ): 릴레이션에서 조건을 만족하는 튜플을 구한다.
> - 프로젝트(π): 릴레이션에서 주어진 속성들의 값으로만 구성된 튜플을 구한다.
> - 조인(⋈): 공통 속성을 이용해 두 릴레이션의 튜플들을 연결하여 생성된 튜플을 구한다.
> - 디비전(÷): 나누어지는 릴레이션에서 나누는 릴레이션의 모든 튜플과 관련이 있는 튜플을 구한다.
> <img src="https://user-images.githubusercontent.com/60968342/130900228-85656894-a5f5-40d3-afe8-fd07c9938198.jpg">

### 1) 셀렉트
```sql
릴레이션 where 조건식
```
> <img src="https://user-images.githubusercontent.com/60968342/130900238-2633e1ca-d9c2-43c0-a733-f414f4cbc172.jpg">

> - 조건식은 비교 연산자를 이용해 구성함.
> - 조건식을 속성과 상수의 비교로 구성할 때는 상수의 데이터 타입이 속성의 도메인과 일치해야 함.
> - 조건식을 다른 속성들 간의 비교로 구성할 때는 속성들의 도메인이 같아야 비교가 가능함.
> - 수평적 부분집합을 생성하게 됨.

### 2) 프로젝트
```sql
릴레이션[속성리스트]
```
> <img src="https://user-images.githubusercontent.com/60968342/130900255-f352e2f3-2b45-481a-a3be-480bf10e5726.jpg">

> - 수직적 부분집합을 생성하는 것과 같음.
> - 릴레이션의 모든 속성 중 일부분만 선택함.

### 3) 조인
> <img src="https://user-images.githubusercontent.com/60968342/130900286-813260b8-4824-49c4-905c-7ef819c560e5.jpg">

> - 조인 속성: 두 릴레이션이 공통으로 가지고 있는 속성. 두 릴레이션이 관계가 있음을 나타냄.
> - 조인 연산의 결과 릴레이션에서 조인 속성은 중복되지 않고 한 번만 표현됨.

### 4) 디비전
> <img src="https://user-images.githubusercontent.com/60968342/130900297-9e6ce581-0925-441c-8ae6-e65427d41530.jpg">

> - 릴레이션 S의 모든 속성과 도메인이 같은 속성을 릴레이션 R이 포함하고 있어야 함.

## 6. 확장된 관계 대수 연산자
> - 세미 조인(⋉): 조인 속성으로 프로젝트한 릴레이션을 이용해 조인한다.
> - 외부 조인(⨝+): 결과 릴레이션에 자연 조인 연산에서 제외되었던 모든 튜플을 포함시킨다.

## 7. 관계 해석
원하는 결과를 얻기 위해 처리를 원하는 데이터가 무엇인지만 기술하는 언어.
> - 수학의 프레디킷 해석에 기반을 둠
> - 튜플 관계 해석과 도메인 관계 해석으로 분류됨.