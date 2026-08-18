package com.example.chook.admin.util;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.KeywordType;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerKeywordType;
import com.example.chook.common.util.QuerydslUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.StringExpression;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiFunction;

import static com.example.chook.common.util.CustomStringUtils.splitByRegex;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

public final class KeywordUtils {

  public static <T extends KeywordType> KeywordCriteria getKeywordCriteria(T keywordType, String keywordCriteriaStr) {

    if (keywordType == null) return KeywordCriteria.ALL_WORDS_CONTAINS;

    KeywordCriteria keywordCriteria = toEnum(keywordCriteriaStr, KeywordCriteria.class, KeywordCriteria.ALL_WORDS_CONTAINS);
    EnumSet<KeywordCriteria> supportedCriteria = keywordType.getSupportedCriteria();
    if (supportedCriteria.contains(keywordCriteria)) return keywordCriteria;
    return supportedCriteria.stream().max(Comparator.comparingInt(KeywordCriteria::getPriority))
      .orElse(KeywordCriteria.ALL_WORDS_CONTAINS);

  }

  public static List<String> getKeywordList(String keywords, KeywordCriteria keywordCriteria) {
    if (keywordCriteria != KeywordCriteria.ALL_WORDS_CONTAINS) return keywords == null ? List.of() : List.of(keywords);
    return splitByRegex(keywords, "[\\s,&]+");
  }

  public static BiFunction<StringExpression, String, BooleanExpression> getKeywordCheckFunction(KeywordCriteria keywordCriteria) {
    return (keywordCriteria == KeywordCriteria.EXACT)
      ? QuerydslUtils::eq
      : QuerydslUtils::contains;
  }

  public static <T extends Comparable<? super T>> List<OrderSpecifier<?>> getOrderSpecifier(
    Order order,
    ComparableExpressionBase<T> expression) {

    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    orderSpecifiers.add(
      new OrderSpecifier<>(
        Order.ASC,
        new CaseBuilder()
          .when(expression.isNull()).then(1)
          .otherwise(0)
      )
    );
    orderSpecifiers.add(new OrderSpecifier<>(order, expression));
    return orderSpecifiers;

  }

}
