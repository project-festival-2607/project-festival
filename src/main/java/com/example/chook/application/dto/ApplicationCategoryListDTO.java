package com.example.chook.application.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationCategoryListDTO {

    // 알바
    private List<ApplicationDTO> individualApplications;

    // 푸드트럭
    private List<ApplicationDTO> foodTruckApplications;

    // 장비
    private List<ApplicationDTO> equipmentApplications;

    // 기타
    private List<ApplicationDTO> etcApplications;
}
