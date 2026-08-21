package com.example.chook.admin.repository;

import com.example.chook.admin.condition.payment.*;
import com.example.chook.admin.dto.payment.*;
import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.payment.charge.*;
import com.example.chook.admin.enums.payment.product.ProductDateRangeType;
import com.example.chook.admin.enums.payment.product.ProductKeywordType;
import com.example.chook.admin.enums.payment.product.ProductSortCriteria;
import com.example.chook.admin.enums.payment.product.ProductStatus;
import com.example.chook.admin.enums.payment.refund.RefundDateRangeType;
import com.example.chook.admin.enums.payment.refund.RefundKeywordType;
import com.example.chook.admin.enums.payment.refund.RefundSortCriteria;
import com.example.chook.admin.enums.payment.refund.RefundStatus;
import com.example.chook.admin.enums.payment.summary.PaymentRecordType;
import com.example.chook.admin.enums.payment.summary.SummaryDateRangeType;
import com.example.chook.admin.enums.payment.summary.SummaryKeywordType;
import com.example.chook.admin.enums.payment.summary.SummarySortCriteria;
import com.example.chook.admin.enums.payment.use.UseDateRangeType;
import com.example.chook.admin.enums.payment.use.UseKeywordType;
import com.example.chook.admin.enums.payment.use.UseSortCriteria;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCheckFunction;
import static com.example.chook.admin.util.KeywordUtils.getOrderSpecifier;
import static com.example.chook.common.util.QuerydslUtils.*;
import static com.example.chook.member.entity.QMember.member;
import static com.example.chook.payment.entity.QPayClassify.payClassify;
import static com.example.chook.payment.entity.QPayment.payment;
import static com.example.chook.payment.entity.QPointHistory.pointHistory;
import static com.example.chook.payment.entity.QProduct.product;
import static com.example.chook.payment.entity.QRefund.refund;
import static com.example.chook.recruitment.entity.QRecruitment.recruitment;

@Repository
@Slf4j
public class AdminPaymentRepositoryImpl implements AdminPaymentRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public AdminPaymentRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<SummaryTableDTO> getSummaryPage(Pageable pageable, SummarySearchCondition condition) {

    JPAQuery<SummaryTableDTO> resultQuery = this.jpaQueryFactory.select(Projections.fields(

      SummaryTableDTO.class,

      payClassify.payClassifyId.as("recordId"),
      payClassify.createdAt.as("recordedAt"),

      payClassify.member.id.as("memberId"),
      payClassify.member.username.as("memberUsername"),
      payClassify.member.name.as("memberName"),

      pointHistory.pointChanging.sum().as("pointChanging"),
      pointHistory.pType.as("rawPaymentRecordType"),

      recruitment.id.as("recruitmentId"),
      recruitment.title.as("recruitmentTitle")

    )).from(payClassify);

    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(payClassify.payClassifyId.countDistinct())
      .from(payClassify);

    resultQuery = applySummaryJoin(resultQuery);
    countQuery = applySummaryJoin(countQuery);

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(applySummaryKeywordFilter(condition.keywordList(), condition.keywordType(), condition.keywordCriteria()))
      .and(applyPaymentRecordTypeFilter(condition.paymentRecordType()))
      .and(applySummaryDateRangeFilter(
        condition.dateRangeType(),
        condition.startDateTime(),
        condition.endDateTime()
      ));

    resultQuery.where(whereCondition);
    resultQuery = applySummaryGroupBy(resultQuery);

    List<SummaryTableDTO> content = resultQuery
      .orderBy(getSummaryOrderSpecifierArray(condition.sortCriteria(), condition.ascending()))
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetch();

    Long total = countQuery
      .where(whereCondition)
      .fetchOne();

    return new PageImpl<>(
      content,
      pageable,
      total != null ? total : 0
    );

  }

  @Override
  public Page<ChargeTableDTO> getChargePage(Pageable pageable, ChargeSearchCondition condition) {

    JPAQuery<ChargeTableDTO> resultQuery = this.jpaQueryFactory.select(Projections.fields(

      ChargeTableDTO.class,

      payment.paymentId,

      payment.member.id.as("memberId"),
      payment.member.username.as("memberUsername"),
      payment.member.name.as("memberName"),

      payment.product.productId,
      payment.pointGet,
      payment.product.productName,

      payment.method.as("rawPaymentMethod"),

      payment.paymentKey,
      payment.orderId,

      payment.paymentStatus.as("rawPaymentStatus"),

      payment.requestedAt,
      payment.approvedAt,
      payment.createdAt,
      payment.updatedAt,

      pointHistory.payClassify.payClassifyId.as("recordId")

    )).from(payment);

    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(payment.count())
      .from(payment);

    resultQuery = applyPaymentJoin(resultQuery);
    countQuery = applyPaymentJoin(countQuery);

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(applyChargeKeywordFilter(condition.keywordList(), condition.keywordType(), condition.keywordCriteria()))
      .and(applyPaymentMethodFilter(condition.paymentMethod()))
      .and(applyPaymentStatusFilter(condition.paymentStatus()))
      .and(applyChargeDateRangeFilter(
        condition.dateRangeType(),
        condition.startDateTime(),
        condition.endDateTime()
      ));

    List<ChargeTableDTO> content = resultQuery
      .where(whereCondition)
      .orderBy(getChargeOrderSpecifierArray(condition.sortCriteria(), condition.ascending()))
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetch();

    Long total = countQuery
      .where(whereCondition)
      .fetchOne();

    return new PageImpl<>(
      content,
      pageable,
      total != null ? total : 0
    );

  }

  @Override
  public Page<UseTableDTO> getUsePage(Pageable pageable, UseSearchCondition condition) {

    JPAQuery<UseTableDTO> resultQuery = this.jpaQueryFactory.select(Projections.fields(

      UseTableDTO.class,

      pointHistory.pointHistoryId,
      pointHistory.createdAt.as("usedAt"),
      pointHistory.pointChanging.negate().as("pointChanging"),

      pointHistory.member.id.as("memberId"),
      pointHistory.member.username.as("memberUsername"),
      pointHistory.member.name.as("memberName"),

      pointHistory.payment.paymentId,

      pointHistory.recruit.id.as("recruitmentId"),
      pointHistory.recruit.title.as("recruitmentTitle"),

      pointHistory.payClassify.payClassifyId.as("recordId")

    )).from(pointHistory);

    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(pointHistory.count())
      .from(pointHistory);

    resultQuery = applyUseJoin(resultQuery);
    countQuery = applyUseJoin(countQuery);

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(equalsIgnoreCase(pointHistory.pType, "USE"))
      .and(applyUseKeywordFilter(condition.keywordList(), condition.keywordType(), condition.keywordCriteria()))
      .and(applyUseDateRangeFilter(
        condition.dateRangeType(),
        condition.startDateTime(),
        condition.endDateTime()
      ));

    List<UseTableDTO> content = resultQuery
      .where(whereCondition)
      .orderBy(getUseOrderSpecifierArray(condition.sortCriteria(), condition.ascending()))
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetch();

    Long total = countQuery
      .where(whereCondition)
      .fetchOne();

    return new PageImpl<>(
      content,
      pageable,
      total != null ? total : 0
    );


  }

  @Override
  public Page<RefundTableDTO> getRefundPage(Pageable pageable, RefundSearchCondition condition) {

    JPAQuery<RefundTableDTO> resultQuery = this.jpaQueryFactory.select(Projections.fields(

        RefundTableDTO.class,

        refund.refundId.as("id"),
        refund.refundAmount,
        refund.refundReal,
        refund.refundComplete.as("recordedAt"),
        refund.canceledAt.as("canceledAt"),
        refund.transactionKey,
        refund.refundStatus.as("rawStatus"),

        refund.member.id.as("memberId"),
        refund.member.username.as("memberUsername"),
        refund.member.name.as("memberName"),

        refund.payClassify.payClassifyId.as("recordId")

      ))
      .from(refund);

    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(refund.count())
      .from(refund);

    resultQuery = applyRefundJoin(resultQuery);
    countQuery = applyRefundJoin(countQuery);

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(applyRefundKeywordFilter(condition.keywordList(), condition.keywordType(), condition.keywordCriteria()))
      .and(applyRefundStatusFilter(condition.status()))
      .and(applyRefundDateRangeFilter(
        condition.dateRangeType(),
        condition.startDateTime(),
        condition.endDateTime()
      ));

    List<RefundTableDTO> content = resultQuery
      .where(whereCondition)
      .orderBy(getRefundOrderSpecifierArray(condition.sortCriteria(), condition.ascending()))
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetch();

    Long total = countQuery
      .where(whereCondition)
      .fetchOne();

    return new PageImpl<>(
      content,
      pageable,
      total != null ? total : 0
    );

  }

  @Override
  public Page<ProductTableDTO> getProductPage(Pageable pageable, ProductSearchCondition condition) {
    JPAQuery<ProductTableDTO> resultQuery = this.jpaQueryFactory.select(Projections.fields(

        ProductTableDTO.class,

        product.productId,
        product.pointGet,
        product.productPrice,
        product.productName,

        product.createdAt,
        product.deletedAt

      ))
      .from(product);

    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(product.count())
      .from(product);

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(applyProductKeywordFilter(condition.keywordList(), condition.keywordType(), condition.keywordCriteria()))
      .and(applyProductStatusFilter(condition.status()))
      .and(applyProductDateRangeFilter(
        condition.dateRangeType(),
        condition.startDateTime(),
        condition.endDateTime()
      ));

    List<ProductTableDTO> content = resultQuery
      .where(whereCondition)
      .orderBy(getProductOrderSpecifierArray(condition.sortCriteria(), condition.ascending()))
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetch();

    Long total = countQuery
      .where(whereCondition)
      .fetchOne();

    return new PageImpl<>(
      content,
      pageable,
      total != null ? total : 0
    );

  }

  // #################### JOIN 적용 메서드 ####################

  private <T> JPAQuery<T> applySummaryJoin(JPAQuery<T> query) {
    return query
      .leftJoin(pointHistory)
      .on(pointHistory.payClassify.payClassifyId.eq(payClassify.payClassifyId))
      .leftJoin(recruitment)
      .on(pointHistory.recruit.id.eq(recruitment.id))
      ;

  }

  private <T> JPAQuery<T> applyPaymentJoin(JPAQuery<T> query) {
    return query
      .leftJoin(member)
      .on(payment.member.id.eq(member.id))
      .leftJoin(product)
      .on(payment.product.productId.eq(product.productId))
      .join(pointHistory)
      .on(
        pointHistory.payment.paymentId.eq(payment.paymentId),
        pointHistory.pType.equalsIgnoreCase("CHARGE")
      )
      ;

  }

  private <T> JPAQuery<T> applyUseJoin(JPAQuery<T> query) {
    return query
      .leftJoin(member)
      .on(pointHistory.member.id.eq(member.id))
      .leftJoin(payment)
      .on(pointHistory.payment.paymentId.eq(payment.paymentId))
      .leftJoin(recruitment)
      .on(pointHistory.recruit.id.eq(recruitment.id))
      .leftJoin(payClassify)
      .on(pointHistory.payClassify.payClassifyId.eq(payClassify.payClassifyId))
      ;

  }

  private <T> JPAQuery<T> applyRefundJoin(JPAQuery<T> query) {
    return query
      .leftJoin(member)
      .on(refund.member.id.eq(member.id))
      .leftJoin(payClassify)
      .on(refund.payClassify.payClassifyId.eq(payClassify.payClassifyId));
  }


  // #################### GROUP BY 적용 메서드 ####################

  private <T> JPAQuery<T> applySummaryGroupBy(JPAQuery<T> query) {
    return query
      .groupBy(
        payClassify.payClassifyId,
        payClassify.createdAt,
        payClassify.member.id,
        payClassify.member.username,
        payClassify.member.name,
        pointHistory.pType,
        recruitment.id,
        recruitment.title
      );
  }

  // #################### KEYWORD FILTER 적용 메서드 ####################

  private BooleanBuilder applySummaryKeywordFilter(List<String> keywordList,
                                                   SummaryKeywordType keywordType,
                                                   KeywordCriteria keywordCriteria) {

    if (keywordList == null || keywordList.isEmpty()) return null;
    List<SummaryKeywordType> types =
      keywordType == null
        ? List.of(SummaryKeywordType.values())
        : List.of(keywordType);

    BooleanBuilder result = new BooleanBuilder();

    BiFunction<StringExpression, String, BooleanExpression> keywordCheck =
      getKeywordCheckFunction(keywordCriteria);

    for (String keyword : keywordList) {
      BooleanBuilder keywordResult = new BooleanBuilder();
      for (SummaryKeywordType type : types) {
        switch (type) {
          case RECORD_ID -> {
            StringExpression payClassifyId = Expressions.stringTemplate("STR({0})", payClassify.payClassifyId);
            keywordResult.or(keywordCheck.apply(payClassifyId, keyword));
          }
          case PAYMENT_ID -> {
            StringExpression paymentId = Expressions.stringTemplate("STR({0})", pointHistory.payment.paymentId);
            keywordResult.or(keywordCheck.apply(paymentId, keyword));
          }
          case POINT_HISTORY_ID -> {
            StringExpression pointHistoryId = Expressions.stringTemplate("STR({0})", pointHistory.pointHistoryId);
            keywordResult.or(keywordCheck.apply(pointHistoryId, keyword));
          }
          case REFUND_ID -> {
            StringExpression refundId = Expressions.stringTemplate("STR({0})", refund.refundId);
            keywordResult.or(
              JPAExpressions
                .selectOne()
                .from(refund)
                .where(
                  refund.payClassify.payClassifyId.eq(payClassify.payClassifyId),
                  keywordCheck.apply(refundId, keyword)
                )
                .exists()
            );
          }
          case MEMBER_ID -> {
            StringExpression memberId = Expressions.stringTemplate("STR({0})", member.id);
            keywordResult.or(keywordCheck.apply(memberId, keyword));
          }
          case MEMBER_USERNAME -> keywordResult.or(keywordCheck.apply(member.username, keyword));
          case MEMBER_NAME -> keywordResult.or(keywordCheck.apply(member.name, keyword));
          case RECRUITMENT_ID -> {
            StringExpression recruitmentId = Expressions.stringTemplate("STR({0})", recruitment.id);
            keywordResult.or(keywordCheck.apply(recruitmentId, keyword));
          }
          case RECRUITMENT_TITLE -> keywordResult.or(keywordCheck.apply(recruitment.title, keyword));
        }
      }
      result.and(keywordResult);
    }
    return result;


  }


  private BooleanBuilder applyChargeKeywordFilter(List<String> keywordList,
                                                  ChargeKeywordType keywordType,
                                                  KeywordCriteria keywordCriteria) {

    if (keywordList == null || keywordList.isEmpty()) return null;
    List<ChargeKeywordType> types =
      keywordType == null
        ? List.of(ChargeKeywordType.values())
        : List.of(keywordType);

    BooleanBuilder result = new BooleanBuilder();

    BiFunction<StringExpression, String, BooleanExpression> keywordCheck =
      getKeywordCheckFunction(keywordCriteria);

    for (String keyword : keywordList) {
      BooleanBuilder keywordResult = new BooleanBuilder();
      for (ChargeKeywordType type : types) {
        switch (type) {
          case RECORD_ID -> {
            StringExpression payClassifyId = Expressions.stringTemplate("STR({0})", payClassify.payClassifyId);
            keywordResult.or(keywordCheck.apply(payClassifyId, keyword));
          }
          case PAYMENT_ID -> {
            StringExpression paymentId = Expressions.stringTemplate("STR({0})", payment.paymentId);
            keywordResult.or(keywordCheck.apply(paymentId, keyword));
          }
          case MEMBER_ID -> {
            StringExpression memberId = Expressions.stringTemplate("STR({0})", member.id);
            keywordResult.or(keywordCheck.apply(memberId, keyword));
          }
          case MEMBER_USERNAME -> keywordResult.or(keywordCheck.apply(member.username, keyword));
          case MEMBER_NAME -> keywordResult.or(keywordCheck.apply(member.name, keyword));
          case PRODUCT_ID -> {
            StringExpression productId = Expressions.stringTemplate("STR({0})", payment.product.productId);
            keywordResult.or(keywordCheck.apply(productId, keyword));
          }
          case PRODUCT_NAME -> keywordResult.or(keywordCheck.apply(product.productName, keyword));
          case PAYMENT_KEY -> keywordResult.or(keywordCheck.apply(payment.paymentKey, keyword));
          case ORDER_ID -> keywordResult.or(keywordCheck.apply(payment.orderId, keyword));
        }
      }
      result.and(keywordResult);
    }
    return result;

  }

  private BooleanBuilder applyUseKeywordFilter(List<String> keywordList,
                                               UseKeywordType keywordType,
                                               KeywordCriteria keywordCriteria) {

    if (keywordList == null || keywordList.isEmpty()) return null;
    List<UseKeywordType> types =
      keywordType == null
        ? List.of(UseKeywordType.values())
        : List.of(keywordType);

    BooleanBuilder result = new BooleanBuilder();

    BiFunction<StringExpression, String, BooleanExpression> keywordCheck =
      getKeywordCheckFunction(keywordCriteria);

    for (String keyword : keywordList) {
      BooleanBuilder keywordResult = new BooleanBuilder();
      for (UseKeywordType type : types) {
        switch (type) {
          case RECORD_ID -> {
            StringExpression payClassifyId = Expressions.stringTemplate("STR({0})", payClassify.payClassifyId);
            keywordResult.or(keywordCheck.apply(payClassifyId, keyword));
          }
          case POINT_HISTORY_ID -> {
            StringExpression pointHistoryId = Expressions.stringTemplate("STR({0})", pointHistory.pointHistoryId);
            keywordResult.or(keywordCheck.apply(pointHistoryId, keyword));
          }
          case MEMBER_ID -> {
            StringExpression memberId = Expressions.stringTemplate("STR({0})", member.id);
            keywordResult.or(keywordCheck.apply(memberId, keyword));
          }
          case MEMBER_USERNAME -> keywordResult.or(keywordCheck.apply(member.username, keyword));
          case MEMBER_NAME -> keywordResult.or(keywordCheck.apply(member.name, keyword));
          case RECRUITMENT_ID -> {
            StringExpression recruitmentId = Expressions.stringTemplate("STR({0})", pointHistory.recruit.id);
            keywordResult.or(keywordCheck.apply(recruitmentId, keyword));
          }
          case RECRUITMENT_TITLE -> keywordResult.or(keywordCheck.apply(pointHistory.recruit.title, keyword));
        }
      }
      result.and(keywordResult);
    }
    return result;

  }

  private BooleanBuilder applyRefundKeywordFilter(List<String> keywordList,
                                                  RefundKeywordType keywordType,
                                                  KeywordCriteria keywordCriteria) {

    if (keywordList == null || keywordList.isEmpty()) return null;
    List<RefundKeywordType> types =
      keywordType == null
        ? List.of(RefundKeywordType.values())
        : List.of(keywordType);

    BooleanBuilder result = new BooleanBuilder();

    BiFunction<StringExpression, String, BooleanExpression> keywordCheck =
      getKeywordCheckFunction(keywordCriteria);

    for (String keyword : keywordList) {
      BooleanBuilder keywordResult = new BooleanBuilder();
      for (RefundKeywordType type : types) {
        switch (type) {
          case REFUND_ID -> {
            StringExpression refundId = Expressions.stringTemplate("STR({0})", refund.refundId);
            keywordResult.or(keywordCheck.apply(refundId, keyword));
          }
          case RECORD_ID -> {
            StringExpression payClassifyId = Expressions.stringTemplate("STR({0})", payClassify.payClassifyId);
            keywordResult.or(keywordCheck.apply(payClassifyId, keyword));
          }
          case MEMBER_ID -> {
            StringExpression memberId = Expressions.stringTemplate("STR({0})", member.id);
            keywordResult.or(keywordCheck.apply(memberId, keyword));
          }
          case MEMBER_USERNAME -> keywordResult.or(keywordCheck.apply(member.username, keyword));
          case MEMBER_NAME -> keywordResult.or(keywordCheck.apply(member.name, keyword));
        }
      }
      result.and(keywordResult);
    }
    return result;

  }

  private BooleanBuilder applyProductKeywordFilter(List<String> keywordList,
                                                   ProductKeywordType keywordType,
                                                   KeywordCriteria keywordCriteria) {

    if (keywordList == null || keywordList.isEmpty()) return null;
    List<ProductKeywordType> types =
      keywordType == null
        ? List.of(ProductKeywordType.values())
        : List.of(keywordType);

    BooleanBuilder result = new BooleanBuilder();

    BiFunction<StringExpression, String, BooleanExpression> keywordCheck =
      getKeywordCheckFunction(keywordCriteria);

    for (String keyword : keywordList) {
      BooleanBuilder keywordResult = new BooleanBuilder();
      for (ProductKeywordType type : types) {
        switch (type) {
          case PRODUCT_ID -> {
            StringExpression productId = Expressions.stringTemplate("STR({0})", product.productId);
            keywordResult.or(keywordCheck.apply(productId, keyword));
          }
          case PRODUCT_NAME -> keywordResult.or(keywordCheck.apply(product.productName, keyword));
        }
      }
      result.and(keywordResult);
    }
    return result;

  }

  // #################### DATE RANGE FILTER 적용 메서드 ####################

  private BooleanBuilder applySummaryDateRangeFilter(SummaryDateRangeType dateRangeType,
                                                     LocalDateTime startDateTime,
                                                     LocalDateTime endDateTime) {

    if (dateRangeType == null) return null;

    BooleanBuilder result = new BooleanBuilder();

    if (dateRangeType == SummaryDateRangeType.RECORDED_AT) {
      result.and(goe(payClassify.createdAt, startDateTime));
      result.and(loe(payClassify.createdAt, endDateTime));
    }

    return result;

  }


  private BooleanBuilder applyChargeDateRangeFilter(ChargeDateRangeType dateRangeType,
                                                    LocalDateTime startDateTime,
                                                    LocalDateTime endDateTime) {

    if (dateRangeType == null) return null;

    BooleanBuilder result = new BooleanBuilder();

    switch (dateRangeType) {
      case REQUESTED_AT -> {
        result.and(goe(payment.requestedAt, startDateTime));
        result.and(loe(payment.requestedAt, endDateTime));
      }
      case APPROVED_AT -> {
        result.and(goe(payment.approvedAt, startDateTime));
        result.and(loe(payment.approvedAt, endDateTime));
      }
//      case CREATED_AT -> {
//        result.and(goe(payment.createdAt, startDateTime));
//        result.and(loe(payment.createdAt, endDateTime));
//      }
//      case UPDATED_AT -> {
//        result.and(goe(payment.updatedAt, startDateTime));
//        result.and(loe(payment.updatedAt, endDateTime));
//      }
      // 이 경우에 속하지 않는 경우 날짜 기준 필터를 사용하지 않음
    }

    return result;

  }

  private BooleanBuilder applyUseDateRangeFilter(UseDateRangeType dateRangeType,
                                                 LocalDateTime startDateTime,
                                                 LocalDateTime endDateTime) {

    if (dateRangeType == null) return null;

    BooleanBuilder result = new BooleanBuilder();

    if (dateRangeType == UseDateRangeType.USED_AT) {
      result.and(goe(pointHistory.createdAt, startDateTime));
      result.and(loe(pointHistory.createdAt, endDateTime));
    }
    return result;

  }

  private BooleanBuilder applyRefundDateRangeFilter(RefundDateRangeType dateRangeType,
                                                    LocalDateTime startDateTime,
                                                    LocalDateTime endDateTime) {

    if (dateRangeType == null) return null;

    BooleanBuilder result = new BooleanBuilder();

    switch (dateRangeType) {
      case RECORDED_AT -> {
        result.and(goe(refund.refundComplete, startDateTime));
        result.and(loe(refund.refundComplete, endDateTime));
      }
      case CANCELED_AT -> {
        result.and(goe(refund.canceledAt, startDateTime));
        result.and(loe(refund.canceledAt, endDateTime));
      }
      // 이 경우에 속하지 않는 경우 날짜 기준 필터를 사용하지 않음
    }

    return result;

  }

  private BooleanBuilder applyProductDateRangeFilter(ProductDateRangeType dateRangeType,
                                                     LocalDateTime startDateTime,
                                                     LocalDateTime endDateTime) {

    if (dateRangeType == null) return null;

    BooleanBuilder result = new BooleanBuilder();

    switch (dateRangeType) {
      case CREATED_AT -> {
        result.and(goe(product.createdAt, startDateTime));
        result.and(loe(product.createdAt, endDateTime));
      }
      case DELETED_AT -> {
        result.and(goe(product.deletedAt, startDateTime));
        result.and(loe(product.deletedAt, endDateTime));
      }
      // 이 경우에 속하지 않는 경우 날짜 기준 필터를 사용하지 않음
    }

    return result;

  }

  // #################### 테이블 상단 필터 적용 메서드 ####################

  private BooleanBuilder applyPaymentRecordTypeFilter(PaymentRecordType paymentRecordType) {
    if (paymentRecordType == null) return null;

    BooleanBuilder result = new BooleanBuilder();
    result.and(equalsIgnoreCase(pointHistory.pType, paymentRecordType.name()));
    return result;

  }

  private BooleanBuilder applyPaymentMethodFilter(PaymentMethod paymentMethod) {

    BooleanBuilder result = new BooleanBuilder();
    if (paymentMethod == null) return null;
    if (paymentMethod == PaymentMethod.NONE) result.and(payment.method.isNull());
    else result.and(eq(payment.method, paymentMethod.getLabel()));
    return result;

  }

  private BooleanBuilder applyPaymentStatusFilter(PaymentStatus paymentStatus) {

    BooleanBuilder result = new BooleanBuilder();
    if (paymentStatus == null) return null;
    result.and(eq(payment.paymentStatus, paymentStatus.getLabel()));
    return result;

  }


  private BooleanBuilder applyRefundStatusFilter(RefundStatus refundStatus) {

    BooleanBuilder result = new BooleanBuilder();
    if (refundStatus == null) return null;
    if (refundStatus == RefundStatus.CANCELED) result.and(refund.canceledAt.isNotNull());
    else result.and(equalsIgnoreCase(refund.refundStatus, refundStatus.name()));
    return result;

  }

  private BooleanBuilder applyProductStatusFilter(ProductStatus productStatus) {

    BooleanBuilder result = new BooleanBuilder();
    if (productStatus == null) return null;
    switch (productStatus) {
      case ON_SALE -> result.and(product.deletedAt.isNull());
      case SALE_ENDED -> result.and(product.deletedAt.isNotNull());
    }
    return result;

  }

  // #################### 테이블 상단 정렬 OrderSpecifier<>[] 구축 메서드 ####################

  private OrderSpecifier<?>[] getSummaryOrderSpecifierArray(SummarySortCriteria sortCriteria,
                                                            Boolean ascending) {

    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case POINT_CHANGING -> orderSpecifiers.addAll(getOrderSpecifier(order, pointHistory.pointChanging.sum()));
        case RECORDED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, payClassify.createdAt));
      }
    }
    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, payClassify.payClassifyId));
    return orderSpecifiers.toArray(new OrderSpecifier[0]);

  }

  private OrderSpecifier<?>[] getChargeOrderSpecifierArray(ChargeSortCriteria sortCriteria,
                                                           Boolean ascending) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case POINT_GET -> orderSpecifiers.addAll(getOrderSpecifier(order, product.pointGet));
        case REQUESTED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, payment.requestedAt));
        case APPROVED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, payment.approvedAt));
//        case CREATED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, payment.createdAt));
//        case UPDATED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, payment.updatedAt));
      }
    }
    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, payment.paymentId));
    return orderSpecifiers.toArray(new OrderSpecifier[0]);
  }

  private OrderSpecifier<?>[] getUseOrderSpecifierArray(UseSortCriteria sortCriteria,
                                                        Boolean ascending) {

    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case USED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, pointHistory.createdAt));
        case POINT_CHANGING -> orderSpecifiers.addAll(getOrderSpecifier(order, pointHistory.pointChanging.negate()));
      }
    }
    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, pointHistory.pointHistoryId));
    return orderSpecifiers.toArray(new OrderSpecifier[0]);

  }

  private OrderSpecifier<?>[] getRefundOrderSpecifierArray(RefundSortCriteria sortCriteria,
                                                           Boolean ascending) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case RECORDED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, refund.refundComplete));
        case CANCELED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, refund.canceledAt));
        case REFUND_AMOUNT -> orderSpecifiers.addAll(getOrderSpecifier(order, refund.refundAmount));
//        case REFUND_REAL -> orderSpecifiers.addAll(getOrderSpecifier(order, refund.refundReal));
      }
    }
    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, refund.refundId));
    return orderSpecifiers.toArray(new OrderSpecifier[0]);
  }

  private OrderSpecifier<?>[] getProductOrderSpecifierArray(ProductSortCriteria sortCriteria,
                                                            Boolean ascending) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case POINT_GET -> orderSpecifiers.addAll(getOrderSpecifier(order, product.pointGet));
        case PRODUCT_PRICE -> orderSpecifiers.addAll(getOrderSpecifier(order, product.productPrice));
        case CREATED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, product.createdAt));
        case DELETED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, product.deletedAt));
      }
    }
    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, product.productId));
    return orderSpecifiers.toArray(new OrderSpecifier[0]);
  }

}
