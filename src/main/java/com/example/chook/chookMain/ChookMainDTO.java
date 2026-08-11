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
public class ChookMainDTO {
    private String contentId;
    private String title;
    private String startDate;
    private String endDate;
    private String firstImage;
    private String program;

    public String getStatus() {
        if (this.startDate == null || this.endDate == null ||
                this.startDate.isEmpty() || this.endDate.isEmpty()) {
            return "정보없음";
        }

        LocalDate now = LocalDate.now();
        LocalDate start = LocalDate.parse(this.startDate);
        LocalDate end = LocalDate.parse(this.endDate);

        if (now.isAfter(end)) {
            return "종료된행사";
        }

        else if (now.isBefore(start)) {
            return "진행예정";
        }

        else {
            return "진행중";
        }
    }
}
