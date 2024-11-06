package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findFirstByUserIdAndDeletedFalseAndActiveTrue(String userId);

    boolean existsByRoleIdAndDeletedFalse(Long roleId);

    @Query("SELECT au FROM AppUser au WHERE au.deleted = FALSE AND au.role.code NOT IN :roleCodes  ORDER BY au.id")
    Page<AppUser> findByDeletedFalse(Pageable pageable, @Param("roleCodes") List<String> roleCodes);

    List<AppUser> findByIsRegionHeadTrueAndDeletedFalse();
}
