package com.example.chook.admin.repository;

import com.example.chook.admin.condition.payment.RefundSearchCondition;
import com.example.chook.admin.dto.payment.RefundTableDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminPaymentRepository {

  Page<RefundTableDTO> getRefundPage(Pageable pageable, RefundSearchCondition condition);

}
