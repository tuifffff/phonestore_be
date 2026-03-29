package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    // Đổi List thành Page và thêm tham số Pageable
    Page<Order> findByUser_UserID(Integer userID, Pageable pageable);
    @Query("SELECT SUM(o.total) FROM Order o " +
            "WHERE MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year " +
            "AND o.status = :status")
    BigDecimal calculateMonthlyRevenue(@Param("month") int month,
                                       @Param("year") int year,
                                       @Param("status") OrderStatus status);

    // 2. Top sản phẩm: Đi từ OrderDetail -> Version -> Product để lấy tên
    @Query("SELECT od.version.product.productName, SUM(od.quantity) as totalSold " +
            "FROM OrderDetail od " +
            "GROUP BY od.version.product.productName " +
            "ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts();
    @Query("SELECT o FROM Order o WHERE " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:keyword IS NULL OR CAST(o.orderID AS string) LIKE %:keyword% " +
            "OR o.receiverName LIKE %:keyword% OR o.phoneNumber LIKE %:keyword%)")
    Page<Order> findAllOrdersWithFilter(
            @Param("status") OrderStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    Long countByStatus(OrderStatus status);
}
