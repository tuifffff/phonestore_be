package com.example.demo.dto.request;
import lombok.*;
import java.util.Set;
import com.example.demo.dto.response.PermissionResponse;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoleRequest {
    private String name;
    private String description;
    private Set<String> permissions; // Chứa danh sách tên Permission (VD: ["CREATE_PRODUCT"])
}
