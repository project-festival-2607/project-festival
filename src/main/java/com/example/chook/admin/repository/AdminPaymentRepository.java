package com.example.chook.admin.repository;

import com.example.chook.admin.condition.payment.PaymentSearchCondition;
import com.example.chook.admin.condition.payment.ProductSearchCondition;
import com.example.chook.admin.condition.payment.RefundSearchCondition;
import com.example.chook.admin.dto.payment.PaymentTableDTO;
import com.example.chook.admin.dto.payment.ProductTableDTO;
import com.example.chook.admin.dto.payment.RefundTableDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminPaymentRepository {

  Page<PaymentTableDTO> getPaymentPage(Pageable pageable, PaymentSearchCondition condition);

  Page<RefundTableDTO> getRefundPage(Pageable pageable, RefundSearchCondition condition);

  Page<ProductTableDTO> getProductPage(Pageable pageable, ProductSearchCondition condition);

}
