package com.example.chook.admin.controller.payment;

import com.example.chook.admin.condition.payment.PaymentSearchCondition;
import com.example.chook.admin.dto.payment.PaymentTableDTO;
import com.example.chook.admin.enums.DateRangeAutofillOption;
import com.example.chook.admin.enums.payment.payment.PaymentDateRangeType;
import com.example.chook.admin.enums.payment.payment.PaymentKeywordType;
import com.example.chook.admin.enums.payment.payment.PaymentMethod;
import com.example.chook.admin.enums.payment.payment.PaymentStatus;
import com.example.chook.admin.form.payment.PaymentSearchForm;
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
@RequestMapping("/admin/payment/payment")
@RequiredArgsConstructor
@Slf4j
public class AdminPaymentController {

  private static final int PAGINATION_SIZE = 10;
  private final AdminPaymentService adminPaymentService;
  private final ModalInfoFieldProvider infoFieldProvider;

  @GetMapping
  public void loadPaymentPage(
    Model model,
    @Valid @ModelAttribute PaymentSearchForm form
  ) {
    model.addAttribute("form", form);
    model.addAttribute("keywordOptions", List.of(PaymentKeywordType.values()));
    model.addAttribute("dateRangeOptions", List.of(PaymentDateRangeType.values()));
    model.addAttribute("dateRangeAutofillOptions", List.of(DateRangeAutofillOption.values()));
  }

  @GetMapping("/result")
  public String getPaymentResultFragment(
    Model model,
    @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
    @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
    @Valid @ModelAttribute PaymentSearchForm form
  ) {

    PaymentSearchCondition condition = PaymentSearchCondition.from(form);
    log.info("condition: {}", condition);
    Page<PaymentTableDTO> page = adminPaymentService.getPaymentPage(pageIdx, pageSize, condition);

    model.addAttribute("page", page);
    model.addAttribute("pageSize", pageSize);
    PagingHandler<PaymentTableDTO, PaymentSearchForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);
    model.addAttribute("pageSizeOptions", List.of(10, 30, 50));

    // thead status dropdown용
    model.addAttribute("paymentStatusList", List.of(PaymentStatus.values()));
    model.addAttribute("paymentMethodList", List.of(PaymentMethod.values()));

    log.info("form: {}", form);
    log.info("model: {}", model);
    log.info("result: {}", page.getContent());
    return "admin/payment/fragments/result/payment";
  }

}
