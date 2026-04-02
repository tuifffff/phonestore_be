package com.example.demo.dto.request;

import lombok.Data;

@Data
public class UpdateMyInfoRequest {
    private String email;
    private String phoneNumber;
    private String fullName;
    private String avatar;
    private String gender;
}
