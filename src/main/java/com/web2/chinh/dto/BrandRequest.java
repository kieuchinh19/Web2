package com.web2.chinh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandRequest {

    @NotBlank(message = "Tên thương hiệu không được để trống")
    @Size(min = 2, max = 100, message = "Tên thương hiệu từ 2 đến 100 ký tự")
    private String name;

    @Size(max = 100, message = "Tên quốc gia không quá 100 ký tự")
    private String country;

    @Size(max = 500, message = "Mô tả không quá 500 ký tự")
    private String description;

    private String logo;

    private String website;
}
