package com.example.chook.admin.enums.payment.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum PaymentMethod {

  // 2026년 8월 20일자 토스페이먼츠 코어 API 문서를 기반으로 작성됨.

  NONE("없음"),
  CARD("카드"),
  VIRTUAL_ACCOUNT("가상계좌"),
  EASY_PAYMENT("간편결제"),
  MOBILE_PAYMENT("휴대폰"),
  BANK_TRANSFER("계좌이체"),
  CULTURE_GIFT_CERTIFICATE("문화상품권"),
  BOOK_CULTURE_GIFT_CERTIFICATE("도서문화상품권"),
  GAME_CULTURE_GIFT_CERTIFICATE("게임문화상품권");

  private final String label;

  public static PaymentMethod fromLabel(String label) {
    return Arrays.stream(values())
      .filter(e -> e.label.equals(label))
      .findFirst()
      .orElse(null);
  }

}
