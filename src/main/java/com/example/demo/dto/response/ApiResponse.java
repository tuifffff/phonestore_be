package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // Chỉ hiện các trường không null (nếu message null thì giấu đi)
public class ApiResponse <T> {
    @Builder.Default
    int code = 1000; // 1000 là thành công, 1001 là lỗi... tùy ông quy định
    String message;
    T result; // Đây là nơi chứa dữ liệu thật (Role, User, List...)
}