package com.example.demo.mapper;

import com.example.demo.dto.response.BannerResponse;
import com.example.demo.entity.Banner;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BannerMapper {
    BannerResponse toBannerResponse(Banner banner);
}
