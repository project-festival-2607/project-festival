package com.example.chook.festival;

import com.example.chook.member.entity.Member;
import com.example.chook.recruitment.entity.Recruitment;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class  Festival {
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

    private String address;

    @Column(name = "play_time")
    private String playTime;

    @Column(columnDefinition = "TEXT")
    private String program;

    @Column(name = "use_time")
    private String useTime;

    @Column(name = "start_date", columnDefinition = "")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "first_image")
    private String firstImage;

    @Column(name = "second_image")
    private String secondImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
      name = "member_id",
      nullable = true,
      foreignKey = @ForeignKey(name = "fk_festival_member_id")
    )
    @ToString.Exclude
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "festival", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    private List<FestivalFile> festivalFiles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "festival", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    private List<Recruitment> recruitments = new ArrayList<>();

}