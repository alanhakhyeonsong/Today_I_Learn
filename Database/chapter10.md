# 회복과 병행 제어
## 1. 트랜잭션의 개념
하나의 작업을 수행하기 위해 필요한 데이터베이스 연산들을 모아놓은 것으로, 논리적인 작업의 단위다. 데이터베이스에 장애가 발생했을 때 데이터를 복구하는 작업의 단위도 된다. 트랜잭션의 모든 명령문이 완벽하게 처리되거나 하나도 처리되지 않아야 데이터베이스가 모순이 없는 일관된 상태를 유지할 수 있다. 일반적으로 데이터베이스를 변경하는 INSERT 문, DELETE 문, UPDATE 문의 실행을 트랜잭션으로 관리한다.

## 2. 트랜잭션의 특징(ACID 특성)
- 원자성: 트랜잭션의 연산이 모두 정상적으로 수행되거나 하나도 수행되지 않아야 한다.
- 일관성: 트랜잭션이 수행된 후에도 데이터베이스가 일관성 있는 상태여야 한다. 즉, 트랜잭션이 수행되기 전에 데이터베이스가 일관된 상태였다면 트랜잭션의 수행이 완료된 후 결과를 반영한 데이터베이스도 또 다른 일관된 상태가 되어야 한다는 의미다.
- 격리성: 수행 중인 트랜잭션이 완료될 때까지 다른 트랜잭션들이 중간 연산 결과에 접근할 수 없다. 일반적으로 데이터베이스 시스템에서는 여러 트랜잭션이 동시에 수행되지만 각 트랜잭션이 독립적으로 수행될 수 있도록 다른 트랜잭션의 중간 연산 결과에 서로 접근하지 못하게 한다.
- 지속성: 트랜잭션의 수행이 완료된 후에 데이터베이스에 반영한 결과는 영구적이어야 한다. 영속성이라고도 한다.

// DBMS의 기능 → 회복 기능: 원자성, 지속성 보장/ 병행 제어 기능: 일관성, 격리성 보장

## 3. 트랜잭션의 연산
- commit 연산: 작업 완료
<img src="./images/commit.jpg">

- rollback 연산: 작업 취소
<img src="./images/rollback.jpg">

## 4. 트랜잭션의 상태
- 활동 상태: 트랜잭션이 수행을 시작하여 현재 수행 중인 상태다.
- 부분 완료 상태: 트랜잭션의 마지막 연산이 실행을 끝낸 직후의 상태다. 모든 연산의 처리가 끝났지만 트랜잭션이 수행된 최종 결과를 데이터베이스에 아직 반영하지 않은 상태이다. 상황에 따라 완료/실패 상태가 될 수 있다.
- 완료 상태: 트랜잭션이 성공적으로 완료되어 commit 연산을 실행한 상태다. 트랜잭션이 수행한 최종 결과를 데이터베이스에 반영하고, 데이터베이스가 새로운 일관된 상태가 되면서 트랜잭션이 종료된다.
- 실패 상태: 장애가 발생하여 트랜잭션의 수행이 중단된 상태다.
- 철회 상태: 트랜잭션의 수행 실패로 rollback 연산을 실행한 상태다.

## 5. 장애의 정의와 유형
시스템이 제대로 동작하지 않는 상태다.

| 유형 | 의미 | 원인 |
|---|---|---|
| 트랜잭션 장애 | 트랜잭션 수행 중 오류가 발생하여 정상적으로 수행을 계속할 수 없는 상태 | 트랜잭션의 논리적 오류, 잘못된 데이터 입력, 시스템 자원의 과다 사용 요구, 처리 대상 데이터의 부재 등 |
| 시스템 장애 | 하드웨어의 결함으로 정상적으로 수행을 계속할 수 없는 상태 | 하드웨어 이상으로 메인 메모리에 저장된 정보가 손실되거나 교착 상태가 발생한 경우 등 |
| 미디어 장애 | 디스크 장치의 결함으로 디스크에 저장된 데이터베이스의 일부 혹은 전체가 손상된 상태 | 디스크 헤드의 손상이나 고장 등 |

## 6. 회복의 정의와 연산
장애가 발생했을 때 데이터베이스를 장애가 발생하기 전의 일관된 상태로 복구시키는 것이다. redo(재실행), undo(취소) 연산이 있다.
- redo: 가장 최근에 저장한 데이터베이스 복사본을 가져온 후 로그를 이용해 복사본이 만들어진 이후에 실행된 모든 변경 연산을 재실행하여 장애가 발생하기 직전의 데이터베이스 상태로 복구(전반적으로 손상된 경우에 주로 사용)
- undo: 로그를 이용해 지금까지 실행된 모든 변경 연산을 취소하여 데이터베이스를 원래의 상태로 복구(변경 중이었거나 이미 변경된 내용만 신뢰성을 잃은 경우에 주로 사용)

📌 데이터베이스 회복을 위해 복사본을 만드는 방법
| 종류 | 의미 |
|---|---|
| 덤프(dump) | 데이터베이스 전체를 다른 저장 장치에 주기적으로 복사하는 방법 |
| 로그(log) | 데이터베이스에서 변경 연산이 실행될 때마다 데이터를 변경하기 이전 값과 변경한 이후의 값을 별도의 파일에 기록하는 방법 |

📌 로그 레코드의 종류
<img src="./images/logrecode.jpg">

## 7. 회복 기법의 유형
- 로그 회복 기법: 로그를 이용한 회복이다.
- 검사 시점 회복 기법: 로그 회복 기법과 같은 방법으로 로그 기록을 이용하되, 일정 시간 간격으로 검사 시점 만들고 이를 이용한 회복이다.
- 미디어 회복 기법: 데이터베이스 덤프(복사본)를 이용한 회복이다.

## 8. 로그 회복 기법
로그를 이용한 회복 기법으로, 즉시 갱신 회복과 지연 갱신 회복이 있다. 로그 전체를 대상으로 회복 기법을 적용하면 데이터베이스 회복에 너무 많은 시간이 걸리고 redo 연산을 수행할 필요가 없는 트랜잭션에도 redo 연산을 실행하는 일이 발생되는 비효율성이 내재되어 있다.
- 즉시 갱신 회복: 트랜잭션을 수행하는 도중에 데이터 변경 연산의 결과를 데이터베이스에 즉시 반영한다.     
// 트랜잭션이 완료되기 전 장애가 발생한 경우 undo 연산을, 트랜잭션이 완료된 후 장애가 발생한 경우 redo 연산을 실행한다.
- 지연 갱신 회복: 트랜잭션이 부분 완료되면 데이터 변경 연산의 결과를 데이터베이스에 한번에 반영한다.       
// 트랜잭션이 완료되기 전에 장애가 발생한 경우 로그 내용을 무시하고 버리고 트랜잭션이 완료된 후에 장애가 발생한 경우 redo 연산을 실행한다. undo 연산이 필요 없고 redo 연산만 필요하므로 로그 레코드에 변경 이전 값을 기록할 필요가 없다.

## 9. 병행 수행과 병행 제어
### 병행 수행: 여러 개의 트랜잭션을 동시에 수행하는 것으로, 실제로 여러 트랜잭션이 차례로 번갈아 수행되는 인터리빙(interleaving) 방식으로 진행된다.
병행 수행을 특별한 제어 없이 진행하면 여러 문제가 발생할 수 있다.
- 모순성(inconsistency): 하나의 트랜잭션이 여러 개의 데이터 변경 연산을 실행할 때 일관성 없는 상태의 데이터베이스에서 데이터를 가져와 연산을 실행함으로써 모순된 결과가 발생하는 것.
- 갱신 분실(lost update): 하나의 트랜잭션이 수행한 데이터 변경 연산의 결과를 다른 트랜잭션이 덮어써 변경 연산이 무효화되는 것.
- 연쇄 복귀(cascading rollback): 트랜잭션이 완료되기 전에 장애가 발생하여 rollback 연산을 수행하면, 이 트랜잭션이 장애 발생 전에 변경한 데이터를 가져가 변경 연산을 실행한 또 다른 트랜잭션에도 rollback 연산을 연쇄적으로 실행해야 한다는 것이다. 그런데 장애가 발생한 트랜잭션이 rollback 연산을 실행하기 전에, 변경한 데이터를 가져가 사용한 다른 트랜잭션이 수행을 완료해버리면 rollback 연산을 실행할 수 없어 큰 문제가 발생하게 된다.

### 병행 제어: 병행 수행 시 문제가 발생하지 않고 정확한 결과를 얻을 수 있도록 트랜잭션의 수행을 제어하는 것이다. 동시성 제어라고도 한다.

## 10. 트랜잭션의 스케줄
트랜잭션에 포함된 연산들을 실행하는 순서를 의미한다. 직렬 스케줄, 비직렬 스케줄, 직렬 가능 스케줄이 있다.
### 직렬 스케줄: 인터리빙 방식을 이용하지 않고 트랜잭션별로 연산들을 순차적으로 실행시키는 것
모든 트랜잭션이 완료될 때까지 다른 트랜잭션의 방해를 받지 않고 독립적으로 수행된다. 그래서 직렬 스케줄에 따라 트랜잭션이 수행되고 나면 항상 모순이 없는 정확한 결과를 얻는다. 같은 트랜잭션들을 대상으로 하더라도 트랜잭션의 수행 순서에 따라 다양한 직렬 스케줄이 만들어질 수 있고, 직렬 스케줄마다 데이터베이스에 반영되는 최종 결과가 달라질 수 있다. 인터리빙 방식을 사용하지 않고 독립적으로 수행하기 때문에 병행 수행이라고 할 수 없고, 일반적으로 잘 사용하지 않는다.

### 비직렬 스케줄: 인터리빙 방식을 이용하여 트랜잭션들을 병행해서 수행시키는 것
트랜잭션이 돌아가면서 연산들을 실행하기 때문에 하나의 트랜잭션이 완료되기 전에 다른 트랜잭션의 연산이 실행될 수 있다. 이 방법에 따라 여러 트랜잭션을 병행 수행하면 갱신 분실, 모순성, 연쇄 복귀 등의 문제가 발생할 수 있어 최종 수행 결과의 정확성을 보장할 수 없다.

### 직렬 가능 스케줄: 직렬 스케줄과 동일한 정확한 결과를 생성하는 비직렬 스케줄
모든 비직렬 스케줄이 직렬 가능한 것은 아니다. 비직렬 스케줄 중에서 수행 결과가 동일한 직렬 스케줄이 없는 것들은 결과의 정확성을 보장할 수 없으므로 직렬 가능 스케줄이 아니다. 직렬 가능 스케줄은 직렬 스케줄과는 다르다. 대부분의 데이터베이스 관리 시스템에서는 직렬 가능 스케줄인지를 검사하기보다는 직렬 가능성을 보장하는 병행 제어 기법을 사용한다.

## 11. 병행 제어 기법
병행 수행하면서도 정확한 결과를 얻을 수 있는 직렬 가능성을 보장하는 것이다. 모든 트랜잭션들이 준수하면 직렬 가능성이 보장되는 규약을 정의하고 트랜잭션들이 이 규약을 따르도록 한다. 대표적으로 로킹 기법이 있다.

## 12. 로킹 기법
병행 수행되는 트랜잭션들이 동일한 데이터에 동시에 접근하지 못하도록 lock과 unlock 연산으로 제어한다. 기본 원리는 한 트랜잭션이 먼저 접근한 데이터에 대한 연산을 모두 마칠 때까지, 해당 데이터에 다른 트랜잭션이 접근하지 못하도록 상호 배재(mutual exclusion)하여 직렬 가능성을 보장하는 것이다(멀티스레딩 프로그래밍 기법과 동일한 원리라고 이해하면 쉽다). 로킹 단위가 커질수록 병행성이 낮아지지만 제어가 쉽고, 로킹 단위가 작아질수록 제어가 어렵지만 병행성이 높아진다.
- lock 연산: 트랜잭션이 데이터에 대한 독점권을 요청하는 연산. 데이터를 단순히 읽어오기만 하는 read 연산의 경우, 다른 트랜잭션이 같은 데이터에 동시에 read 연산을 실행해도 문제가 생기지는 않는다. 따라서 하나의 데이터에 read 연산을 동시에 실행할 수 있도록 하여 처리 효율성을 높일 필요가 있다.    
<img src="./images/lock연산.jpg">
<img src="./images/lock연산2.jpg">
- unlock 연산: 트랜잭션이 데이터에 대한 독점권을 반환하는 연산

## 13. 2단계 로킹 규약
트랜잭션이 lock과 unlock 연산을 확장 단계와 축소 단계로 나누어 수행해야 한다. 모든 트랜잭션이 2단계 로킹 규약을 준수하면 해당 스케줄은 직렬 가능성을 보장받는다. 하지만 교착상태(deadlock)가 발생할 수 있어 이에 대한 해결책이 필요하다.
- 확장 단계: 트랜잭션이 lock 연산만 실행할 수 있고, unlock 연산은 실행할 수 없는 단계
- 축소 단계: 트랜잭션이 unlock 연산만 실행할 수 있고, lock 연산은 실행할 수 없는 단계