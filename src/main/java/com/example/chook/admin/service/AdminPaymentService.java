package com.example.chook.admin.service;

import com.example.chook.admin.condition.payment.*;
import com.example.chook.admin.dto.payment.*;
import org.springframework.data.domain.Page;

public interface AdminPaymentService {

  Page<SummaryTableDTO> getSummaryPage(int pageIdx, int pageSize, SummarySearchCondition condition);

  Page<ChargeTableDTO> getChargePage(int pageIdx, int pageSize, ChargeSearchCondition condition);

  Page<UseTableDTO> getUsePage(int pageIdx, int pageSize, UseSearchCondition condition);

  Page<RefundTableDTO> getRefundPage(int pageIdx, int pageSize, RefundSearchCondition condition);

  Page<ProductTableDTO> getProductPage(int pageIdx, int pageSize, ProductSearchCondition condition);

  boolean deleteProduct(Integer productId);

  Integer addProduct(String productName, Integer productPointGet);

}
