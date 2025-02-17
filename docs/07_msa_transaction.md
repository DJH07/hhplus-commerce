# 1. 서비스 확장에 따른 MSA 전환과 트랜잭션 관리

## 1-1. 개요
서비스의 규모가 확장됨에 따라 MSA(Microservices Architecture) 형태로 각 도메인별로 배포 단위를 분리해야 한다. 이에 따라 기존 모놀리식 아키텍처에서 보장되던 트랜잭션 관리가 어려워지는 문제가 발생한다. 이 문서에서는 MSA 환경에서의 트랜잭션 관리 한계를 분석하고, 해결 방안을 제시한다.

## 1-2. 트랜잭션의 한계

### 1-2-1. 분산 트랜잭션 관리의 어려움
MSA 환경에서는 데이터베이스가 서비스별로 분리되므로, 단일 데이터베이스에서 보장되던 ACID(Atomicity, Consistency, Isolation, Durability) 속성을 유지하기 어렵다. 따라서 다음과 같은 한계가 존재한다.

- 단일 트랜잭션 내에서 여러 서비스의 데이터 변경을 보장할 수 없음
- 네트워크 및 장애 발생 시 트랜잭션 롤백이 어려움
- 트랜잭션 처리 시간이 증가하여 성능 저하 발생 가능

### 1-2-2. 데이터 일관성 문제
MSA 환경에서는 각 서비스가 개별적으로 데이터를 관리하기 때문에 데이터 불일치 문제가 발생할 수 있다. 특히, 트랜잭션 도중 일부 서비스만 성공하고 일부는 실패하는 경우, 정합성을 보장하기 어렵다.

### 1-2-3. 트랜잭션 롤백 불가능
모놀리식 아키텍처에서는 트랜잭션이 실패할 경우 전체 작업을 롤백할 수 있었지만, MSA 환경에서는 각 서비스가 독립적으로 트랜잭션을 수행하므로 전체 트랜잭션 롤백이 어렵다.

---

# 2. MSA 환경에서의 트랜잭션 해결 방안

![image](https://github.com/user-attachments/assets/abb1882b-3393-4bef-9abc-aa9d63e7f01b)

## 2-1. Two-Phase Commit(2PC) 적용
### 2-1-1. Two-Phase Commit(2PC) 개념
Two-Phase Commit(2PC)은 분산 환경에서 트랜잭션을 일관되게 유지하기 위해 사용하는 방법이다. 두 개의 단계(Prepare, Commit)로 나누어 트랜잭션을 수행하며, 각 서비스가 트랜잭션을 커밋할 준비가 되었는지 확인한 후 최종적으로 커밋을 수행한다.

### 2-1-2. 2PC의 한계
- **네트워크 장애 발생 시 데이터 정합성 보장 어려움**
- **Locking 문제**: 모든 서비스가 트랜잭션 완료를 기다려야 하므로, 동시성이 낮아지고 성능이 저하됨
- **MSA 환경에서 비효율적**: 각 서비스가 독립적으로 운영되므로 2PC를 적용하기 어렵고, 서비스 간 결합도가 높아짐

## 2-2. SAGA 패턴 적용
SAGA 패턴은 MSA 환경에서 트랜잭션을 관리하기 위한 대표적인 패턴이다. 트랜잭션을 서비스 간의 독립적인 단계로 나누고, 실패 시 보상 트랜잭션을 수행하여 데이터 정합성을 유지한다.

### 2-2-1. SAGA 패턴의 종류

#### 2-2-1-1. Choreography 기반 SAGA
![image](https://github.com/user-attachments/assets/15b0322b-ff1f-4bbf-8886-ef261ba3d9ee)
![image](https://github.com/user-attachments/assets/9515d971-7c60-4e47-91c0-e85ef348e3da)

- 각 서비스가 이벤트를 발행하고, 다른 서비스가 이벤트를 구독하여 처리한다.
- 분산된 서비스 간에 별도의 중앙 관리 없이 트랜잭션을 진행할 수 있다.
- 서비스 간 결합도가 높아질 위험이 있다.

#### 2-2-1-2. Orchestration 기반 SAGA
![image](https://github.com/user-attachments/assets/8eb6e3f2-2373-4174-ac42-af3b9f247fcc)
![image](https://github.com/user-attachments/assets/6bcf3bb7-0242-4fa1-871b-582d619275c4)

- 중앙에서 트랜잭션을 관리하는 Orchestrator 서비스가 존재하며, 각 서비스에 작업을 요청한다.
- 중앙에서 전체 트랜잭션의 흐름을 제어하므로 서비스 간 결합도가 낮다.
- Orchestrator 서비스가 단일 장애점(Single Point of Failure)이 될 수 있다.

## 2-3. 이벤트 소싱(Event Sourcing) 적용
이벤트 소싱을 활용하면 서비스 간 데이터 정합성을 유지할 수 있다. 각 서비스에서 상태를 직접 변경하는 것이 아니라, 변경 이력을 이벤트로 저장하고 이를 기반으로 상태를 복구할 수 있다.

## 2-4. 보상 트랜잭션 적용
SAGA 패턴을 사용할 경우 트랜잭션 실패 시 보상 트랜잭션을 수행하여 데이터 정합성을 유지한다. 예를 들어, 결제 서비스에서 실패하면 잔액 차감 작업을 취소하는 보상 트랜잭션을 수행한다.

---

# 3. 이커머스 MSA 전환을 위한 서비스 설계

## 3-1. 기존 주문 및 결제 처리 로직
기존에는 단일 서비스에서 주문과 결제를 처리하였으며, 다음과 같은 방식으로 트랜잭션이 관리되었다.

```java
public Payment processPayment(Long orderId) {
    // 주문 상태 확인
    // 주문 상태 변경 (결제 진행중)
    // 사용자 잔액 조회
    // 잔액 차감
    // 결제 진행
    // 주문 상태 변경 (결제 완료)
}
```

## 3-2. MSA 기반 주문 및 결제 처리 로직 설계
MSA 환경에서는 주문, 사용자, 결제 서비스를 분리하고 SAGA 패턴을 적용하여 트랜잭션을 관리한다.

### 3-2-1. 서비스 분리

#### 3-2-1-1. 사용자 서비스
- 사용자 정보 조회
- 사용자 잔액 조회 및 차감 (보상 트랜잭션 활용)

#### 3-2-1-2. 주문 서비스
- 주문 생성 및 조회
- 주문 상태 변경

#### 3-2-1-3. 결제 서비스
- 결제 진행
- 결제 취소 (보상 트랜잭션 활용)

## 3-3. 주문 및 결제 처리 과정에서 SAGA 패턴 적용

1. 주문 서비스에서 주문 상태 변경 (진행 중)
2. 사용자 서비스에서 잔액 차감
3. 결제 서비스에서 결제 진행
4. 주문 서비스에서 주문 상태 변경 (완료)
5. 실패 시 보상 트랜잭션 수행 (잔액 복구, 결제 취소)

---

# 4. 실시간 주문 데이터 전달 및 기존 로직 개선

## 4-1. 실시간 데이터 전달 요구사항
이커머스 서비스에서 주문 정보를 실시간으로 데이터 플랫폼으로 전달해야 한다. 기존의 동기식 트랜잭션 로직과 분리하여 비동기 이벤트 기반으로 변경해야 한다.

## 4-2. 기존 주문 처리 로직 개선 방안

### 4-2-1. 기존 파사드 패턴 구조
기존 파사드 패턴에서는 주문과 결제가 하나의 트랜잭션으로 처리되었으며, 외부 시스템과의 연동이 고려되지 않았다.

### 4-2-2. 이벤트 기반 아키텍처 도입
기존 로직을 유지하면서 실시간 주문 데이터를 전송하기 위해 이벤트 발행을 추가한다.

- 이벤트 발행 (PaymentFacade에서 이벤트 발생)
  - PaymentFacade에서 결제가 성공하면 PaymentCompletedEvent를 발행한다.
  - 이를 통해 주문 서비스가 비동기적으로 결제 완료 이벤트를 처리할 수 있도록 한다.
  ```java

    @Transactional
    public PaymentStatus payment(Long orderId, Long userCouponId, boolean isFail) {
        OrderResult order = orderService.getOrderResult(orderId);
        Long payTotalAmount = couponService.applyUserCoupon(userCouponId, order.totalAmount());
        balanceService.reduceBalance(order.userId(), payTotalAmount);
        PaymentStatus paymentStatus = paymentService.processPayment(orderId, payTotalAmount, isFail);

        if (Objects.requireNonNull(paymentStatus).equals(PaymentStatus.SUCCESS)) {
            orderService.successOrder(orderId, payTotalAmount);
            eventPublisher.publishEvent(new PaymentCompletedEvent(orderId, payTotalAmount));
        } else if (paymentStatus.equals(PaymentStatus.FAILED)) {
            orderService.failOrder(orderId);
            productService.restoreProduct(order.itemResultList());
            throw new BusinessException(BusinessErrorCode.PAYMENT_FAILED);
        }
        return paymentStatus;
    }
  ```

- 이벤트 구독 (주문 서비스에서 이벤트 구독하여 주문 상태 업데이트)
    - PaymentEventListener가 @EventListener를 통해 PaymentCompletedEvent를 구독한다.
    - 해당 이벤트가 발생하면 orderService.successOrder()를 호출하여 주문 상태를 '결제 완료'로 변경한다.

  ```java
    @Component
    @RequiredArgsConstructor
    public class PaymentEventListener {
    
        private final OrderService orderService;
    
        @EventListener
        public void handlePaymentCompleted(PaymentCompletedEvent event) {
            orderService.successOrder(event.getOrderId(), event.getPayTotalAmount());
        }
    }
  ```


## 4-3. 기대 효과
- 주문 생성과 데이터 플랫폼 전송을 분리하여 기존 로직에 영향을 주지 않음
- 트랜잭션이 종료된 후 이벤트 발행하여 성능 향상
- 비동기 이벤트 기반으로 데이터 일관성 유지 가능

---

# 5. 결론
MSA 환경에서는 트랜잭션 관리가 어려워지므로, Two-Phase Commit과 SAGA 패턴을 비교하여 적절한 방식으로 적용해야 한다. 또한, 실시간 주문 데이터 전달을 위해 기존 로직을 이벤트 기반으로 개선함으로써 서비스 성능과 확장성을 확보할 수 있다.

---

# 참고
- [MSA : SAGA 패턴이란](https://azderica.github.io/01-architecture-msa/)
- [MSA 환경에서의 분산 트랜잭션 관리: 2PC & SAGA 패턴](https://velog.io/@ch200203/MSA-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C%EC%9D%98-%EB%B6%84%EC%82%B0-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EA%B4%80%EB%A6%AC2PC-SAGA-%ED%8C%A8%ED%84%B4)