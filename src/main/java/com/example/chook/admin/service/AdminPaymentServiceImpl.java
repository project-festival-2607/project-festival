package com.example.chook.admin.service;

import com.example.chook.admin.condition.payment.ProductSearchCondition;
import com.example.chook.admin.condition.payment.RefundSearchCondition;
import com.example.chook.admin.dto.board.InquiryTableDTO;
import com.example.chook.admin.dto.payment.ProductTableDTO;
import com.example.chook.admin.dto.payment.RefundTableDTO;
import com.example.chook.admin.enums.board.inquiry.InquiryReplyStatus;
import com.example.chook.admin.enums.payment.product.ProductStatus;
import com.example.chook.admin.enums.payment.refund.RefundStatus;
import com.example.chook.admin.repository.AdminPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminPaymentServiceImpl implements AdminPaymentService {

  private final AdminPaymentRepository adminPaymentRepository;

  @Override
  public Page<RefundTableDTO> getRefundPage(int pageIdx, int pageSize, RefundSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    Page<RefundTableDTO> result = adminPaymentRepository.getRefundPage(pageable, condition);
    result.forEach(dto -> {
      if (dto.getCanceledAt() != null) dto.setStatus(RefundStatus.CANCELED);
      if (dto.getRawStatus().equalsIgnoreCase("REQUESTED")) dto.setStatus(RefundStatus.REQUESTED);
      if (dto.getRawStatus().equalsIgnoreCase("PARTIALLY_FAILED")) dto.setStatus(RefundStatus.PARTIALLY_FAILED);
      if (dto.getRawStatus().equalsIgnoreCase("COMPLETED")) dto.setStatus(RefundStatus.COMPLETED);
    });
    return result;
  }

  @Override
  public Page<ProductTableDTO> getProductPage(int pageIdx, int pageSize, ProductSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    Page<ProductTableDTO> result = adminPaymentRepository.getProductPage(pageable, condition);
    result.forEach(dto -> {
      if (dto.getDeletedAt() == null) dto.setStatus(ProductStatus.ON_SALE);
      if (dto.getDeletedAt() != null) dto.setStatus(ProductStatus.SALE_ENDED);
    });
    return result;
  }
}
