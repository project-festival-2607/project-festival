package com.example.chook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Festival {
    @Id
    @Column(name = "content_id")
    private String contentId;

    @Column(nullable = false)
    private String title;

    private String homepage;

    @Column(name = "map_x", precision = 11, scale = 6)
    private BigDecimal mapX;

    @Column(name = "map_y", precision = 10, scale = 6)
    private BigDecimal mapY;

    @Column(columnDefinition = "TEXT")
    private String overview;

    private String tel;

    @Column(name = "tel_name")
    private String telName;

    @Column(name = "age_limit")
    private String ageLimit;

    @Column(name = "event_place")
    private String eventPlace;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "play_time")
    private String playTime;

    @Column(columnDefinition = "TEXT")
    private String program;

    @Column(name = "use_time")
    private String useTime;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    @Column(name = "first_image")
    private String firstImage;

    @Column(name = "second_image")
    private String secondImage;
}
