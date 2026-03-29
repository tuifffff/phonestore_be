package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder @NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String userID;
    String username;
    String email;
    String phoneNumber;
    String address;
    String gender;
    String roleName;
    // Tuyệt đối không để List<Order> ở đây để tránh vòng lặp!
}
