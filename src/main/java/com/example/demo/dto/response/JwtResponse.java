package com.example.demo.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class JwtResponse {
    private String token;
    private Integer id;
    private String username;
    private String role;
    private List<String> permissions; // NEW: danh sách quyền để FE render dynamic sidebar

    public JwtResponse(String token, Integer id, String username, String role, List<String> permissions) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.role = role;
        this.permissions = permissions;
    }
}
