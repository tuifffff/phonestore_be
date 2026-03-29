package com.example.demo.repository;

import com.example.demo.entity.CartDetails;
import com.example.demo.entity.CartDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartDetailsRepository extends JpaRepository<CartDetails, CartDetailsId> {
    List<CartDetails> findByCart_User_UserID(Integer userID);
    List<CartDetails> findByCart_CartID(Integer cartId);
}
