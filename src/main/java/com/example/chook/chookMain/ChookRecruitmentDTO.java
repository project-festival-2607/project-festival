package com.example.chook.chookMain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChookRecruitmentDTO {
    private Long recruitmentId;
    private String recruitmentTitle;
    private LocalDate workingStartDate;
    private LocalDate workingEndDate;
}
