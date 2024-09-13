package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    PasswordResetOtp findFirstByUserIdAndDeletedFalse(String userId);

    @Modifying
    @Query("DELETE FROM PasswordResetOtp pro WHERE pro.userId = :userId")
    void deleteByUserId(String userId);
}
