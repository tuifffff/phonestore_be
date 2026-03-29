package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
    private Integer addressID;
    private String street;
    private String district;
    private String city;
    private Boolean isDefault;
}