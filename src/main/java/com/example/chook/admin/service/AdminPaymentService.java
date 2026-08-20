package com.example.chook.admin.service;

import com.example.chook.admin.condition.payment.ChargeSearchCondition;
import com.example.chook.admin.condition.payment.ProductSearchCondition;
import com.example.chook.admin.condition.payment.RefundSearchCondition;
import com.example.chook.admin.condition.payment.UseSearchCondition;
import com.example.chook.admin.dto.payment.ChargeTableDTO;
import com.example.chook.admin.dto.payment.ProductTableDTO;
import com.example.chook.admin.dto.payment.RefundTableDTO;
import com.example.chook.admin.dto.payment.UseTableDTO;
import org.springframework.data.domain.Page;

public interface AdminPaymentService {

  Page<ChargeTableDTO> getChargePage(int pageIdx, int pageSize, ChargeSearchCondition condition);

  Page<UseTableDTO> getUsePage(int pageIdx, int pageSize, UseSearchCondition condition);

  Page<RefundTableDTO> getRefundPage(int pageIdx, int pageSize, RefundSearchCondition condition);

  Page<ProductTableDTO> getProductPage(int pageIdx, int pageSize, ProductSearchCondition condition);

  boolean deleteProduct(Integer productId);

  Integer addProduct(String productName, Integer productPointGet);

}
