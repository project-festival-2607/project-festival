package com.example.chook.admin.repository;

import com.example.chook.admin.condition.payment.*;
import com.example.chook.admin.dto.payment.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminPaymentRepository {

  Page<SummaryTableDTO> getSummaryPage(Pageable pageable, SummarySearchCondition condition);

  Page<ChargeTableDTO> getChargePage(Pageable pageable, ChargeSearchCondition condition);

  Page<UseTableDTO> getUsePage(Pageable pageable, UseSearchCondition condition);

  Page<RefundTableDTO> getRefundPage(Pageable pageable, RefundSearchCondition condition);

  Page<ProductTableDTO> getProductPage(Pageable pageable, ProductSearchCondition condition);

}
