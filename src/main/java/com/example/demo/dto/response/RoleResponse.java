package com.example.demo.dto.response;

import lombok.*;
import java.util.Set;
import com.example.demo.dto.response.PermissionResponse;

// --- Response ---
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoleResponse {
    private String name;
    private String description;
    private Set<PermissionResponse> permissions;
}
