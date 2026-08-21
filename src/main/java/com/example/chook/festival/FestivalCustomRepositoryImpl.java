package com.example.chook.festival;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static com.example.chook.festival.QFestival.festival;

@Slf4j
public class FestivalCustomRepositoryImpl implements FestivalCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public FestivalCustomRepositoryImpl(EntityManager em){
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Festival> searchFestival(String type, String keyword, String month, Pageable pageable){
        BooleanExpression condition = eqSearchType(type, keyword, month);

        List<Festival> festivalList = jpaQueryFactory
                .selectFrom(festival)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(festival.startDate.desc())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory
                .select(festival.count())
                .from(festival)
                .where(condition);

        return PageableExecutionUtils.getPage(festivalList, pageable, count::fetchOne);
    }

    @Override
    public List<Festival> findActiveFestivalsTop9(LocalDate today) {
        return jpaQueryFactory
                .selectFrom(festival)
                .where(festival.endDate.goe(today))
                .orderBy(
                        new CaseBuilder()
                                .when(festival.startDate.loe(today)).then(1)
                                .otherwise(2).asc(),
                        festival.startDate.asc()
                )
                .limit(10)
                .fetch();
    }

    private BooleanExpression eqSearchType(String type, String keyword, String month){

        if(StringUtils.hasText(month)){
            try{
                int monthValue = Integer.parseInt(month);
                return festival.startDate.month().eq(monthValue);
            } catch (Exception e){
                return null;
            }
        }

        if(!StringUtils.hasText(type) || !StringUtils.hasText(keyword)){
            return null;
        }

        switch (type){
            case "t" : return festival.title.containsIgnoreCase(keyword);
            case "c" : return festival.overview.containsIgnoreCase(keyword);
            case "r" : return festival.address.containsIgnoreCase(keyword);
            default: return null;
        }
    }


    // address 받아오기 전 region 데이터랑 join 했던 것

//    private BooleanExpression eqRegion(String keyword){
//        BooleanExpression sido = JPAExpressions
//                .selectOne()
//                .from(regionSido)
//                .where(
//                        festival.zipCode.substring(0, 2).eq(regionSido.code)
//                                .and(
//                                        regionSido.name.containsIgnoreCase(keyword)
//                                                .or(regionSido.shortName.containsIgnoreCase(keyword))
//                                )
//                ).exists();
//
//        BooleanExpression sigungu = JPAExpressions
//                .selectOne()
//                .from(regionSigungu)
//                .where(
//                        regionSigungu.name.containsIgnoreCase(keyword)
//                ).exists();
//
//        return sido.or(sigungu);
//
//    }
}
