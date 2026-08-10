package com.example.chook.chookMain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChookMainDTO {
    private String contentId;
    private String title;
    private String startDate;
    private String endDate;
    private String firstImage;
    private String program;
}
