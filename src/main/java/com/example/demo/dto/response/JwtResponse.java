package com.example.demo.dto.response;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private Integer id;
    private String username;
    private String role;

    public JwtResponse(String token, Integer id, String username, String role) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.role = role;
    }
}
