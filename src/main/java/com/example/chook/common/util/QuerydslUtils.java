package com.example.chook.common.util;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.SimpleExpression;

public final class QuerydslUtils {

  public static <T> BooleanExpression eq(SimpleExpression<T> path, T value) {
    return value == null ? null : path.eq(value);
  }

  public static <T extends Comparable<? super T>> BooleanExpression gt(ComparableExpression<T> path, T value) {
    return value == null ? null : path.gt(value);
  }

  public static <T extends Comparable<? super T>> BooleanExpression goe(ComparableExpression<T> path, T value) {
    return value == null ? null : path.goe(value);
  }

  public static <T extends Comparable<? super T>> BooleanExpression loe(ComparableExpression<T> path, T value) {
    return value == null ? null : path.loe(value);
  }

  public static <T extends Comparable<? super T>> BooleanExpression lt(ComparableExpression<T> path, T value) {
    return value == null ? null : path.lt(value);
  }

}
