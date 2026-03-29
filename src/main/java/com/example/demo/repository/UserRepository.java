package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // --- CÁC HÀM TÌM KIẾM VÀ CHỐNG TRÙNG CỦA BOBOI (GIỮ NGUYÊN) ---
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Hàm search cũ (không loại trừ)
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContaining(
            String username, String email, String phoneNumber, Pageable pageable);

    // Hàm lọc role cũ (không loại trừ)
    Page<User> findByRole_Name(String roleName, Pageable pageable);

    // --- CÁC HÀM MỚI ĐỂ ẨN ADMIN (TỐI ƯU THEO USERNAME) ---

    // 1. Mặc định lấy tất cả ngoại trừ chính mình
    Page<User> findByUsernameNot(String username, Pageable pageable);

    // 2. Lọc theo Role và loại trừ bản thân
    Page<User> findByRole_NameAndUsernameNot(String roleName, String username, Pageable pageable);

    // 3. Tìm kiếm theo keyword và loại trừ bản thân
    @Query("SELECT u FROM User u WHERE u.username <> :currentAdminUsername AND " +
            "(u.username LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phoneNumber LIKE %:keyword%)")
    Page<User> searchUsersExcludeSelf(@Param("keyword") String keyword,
                                      @Param("currentAdminUsername") String currentAdminUsername,
                                      Pageable pageable);
}