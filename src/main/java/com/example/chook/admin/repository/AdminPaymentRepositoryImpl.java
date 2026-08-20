package com.example.chook.admin.repository;

import com.example.chook.admin.condition.payment.ChargeSearchCondition;
import com.example.chook.admin.condition.payment.ProductSearchCondition;
import com.example.chook.admin.condition.payment.RefundSearchCondition;
import com.example.chook.admin.dto.payment.ChargeTableDTO;
import com.example.chook.admin.dto.payment.ProductTableDTO;
import com.example.chook.admin.dto.payment.RefundTableDTO;
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
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
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
import static com.example.chook.payment.entity.QProduct.product;
import static com.example.chook.payment.entity.QRefund.refund;

@Repository
@Slf4j
public class AdminPaymentRepositoryImpl implements AdminPaymentRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public AdminPaymentRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
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
      payment.updatedAt

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

  private <T> JPAQuery<T> applyPaymentJoin(JPAQuery<T> query) {
    return query
      .leftJoin(member)
      .on(payment.member.id.eq(member.id))
      .leftJoin(product)
      .on(payment.product.productId.eq(product.productId));
  }

  private <T> JPAQuery<T> applyRefundJoin(JPAQuery<T> query) {
    return query
      .leftJoin(member)
      .on(refund.member.id.eq(member.id))
      .leftJoin(payClassify)
      .on(refund.payClassify.payClassifyId.eq(payClassify.payClassifyId));
  }


  // #################### KEYWORD FILTER 적용 메서드 ####################

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
          case PAYMENT_KEY ->  keywordResult.or(keywordCheck.apply(payment.paymentKey, keyword));
          case ORDER_ID -> keywordResult.or(keywordCheck.apply(payment.orderId, keyword));
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
          case PAY_CLASSIFY_ID -> {
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
          case PRODUCT_NAME -> {
            keywordResult.or(keywordCheck.apply(product.productName, keyword));
          }
        }
      }
      result.and(keywordResult);
    }
    return result;

  }

  // #################### DATE RANGE FILTER 적용 메서드 ####################

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

  private OrderSpecifier<?>[] getRefundOrderSpecifierArray(RefundSortCriteria sortCriteria,
                                                           Boolean ascending) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case RECORDED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, refund.refundComplete));
        case CANCELED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, refund.canceledAt));
        case REFUND_AMOUNT -> orderSpecifiers.addAll(getOrderSpecifier(order, refund.refundAmount));
        case REFUND_REAL -> orderSpecifiers.addAll(getOrderSpecifier(order, refund.refundReal));
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
