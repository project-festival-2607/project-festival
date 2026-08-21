package com.example.chook.festival;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FestivalDTO {
    private String contentId;
    private String title;
    private String homepage;
    private BigDecimal mapX;
    private BigDecimal mapY;
    private String overview;
    private String tel;
    private String telName;
    private String ageLimit;
    private String eventPlace;
    private String zipCode;
    private String address;
    private String playTime;
    private String program;
    private String useTime;
    private String startDate;
    private String endDate;
    private String firstImage;
    private String secondImage;

    private Long member;

    public String getOfficialHomepage() {
        if (this.homepage == null || this.homepage.isBlank()) {
            return "";
        }
        var matcher = java.util.regex.Pattern.compile("https?://[^\\s]+").matcher(this.homepage);
        return matcher.find() ? matcher.group() : this.homepage;
    }

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
