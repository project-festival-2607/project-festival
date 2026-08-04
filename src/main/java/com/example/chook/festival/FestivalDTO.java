package com.example.chook.festival;

import lombok.*;

import java.math.BigDecimal;

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
    private String playTime;
    private String program;
    private String useTime;
    private String startDate;
    private String endDate;
    private String firstImage;
    private String secondImage;

    public String getOfficialHomepage() {
        if (this.homepage == null || this.homepage.isBlank()) {
            return "";
        }
        var matcher = java.util.regex.Pattern.compile("https?://[^\\s]+").matcher(this.homepage);
        return matcher.find() ? matcher.group() : this.homepage;
    }
}
