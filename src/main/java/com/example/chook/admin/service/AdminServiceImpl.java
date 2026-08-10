package com.example.chook.admin.service;

import com.example.chook.festival.FestivalRepository;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.payment.repository.PaymentRepository;
import com.example.chook.payment.repository.PointCalcUseRepository;
import com.example.chook.payment.repository.PointHistoryRepository;
import com.example.chook.payment.repository.ProductRepository;
import com.example.chook.support.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

  private final MemberRepository memberRepository;
  private final InquiryRepository inquiryRepository;
  private final PaymentRepository paymentRepository;
  private final PointCalcUseRepository pointCalcUseRepository;
  private final PointHistoryRepository pointHistoryRepository;
  private final ProductRepository productRepository;
  private final FestivalRepository festivalRepository;


}
