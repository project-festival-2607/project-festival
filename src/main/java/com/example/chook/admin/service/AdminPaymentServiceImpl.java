package com.example.chook.admin.service;

import com.example.chook.admin.condition.payment.ChargeSearchCondition;
import com.example.chook.admin.condition.payment.ProductSearchCondition;
import com.example.chook.admin.condition.payment.RefundSearchCondition;
import com.example.chook.admin.condition.payment.UseSearchCondition;
import com.example.chook.admin.dto.payment.ChargeTableDTO;
import com.example.chook.admin.dto.payment.ProductTableDTO;
import com.example.chook.admin.dto.payment.RefundTableDTO;
import com.example.chook.admin.dto.payment.UseTableDTO;
import com.example.chook.admin.enums.payment.charge.PaymentMethod;
import com.example.chook.admin.enums.payment.charge.PaymentStatus;
import com.example.chook.admin.enums.payment.product.ProductStatus;
import com.example.chook.admin.enums.payment.refund.RefundStatus;
import com.example.chook.admin.repository.AdminPaymentRepository;
import com.example.chook.payment.entity.Product;
import com.example.chook.payment.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.chook.common.util.CustomStringUtils.toEnum;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminPaymentServiceImpl implements AdminPaymentService {

  private final AdminPaymentRepository adminPaymentRepository;
  private final ProductRepository productRepository;

  @Override
  public Page<ChargeTableDTO> getChargePage(int pageIdx, int pageSize, ChargeSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    Page<ChargeTableDTO> result = adminPaymentRepository.getChargePage(pageable, condition);
    result.forEach(dto -> {
      dto.setPaymentMethod(PaymentMethod.fromLabel(dto.getRawPaymentMethod()));
      dto.setPaymentStatus(toEnum(dto.getRawPaymentStatus(), PaymentStatus.class, null));
    });
    return result;
  }

  @Override
  public Page<UseTableDTO> getUsePage(int pageIdx, int pageSize, UseSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    return adminPaymentRepository.getUsePage(pageable, condition);
  }

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

  @Transactional
  @Override
  public boolean deleteProduct(Integer productId) {
    Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("해당 상품이 없습니다."));
    if (product.getDeletedAt() != null) return false;
    product.setDeletedAt(LocalDateTime.now());
    return true;
  }

  @Transactional
  @Override
  public Integer addProduct(String productName, Integer productPointGet) {
    Product product = productRepository.save(
      Product.builder()
        .pointGet(productPointGet)
        .productPrice(productPointGet)
        .productName(productName)
        .build()
    );

    return product.getProductId();
  }
}
