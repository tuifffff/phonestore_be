package com.example.demo.mapper;

import com.example.demo.dto.response.ReviewResponse;
import com.example.demo.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "username", source = "user.username")
    ReviewResponse toResponse(Review review);
}
