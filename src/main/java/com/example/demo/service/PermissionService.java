package com.example.demo.service;

import com.example.demo.dto.request.PermissionRequest;
import com.example.demo.dto.response.PermissionResponse;
import com.example.demo.entity.Permission;
import com.example.demo.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // Tự thêm private final cho mọi field
public class PermissionService {

    PermissionRepository permissionRepository;

    // 1. Tạo quyền mới (VD: CREATE_POST, DELETE_USER)
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = Permission.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return toResponse(permissionRepository.save(permission));
    }

    // 2. Lấy danh sách tất cả các quyền
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // 3. Xóa quyền theo tên (ID)
    public void delete(String name) {
        if (!permissionRepository.existsById(name)) {
            throw new RuntimeException("Quyền này không tồn tại!");
        }
        permissionRepository.deleteById(name);
    }

    // Hàm phụ để chuyển đổi từ Entity sang Response DTO
    private PermissionResponse toResponse(Permission p) {
        return PermissionResponse.builder()
                .name(p.getName())
                .description(p.getDescription())
                .build();
    }
}