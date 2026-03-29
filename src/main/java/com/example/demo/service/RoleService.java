package com.example.demo.service;

import com.example.demo.dto.request.RoleRequest;
import com.example.demo.dto.response.RoleResponse;
import com.example.demo.dto.response.PermissionResponse;
import com.example.demo.entity.Role;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    public RoleResponse create(RoleRequest request) {
        var permissions = permissionRepository.findAllById(request.getPermissions());
        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .permissions(new HashSet<>(permissions))
                .build();
        return toResponse(roleRepository.save(role));
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(this::toResponse).toList();
    }

    public void delete(String name) { roleRepository.deleteById(name); }

    private RoleResponse toResponse(Role r) {
        return RoleResponse.builder()
                .name(r.getName())
                .description(r.getDescription())
                .permissions(r.getPermissions().stream()
                        .map(p -> PermissionResponse.builder().name(p.getName()).description(p.getDescription()).build())
                        .collect(Collectors.toSet()))
                .build();
    }
}