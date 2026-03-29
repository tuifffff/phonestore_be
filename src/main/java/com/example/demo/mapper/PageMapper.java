package com.example.demo.mapper;

import com.example.demo.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.function.Function;

public class PageMapper {

    // Hàm Generic: T là Entity, R là DTO
    public static <T, R> PageResponse<R> toPageResponse(Page<T> pageData, Function<T, R> mapperFunction) {
        return PageResponse.<R>builder()
                .content(pageData.getContent().stream().map(mapperFunction).toList())
                .page(pageData.getNumber())
                .size(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .build();
    }
}