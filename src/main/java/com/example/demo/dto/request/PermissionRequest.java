package com.example.demo.dto.request;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PermissionRequest {
    private String name;
    private String description;
}
