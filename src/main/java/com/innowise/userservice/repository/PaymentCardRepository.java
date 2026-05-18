package com.innowise.userservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.innowise.userservice.model.PaymentCard;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    Page<PaymentCard> findByUserId(Long userId, Pageable pageable);

    @Query(
            value = """
                    SELECT COUNT(*)
                    FROM payment_cards
                    WHERE user_id = :userId
                    """,
            nativeQuery = true
    )
    long countByUserId(Long userId);
}
