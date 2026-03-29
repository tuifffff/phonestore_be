package com.example.demo.dto.request;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data @FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewRequest {
    Integer productId;
    Integer rating; // 1-5 sao
    String comment;
}