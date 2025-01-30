package kr.hhplus.be.commerce.domain.balance;

import jakarta.persistence.*;
import kr.hhplus.be.commerce.domain.utils.AuditingFields;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "balance")
public class Balance extends AuditingFields implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id", nullable = false)
    @Comment("잔액 ID")
    private Long balanceId;

    @Column(name = "user_id", nullable = false)
    @Comment("사용자 ID")
    private Long userId;

    @Column(name = "amount", nullable = false)
    @Comment("금액")
    private Long amount;

    @Version
    @Column(name = "version", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    @Comment("버전")
    private Long version;

    public static Balance create(Long userId, Long amount) {
        Balance entity = new Balance();
        entity.userId = userId;
        entity.amount = amount;
        return entity;
    }

    public void changeAmount(Long amount) {
        this.amount = amount;
    }
}