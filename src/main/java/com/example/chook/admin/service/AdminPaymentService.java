package com.example.chook.admin.service;

import com.example.chook.admin.condition.payment.PaymentSearchCondition;
import com.example.chook.admin.condition.payment.ProductSearchCondition;
import com.example.chook.admin.condition.payment.RefundSearchCondition;
import com.example.chook.admin.dto.payment.PaymentTableDTO;
import com.example.chook.admin.dto.payment.ProductTableDTO;
import com.example.chook.admin.dto.payment.RefundTableDTO;
import org.springframework.data.domain.Page;

public interface AdminPaymentService {

  Page<PaymentTableDTO> getPaymentPage(int pageIdx, int pageSize, PaymentSearchCondition condition);

  Page<RefundTableDTO> getRefundPage(int pageIdx, int pageSize, RefundSearchCondition condition);

  Page<ProductTableDTO> getProductPage(int pageIdx, int pageSize, ProductSearchCondition condition);

  boolean deleteProduct(Integer productId);

  Integer addProduct(String productName, Integer productPointGet);

}
