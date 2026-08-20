package com.example.chook.admin.service;

import com.example.chook.admin.condition.payment.ProductSearchCondition;
import com.example.chook.admin.condition.payment.RefundSearchCondition;
import com.example.chook.admin.dto.payment.ProductTableDTO;
import com.example.chook.admin.dto.payment.RefundTableDTO;
import org.springframework.data.domain.Page;

public interface AdminPaymentService {

  Page<RefundTableDTO> getRefundPage(int pageIdx, int pageSize, RefundSearchCondition condition);

  Page<ProductTableDTO> getProductPage(int pageIdx, int pageSize, ProductSearchCondition condition);

}
