# 1. 배경

- 현재 운영 데이터가 없고, 테스트 컨테이너로 테스트 중인 상태.
- MySQL 8버전 이상에서 진행.
- 운영 환경에서 발생할 수 있는 조회 성능 문제를 사전에 분석하고 최적화 방안을 마련하여 개선하는 것이 목표.

# 2. 문제 정의

## 2-1. 주요 성능 문제 분석

데이터베이스에서 특정 조회 패턴이 비효율적인 실행 계획을 초래할 경우, 성능 저하가 발생할 수 있다. 대표적인 성능 저하 원인으로 **Full Table Scan, Using Temporary, Using Filesort** 등이 있다. 이러한 문제는 경우에 따라 유용할 수도 있지만, 특정 상황에서는 성능을 크게 저하시킬 수 있다.

### 2-1-1. Full Table Scan (테이블 전체 검색)

- **개념**
    - 테이블의 모든 행을 읽어야 하는 방식으로, 인덱스를 사용하지 않는 경우 발생한다.
    - 데이터 양이 많을수록 성능이 급격히 저하된다.

- **발생 조건**
1. WHERE 조건에 인덱스가 없는 컬럼을 사용한 경우
    - `name` 컬럼에 인덱스가 없으면, MySQL은 모든 행을 조회해야 하므로 **Full Table Scan 발생**

    ```sql
    SELECT * FROM product WHERE description = '최신형 스마트폰';
    ```

   ![image](https://github.com/user-attachments/assets/5f23171f-8797-44b4-9a1d-82e9d37046af)

    - **결과 해석**
        - `type = ALL` → **Full Table Scan 발생**
        - `possible_keys = NULL` → 사용할 수 있는 **인덱스 없다.**
        - `key = NULL` → **인덱스 미사용**
        - `rows = 10094` → **모든 행을 검색**해야 한다.
    - 해결 방법 :  **인덱스를 생성**하면 성능 개선 가능

    ```sql
    CREATE INDEX idx_description ON product(description);
    ```

2. LIKE 검색 시 와일드카드를 앞에 붙인 경우
    - `%gmail.com`처럼 앞에 `%`가 붙으면 인덱스를 사용할 수 없고, **전체 행을 검사해야 한다.**

    ```sql
    SELECT * FROM user_info WHERE user_name LIKE '%철수';
    ```

   ![image (2)](https://github.com/user-attachments/assets/dbaffc33-496e-475c-950f-c096eff9921a)

    - **결과 해석**
        - `type = ALL` → **Full Table Scan 발생**
        - `possible_keys = NULL` → 사용할 수 있는 **인덱스 없다.**
        - `key = NULL` → **인덱스 미사용**
        - `rows = 10174` → **모든 행을 검색**해야 한다.
    - 해결 방법
        - 와일드카드 위치를 뒤쪽으로 변경 (`'철수%'`) → **인덱스 사용 가능**
        - `FULLTEXT INDEX`를 활용 (`MATCH() AGAINST()` 사용)

        ```sql
        ALTER TABLE user_info ADD FULLTEXT(user_name);
        SELECT * FROM user_info WHERE MATCH(user_name) AGAINST ('철수');
        ```


3. OR 조건이 인덱스를 용하지 못하는 경우
    - `status` 또는 `order_total_amount` 컬럼 중 **하나만 인덱스를 사용**할 수 있는 경우 → MySQL이 인덱스를 활용하지 못하고 **Full Table Scan 발생**

    ```sql
    SELECT * FROM order_info WHERE status = 'PENDING' OR order_total_amount > 50000;
    ```


   ![image (3)](https://github.com/user-attachments/assets/b1b354d1-d2f2-4fcd-9d6b-93e9c957a097)

    - 해결 방법: `UNION ALL` 또는 `인덱스 최적화`

    ```sql
    (SELECT * FROM order_info WHERE status = 'PENDING')
    UNION ALL
    (SELECT * FROM order_info WHERE order_total_amount > 50000);
    ```

4. 함수를 사용한 WHERE 조건
    - `DATE(created_at)`을 사용하면 **인덱스가 무효화**되어 전체 행을 검색해야 한다. → **Full Table Scan 발생**

    ```sql
    SELECT * FROM balance WHERE DATE(created_at) = '2024-02-10';
    ```

   ![image (4)](https://github.com/user-attachments/assets/bb3113db-222f-4da8-abc7-0e87914d535d)

    - **해결 방법**: 범위를 활용하여 직접 비교

    ```sql
    SELECT * FROM balance 
    WHERE created_at >= '2024-02-10 00:00:00' 
    AND created_at < '2024-02-11 00:00:00';
    ```


1. **데이터 타입이 맞지 않아 인덱스를 사용하지 못하는 경우**
    - `user_id` 컬럼이 `BIGINT`인데 문자열 `'123'`로 비교하면 **암시적 형 변환**이 발생하여 인덱스를 사용하지 못함

    ```sql
    SELECT * FROM user_coupon WHERE user_id = '123';
    ```

    - **해결 방법**: `INT` 타입으로 조회

    ```sql
    SELECT * FROM user_coupon WHERE user_id = 123;
    ```


1. **ORDER BY와 LIMIT이 인덱스를 활용하지 못하는 경우**
    - `ORDER BY` 대상 컬럼이 **인덱스가 없거나, 적절한 순서대로 정렬되지 않는 경우** → **Full Table Scan 발생**

    ```sql
    SELECT * FROM product ORDER BY description LIMIT 10;
    ```

    - **해결 방법**: 정렬에 필요한 **인덱스 추가**

    ```sql
    CREATE INDEX idx_product_description ON product(description);
    ```


1. **조인(Join) 시 인덱스가 없을 경우**
    - `user_info.user_name`에 인덱스가 없으면 **user_info 테이블에서 Full Table Scan 발생**

    ```sql
    SELECT * FROM user_info u
    JOIN order_info o ON u.user_id = o.user_id
    WHERE u.user_name = '홍길동';
    ```

    - **해결 방법**: `user_name`에 **인덱스 추가**

    ```sql
    CREATE INDEX idx_user_name ON user_info(user_name);
    ```


- **유용할 수 있는 경우**
    - 테이블 크기가 작을 때
    - 대부분의 데이터를 조회할 때

- **문제되는 경우**
    - 데이터가 많을수록 성능 저하가 심각해짐
    - 불필요한 I/O 부하 발생으로 인해 쿼리 실행 시간이 길어지고, DB 서버 리소스를 낭비


---

### 2-1-2. Using Temporary (임시 테이블 사용)

- **개념**
    - `GROUP BY`, `ORDER BY`, `DISTINCT` 등의 연산이 수행될 때, 임시 테이블을 생성하여 데이터를 저장한 후 정렬 또는 그룹화하는 방식
    - 메모리 또는 디스크에 임시 테이블을 생성하여 연산을 수행하므로, 쿼리 성능이 저하될 가능성이 높다.
    - `EXPLAIN` 실행 시 **Using temporary**가 표시되면 **임시 테이블이 사용된다는 의미**이다.

- **발생 조건**
1. `GROUP BY`가 인덱스를 사용하지 못하는 경우

    ```sql
    SELECT user_id, COUNT(*)
    FROM order_info
    GROUP BY user_id;
    ```

   ![image (5)](https://github.com/user-attachments/assets/5af1227d-f593-40b3-b8f6-d089d3f5be9b)

    - **결과 해석**
        - `type = ALL` → **Full Table Scan 발생**
        - `possible_keys = NULL` → **사용할 수 있는 인덱스 없다**
        - `Extra = Using temporary` → **임시 테이블 생성됨**
    - **해결 방법**: `category_id`에 인덱스 추가

    ```sql
    CREATE INDEX idx_order_user_id ON order_info(user_id);
    ```

2. `ORDER BY`와 `GROUP BY`가 다른 컬럼을 사용할 때
    - `GROUP BY`와 `ORDER BY`가 다르면 **MySQL은 별도의 정렬을 수행해야 하므로 Using Temporary 발생 가능**

    ```sql
    SELECT user_id, COUNT(*), MAX(created_at) AS latest_order_date
    FROM order_info
    GROUP BY user_id
    ORDER BY latest_order_date DESC;
    ```

   ![image (6)](https://github.com/user-attachments/assets/6c8605ad-2f6e-4797-8822-57815a3730af)

    - **결과 해석**
        - `Extra = Using temporary; Using filesort` → **임시 테이블과 추가 정렬 연산 발생**
    - **해결 방법**: `user_id`와 `created_at`을 포함한 복합 인덱스 추가

    ```sql
    CREATE INDEX idx_order_user_created ON order_info(user_id, created_at);
    ```


1. 다중 테이블 JOIN에서 임시 테이블이 발생하는 경우

    ```sql
    SELECT 
        u.user_name, 
        MAX(o.order_id) AS latest_order_id,
        MAX(o.status) AS latest_status,
        SUM(o.order_total_amount) AS total_amount
    FROM user_info u
    JOIN order_info o ON u.user_id = o.user_id
    GROUP BY u.user_name
    ORDER BY MAX(o.created_at) DESC;
    ```

   ![image (7)](https://github.com/user-attachments/assets/63c96d64-4808-44b4-95ea-808cbab5b6a0)

    - 결과 해석
        - `Extra = Using temporary; Using filesort` → **임시 테이블과 추가 정렬 연산 발생**
    - 해결 방법 : **복합 인덱스 추가**

    ```sql
    CREATE INDEX idx_order_user_created ON order_info(user_id, created_at);
    ```


- **유용할 수 있는 경우**
    - 적절한 인덱스 없이도 특정 데이터 그룹화 및 정렬을 수행할 수 있다.
    - 메모리가 충분하면 빠르게 수행될 수도 있다.

- **문제되는 경우**
    - 데이터가 많아질수록 성능이 급격히 저하
    - 디스크에 저장되는 경우 속도가 더욱 감소

---

### 2-1-3. Using Filesort (디스크 또는 메모리 정렬)

- 개념
    - `ORDER BY`, `DISTINCT`, `GROUP BY` 등을 실행할 때, MySQL이 별도로 데이터를 정렬하는 방식
    - 인덱스를 사용하지 못하는 경우 디스크 정렬을 수행하여 성능 저하
    - `EXPLAIN` 실행 시 **Using filesort**가 표시되면 **디스크 또는 메모리 정렬이 발생**했다는 의미

- **발생 조건**
1. `ORDER BY`가 인덱스를 활용하지 못하는 경우

    ```sql
    SELECT * FROM order_info ORDER BY created_at DESC;
    ```

   ![image (8)](https://github.com/user-attachments/assets/b5408614-cbc5-47fe-b84b-93a1b78437ef)

    - 결과 해석
        - `type = ALL` → **Full Table Scan 발생**
        - `possible_keys = NULL` → **사용할 수 있는 인덱스 없다.**
        - `Extra = Using filesort` → **MySQL이 정렬을 위해 별도로 Filesort 수행**
    - **해결 방법**: `created_at` 컬럼에 인덱스 추가

    ```sql
    CREATE INDEX idx_order_created ON order_info(created_at);
    ```

2. `ORDER BY`와 `GROUP BY`를 함께 사용할 때

    ```sql
    SELECT user_id, COUNT(*), MAX(created_at) AS latest_order_date
    FROM order_info
    GROUP BY user_id
    ORDER BY latest_order_date DESC;
    ```

   ![image (9)](https://github.com/user-attachments/assets/31557a1a-3352-4da2-98e2-5b8fde33f97e)

    - **결과 해석**
        - `Extra = Using temporary; Using filesort` → **임시 테이블과 추가 정렬 연산 발생**
    - **해결 방법**: `user_id`와 `created_at`을 포함한 복합 인덱스 추가

    ```sql
    CREATE INDEX idx_order_user_created ON order_info(user_id, created_at);
    ```


- **유용할 수 있는 경우**
    - 소량의 데이터라면 큰 성능 문제가 발생하지 않을 수 있다.
    - 특정 상황에서 인덱스보다 Filesort가 더 빠를 수도 있다.

- **문제되는 경우**
    - 데이터가 많아질수록 성능 저하 심각
    - 디스크 정렬이 발생하면 속도가 급격히 저하

---

## 2-2. 프로젝트 기능 성능 문제 분석

현재 프로젝트에서 **주문량 상위 5개 상품 조회 기능** 쿼리를 분석해보자.

```sql
SELECT 
    product.product_id, 
    product.name, 
    product.price, 
    product.description, 
    SUM(order_item.quantity) AS total_quantity
FROM product
LEFT JOIN order_item ON order_item.product_id = product.product_id
LEFT JOIN order_info ON order_info.order_id = order_item.order_id
WHERE order_info.status = 'PAID'
AND order_info.pay_date BETWEEN NOW() - INTERVAL 3 DAY AND NOW()
GROUP BY product.product_id, product.name, product.price, product.description
ORDER BY total_quantity DESC
LIMIT 5;
```

- 실행 계획 해석
![image (10)](https://github.com/user-attachments/assets/e0aa508b-4c54-4275-bb57-3fdfd39b7388)

1. **`order_info` - `ALL` (Full Table Scan)**
    - `type = ALL`: **Full Table Scan 발생**
    - `rows = 4,897,962`: 거의 500만 개의 레코드를 **전부 검색**
    - `Extra = Using where; Using temporary; Using filesort`:
        - `Using where`: `WHERE o.status = 'PAID' AND o.pay_date BETWEEN NOW() - INTERVAL 3 DAY AND NOW()` 조건이 적용된다.
        - `Using temporary`: **임시 테이블이 생성된다.**
        - `Using filesort`: **디스크 정렬(Filesort) 발생한다.**

2. **`order_item` - `ref`**
    - `type = ref`: `order_id`를 **인덱스를 활용하여 검색한다.**
    - `rows = 7`: `order_info`의 각 행에 대해 평균적으로 **7개의 `order_item` 데이터**가 매칭된다.

3. **`product` - `eq_ref`**
    - `type = eq_ref`: **매우 효율적인 조인** → `PRIMARY KEY`를 활용한 단일 행 조회
    - `rows = 1`: `product_id`를 기준으로 **각 `order_item`에 대해 1개의 제품을 찾는다.**

아래와 같은 성능 문제가 발생할 가능성이 있다.

---

## 2-3. 성능 문제 정리

1. **`order_info` 에서 Full Table Scan 발생 (`type = ALL`)**
    - `o.status`와 `o.pay_date`에 적절한 인덱스가 없거나 인덱스를 활용하지 못한다.
    - 4,897,962개의 행을 **모두 스캔**하기 때문에 매우 비효율적.

2. **임시 테이블 (`Using temporary`) 사용**
    - `GROUP BY p.product_id, p.name, p.price, p.description` 때문에 MySQL이 **임시 테이블을 생성하여 그룹화** 수행.

3. **디스크 정렬 (`Using filesort`) 발생**
    - `ORDER BY SUM(oi.quantity) DESC` 정렬을 위해 추가적인 정렬 연산이 필요하여 발생.

---

# 3. 인덱싱 전략과 최적화 방안

## 3-1. 인덱스 전략

- Composite Index vs. Single Index
    - **Composite Index (복합 인덱스)**: 여러 개의 컬럼을 하나의 인덱스로 생성하여 검색 속도를 최적화한다.
    - **Single Index (단일 인덱스)**: 하나의 컬럼에 대해서만 인덱스를 생성하여 특정 조건에서 빠른 검색 가능하다.
- Primary Key vs. Unique Index
    - **Primary Key**: 기본 키로 테이블에서 유일한 레코드를 식별하는 필드
    - **Unique Index**: 중복을 허용하지 않는 인덱스, 특정 컬럼이 중복될 수 없는 경우 사용
- Covering Index
    - **Covering Index**는 쿼리 실행 시 필요한 모든 컬럼을 포함하는 인덱스


## 3-2. 조회 최적화 방안

- **`order_info` 테이블에서 `status, pay_date` 복합 인덱스 적용** → Full Table Scan 방지

    ```sql
    CREATE INDEX idx_order_status_date ON order_info (status, pay_date);
    ```

- **`order_item` 테이블에서 `order_id, product_id` 복합 인덱스 적용** → JOIN 최적화

    ```sql
    CREATE INDEX idx_orderitem_orderid_productid ON order_item (order_id, product_id);
    ```

- **`GROUP BY` 최적화 적용** → 임시 테이블 최소화

    ```sql
    SELECT
        p.product_id,
        p.name,
        p.price,
        p.description,
        total_quantity
    FROM product p
    JOIN (
        SELECT product_id, SUM(quantity) AS total_quantity
        FROM order_item oi
        JOIN order_info o ON oi.order_id = o.order_id
        WHERE o.status = 'PAID'
        AND o.pay_date BETWEEN NOW() - INTERVAL 3 DAY AND NOW()
        GROUP BY product_id
        ORDER BY total_quantity DESC
        LIMIT 5
    ) ranked_orders ON p.product_id = ranked_orders.product_id;
    ```

    - **서브쿼리를 활용하여 최적화된 데이터만 JOIN** → 불필요한 연산 제거
    - **쿼리 실행 방식 개선** → 인덱스를 활용한 실행 계획 검토 및 필요 시 추가 조정

    ```java
    @Query("
        SELECT new kr.hhplus.be.commerce.domain.product.TopProductResult(
            p.productId, p.name, p.price, p.description, SUM(oi.quantity)
        )
        FROM Product p
        LEFT JOIN OrderItem oi ON oi.productId = p.productId
        LEFT JOIN Order o ON o.orderId = oi.orderId
        WHERE o.status = :status 
        AND o.payDate BETWEEN :startDate AND :endDate
        GROUP BY p.productId, p.name, p.price, p.description
        ORDER BY SUM(oi.quantity) DESC
    ")
    List<TopProductResult> findTopProducts(@Param("status") OrderStatus status,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           Pageable pageable);
    ```

    - **JPQL에서는 `FROM` 절에서 서브쿼리를 사용할 수 없기 때문에 기존 SQL과 구조가 변경**
    - **기존 SQL에서 사용한 `LIMIT`은 `Pageable`을 활용하여 대체**
    - **JPQL에서 서브쿼리를 직접 사용할 수 없어 `JOIN` 방식으로 변경하여 실행 가능하도록 조정**
    - **DTO 매핑을 활용하여 쿼리 결과를 직접 객체로 변환**
    - 위 내용대로 수정했을 때 쿼리

    ```sql
    SELECT 
        p.product_id, 
        p.name, 
        p.price, 
        p.description, 
        SUM(oi.quantity) AS total_quantity
    FROM product p
    LEFT JOIN order_item oi ON oi.product_id = p.product_id
    LEFT JOIN order_info o ON o.order_id = oi.order_id
    WHERE o.status = 'PAID'
    AND o.pay_date BETWEEN NOW() - INTERVAL 3 DAY AND NOW()
    GROUP BY p.product_id, p.name, p.price, p.description
    ORDER BY total_quantity DESC
    LIMIT 5;
    ```


---

# 4. **인덱스 성능 테스트**

## **4-1. 테스트 환경 및 전제 조건**

### **4-1-1. 독립적인 DB 환경 구성**

- 인덱스 및 쿼리 변경에 따른 성능 비교를 위해 세 개의 독립적인 스키마 사용:
    - **`commerce`**: 기존 스키마 (인덱스 미적용, 기존 쿼리 사용)
    - **`commerce_tuned`**: 인덱스 미적용, 튜닝된 쿼리 사용
    - **`commerce_indexed`**: 인덱스 적용, 튜닝된 쿼리 사용
- 동일한 데이터셋을 유지하며 **쿼리 변경과 인덱스 적용 여부를 개별적으로 비교 수행**.
- 테스트 대상 테이블: `order_info`, `order_item`, `product`

### **4-1-2. 통제 변수 (Control Variables)**

- **동일한 데이터셋 사용**: `commerce`, `commerce_tuned`, `commerce_indexed` 스키마에 동일한 데이터를 삽입
- **동일한 DBMS 설정**: MySQL 동일 버전 및 설정 유지
- **동일한 하드웨어 환경**: 동일한 서버에서 테스트 실행하여 변수를 최소화

### **4-1-3. 독립 변수 (Independent Variable)**

- `commerce` 스키마에는 기존 쿼리 + 인덱스 없음
- `commerce_tuned` 스키마에는 **튜닝된 쿼리 적용, 인덱스 없음**
- `commerce_indexed` 스키마에는 **튜닝된 쿼리 적용, 인덱스 추가**
- 적용한 인덱스 (`commerce_indexed` 전용):

    ```sql
    CREATE INDEX idx_order_status_date ON commerce_indexed.order_info (status, pay_date);
    CREATE INDEX idx_orderitem_orderid_productid ON commerce_indexed.order_item (order_id, product_id);
    ```


### **4-1-4. 종속 변수 (Dependent Variable)**

- `EXPLAIN ANALYZE`를 이용하여 실행 시간 및 실행 계획 비교
- **측정 항목**:
    - 쿼리 실행 시간 (ms 단위)
    - `rows` 값 비교 (스캔된 행 수)
    - `type` 값 (INDEX / RANGE / FULL TABLE SCAN 여부)
    - `Using temporary`, `Using filesort` 여부

---

## **4-2. 테스트 수행 및 결과 비교**

### **4-2-1. 기존 쿼리 실행 (인덱스 미적용, `commerce` 스키마)**

- **Full Table Scan 발생 (`Table scan on order_info`)** → `6.21M` 행 전체 스캔
- `Using temporary; Using filesort` → 임시 테이블 사용으로 정렬 비용 증가
- **실행 시간**: `9028ms`

### **4-2-2. 튜닝된 쿼리 실행 (인덱스 미적용, `commerce_tuned` 스키마)**

- **쿼리 최적화 적용 (JOIN 방식 변경, 필터링 개선 등)**
- **Full Table Scan 여전히 존재하지만, 불필요한 데이터 로딩 감소**
- **실행 시간 감소**: `8276ms` (기존 쿼리 대비 약 8% 개선)

### **4-2-3. 튜닝된 쿼리 + 인덱스 적용 (인덱스 적용, `commerce_indexed` 스키마)**

- `order_info` 테이블에서 **인덱스 사용 (`idx_order_status_date`)** → `Full Table Scan 제거`
- `Using index` 적용으로 **임시 테이블 및 파일 정렬 제거**
- **실행 시간 단축**: `2165ms` (기존 대비 **4배 속도 개선**)

---

# **5. 실행 계획 비교 분석**

| 테스트 환경 | `type` 변경 | `rows` 값 | `Using temporary / Using filesort` | 실행 시간 (ms) |
| --- | --- | --- | --- | --- |
| **기존 쿼리 (commerce, 인덱스 미적용)** | `ALL` (Full Table Scan) | `6.21M` | ✅ 사용 | **9028ms** |
| **튜닝된 쿼리 (commerce_tuned, 인덱스 미적용)** | `ALL` (Full Table Scan) | `6.21M` | ✅ 사용 (일부 감소) | **8276ms** |
| **튜닝된 쿼리 + 인덱스 적용 (commerce_indexed)** | `RANGE` (Index Range Scan) | `45,551` | ❌ 제거 | **2165ms** |
- `type` 변경: `ALL` → `RANGE`로 변경되어 **Full Table Scan 제거**
- `rows` 값 감소: 6.21M → 45,551 행으로 **스캔량 감소**
- `Using temporary`, `Using filesort` 제거 → **임시 테이블 및 디스크 정렬 최소화**
- **전체 실행 시간 4배 이상 단축 (9028ms → 2165ms)**

---

# **6. 결론 및 개선 효과**

## **6-1. 튜닝된 쿼리만 적용 시**

1. **쿼리 최적화만으로 8% 개선**되었으나, Full Table Scan은 여전히 존재
2. 불필요한 데이터 로딩이 줄어 실행 시간이 다소 단축됨

## **6-2. 튜닝된 쿼리 + 인덱스 적용 시**

1. **Full Table Scan 제거** → 실행 시간 **4배 이상 단축**
2. `Using temporary`, `Using filesort` 제거 → **메모리 및 디스크 I/O 감소**
3. 조인 성능 개선 (`Nested Loop Join` 성능 향상) → 최적화 효과 극대화

## **6-3. 추가 개선 가능성**

- `Covering Index` 적용 고려 (필요한 모든 컬럼을 인덱스에 포함)
- 쿼리 캐싱 또는 `Materialized View` 활용 검토
- `order_info`의 최신 데이터만 조회하도록 **파티셔닝 고려**

## **6-4. 결론**

**쿼리 튜닝만으로는 한계가 있으며, 인덱스를 함께 적용해야 최적의 성능 개선을 달성할 수 있다.**

---

# 참고

- [MySQL 공식 문서: EXPLAIN](https://dev.mysql.com/doc/refman/8.0/en/explain-output.html)
- [SQL 쿼리 프로파일링](https://velog.io/@mocaccino/Profileling-Performance-%EC%BF%BC%EB%A6%AC-%EC%84%B1%EB%8A%A5-%EB%B6%84%EC%84%9D)
- [MySql SQL 튜닝의 실행 계획 파헤치기](https://velog.io/@clock509/MySQL-SQL-%ED%8A%9C%EB%8B%9D%EC%9D%98-%EC%8B%A4%ED%96%89-%EA%B3%84%ED%9A%8D-%ED%8C%8C%ED%97%A4%EC%B9%98%EA%B8%B0)
- [쿼리 튜닝의 I/O 병목 개선 원리 With MySQL](https://velog.io/@fishphobiagg/%EC%BF%BC%EB%A6%AC-%ED%8A%9C%EB%8B%9D%EC%9D%98-IO-%EB%B3%91%EB%AA%A9-%EA%B0%9C%EC%84%A0-%EC%9B%90%EB%A6%AC-With-MySQL-JPA)
- [SQL 성능 확인, Query Plan 보는 법](https://spidyweb.tistory.com/460)
- [MySql limit 최적화](https://jeong-pro.tistory.com/m/244#google_vignette)
- [MySql 생애 첫 쿼리 튜닝을 통한 조회 성능 개선](https://wimoney.tistory.com/entry/MySQLSpringDataJPA-%EC%83%9D%EC%95%A0-%EC%B2%AB-%EC%BF%BC%EB%A6%AC-%ED%8A%9C%EB%8B%9D%EC%9D%84-%ED%86%B5%ED%95%9C-%EC%A1%B0%ED%9A%8C-%EC%84%B1%EB%8A%A5%EC%9D%84-%EA%B0%9C%EC%84%A0)