package com.example.chook.admin.controller.payment;

import com.example.chook.admin.condition.payment.RefundSearchCondition;
import com.example.chook.admin.dto.payment.RefundTableDTO;
import com.example.chook.admin.enums.DateRangeAutofillOption;
import com.example.chook.admin.enums.payment.refund.RefundDateRangeType;
import com.example.chook.admin.enums.payment.refund.RefundKeywordType;
import com.example.chook.admin.enums.payment.refund.RefundStatus;
import com.example.chook.admin.form.payment.RefundSearchForm;
import com.example.chook.admin.provider.ModalInfoFieldProvider;
import com.example.chook.admin.service.AdminPaymentService;
import com.example.chook.common.handler.PagingHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/payment/refund")
@RequiredArgsConstructor
@Slf4j
public class AdminRefundController {

  private static final int PAGINATION_SIZE = 10;
  private final AdminPaymentService adminPaymentService;
  private final ModalInfoFieldProvider infoFieldProvider;

  @GetMapping
  public void loadRefundPage(
    Model model,
    @Valid @ModelAttribute RefundSearchForm form
  ) {
    model.addAttribute("form", form);
    model.addAttribute("keywordOptions", List.of(RefundKeywordType.values()));
    model.addAttribute("dateRangeOptions", List.of(RefundDateRangeType.values()));
    model.addAttribute("dateRangeAutofillOptions", List.of(DateRangeAutofillOption.values()));
  }

  @GetMapping("/result")
  public String getRefundResultFragment(
    Model model,
    @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
    @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
    @Valid @ModelAttribute RefundSearchForm form
  ) {

    RefundSearchCondition condition = RefundSearchCondition.from(form);
    log.info("condition: {}", condition);
    Page<RefundTableDTO> page = adminPaymentService.getRefundPage(pageIdx, pageSize, condition);

    model.addAttribute("page", page);
    model.addAttribute("pageSize", pageSize);
    PagingHandler<RefundTableDTO, RefundSearchForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);
    model.addAttribute("pageSizeOptions", List.of(10, 30, 50));

    // thead status dropdown용
    model.addAttribute("refundStatusList", List.of(RefundStatus.values()));

    log.info("form: {}", form);
    log.info("model: {}", model);
    return "admin/payment/fragments/result/refund";
  }

}
