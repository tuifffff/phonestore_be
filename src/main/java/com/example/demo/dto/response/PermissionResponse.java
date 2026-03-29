
package com.example.demo.dto.response;
import lombok.*;
// --- Response ---
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PermissionResponse {
    private String name;
    private String description;
}