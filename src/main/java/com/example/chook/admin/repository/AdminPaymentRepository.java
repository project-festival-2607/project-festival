package com.example.chook.admin.repository;

import com.example.chook.admin.condition.payment.ChargeSearchCondition;
import com.example.chook.admin.condition.payment.ProductSearchCondition;
import com.example.chook.admin.condition.payment.RefundSearchCondition;
import com.example.chook.admin.dto.payment.ChargeTableDTO;
import com.example.chook.admin.dto.payment.ProductTableDTO;
import com.example.chook.admin.dto.payment.RefundTableDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminPaymentRepository {

  Page<ChargeTableDTO> getChargePage(Pageable pageable, ChargeSearchCondition condition);

  Page<RefundTableDTO> getRefundPage(Pageable pageable, RefundSearchCondition condition);

  Page<ProductTableDTO> getProductPage(Pageable pageable, ProductSearchCondition condition);

}
