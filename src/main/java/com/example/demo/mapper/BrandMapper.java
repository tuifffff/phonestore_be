package com.example.demo.mapper;
import com.example.demo.dto.request.BrandRequest;
import com.example.demo.dto.response.BrandResponse;
import com.example.demo.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface BrandMapper {
    // Request -> Entity
    @Mapping(target = "brandName", source = "name")
    @Mapping(target = "brandLogo", source = "logo")
    Brand toEntity(BrandRequest request);

    // Entity -> Response
    @Mapping(target = "id", source = "brandID")
    @Mapping(target = "name", source = "brandName")
    @Mapping(target = "logo", source = "brandLogo")
    BrandResponse toResponse(Brand brand);
}
