package com.example.chook.payment.service;

import com.example.chook.payment.entity.PayClassify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayClassifyService {
  public PayClassify createTransaction(Long memberId, String charge) {
    return PayClassify.builder().build();
  }
}
