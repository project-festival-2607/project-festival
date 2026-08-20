package com.example.chook.admin.controller.payment;

import com.example.chook.admin.condition.payment.UseSearchCondition;
import com.example.chook.admin.dto.payment.UseTableDTO;
import com.example.chook.admin.enums.DateRangeAutofillOption;
import com.example.chook.admin.enums.payment.product.ProductDateRangeType;
import com.example.chook.admin.enums.payment.product.ProductKeywordType;
import com.example.chook.admin.form.payment.UseSearchForm;
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
@RequestMapping("/admin/payment/use")
@RequiredArgsConstructor
@Slf4j
public class AdminUseController {

  private static final int PAGINATION_SIZE = 10;
  private final AdminPaymentService adminPaymentService;
  private final ModalInfoFieldProvider infoFieldProvider;

  @GetMapping
  public void loadUsePage(
    Model model,
    @Valid @ModelAttribute UseSearchForm form
  ) {
    model.addAttribute("form", form);
    model.addAttribute("keywordOptions", List.of(ProductKeywordType.values()));
    model.addAttribute("dateRangeOptions", List.of(ProductDateRangeType.values()));
    model.addAttribute("dateRangeAutofillOptions", List.of(DateRangeAutofillOption.values()));
  }

  @GetMapping("/result")
  public String getUseResultFragment(
    Model model,
    @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
    @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
    @Valid @ModelAttribute UseSearchForm form
  ) {

    UseSearchCondition condition = UseSearchCondition.from(form);
    log.info("condition: {}", condition);
    Page<UseTableDTO> page = adminPaymentService.getUsePage(pageIdx, pageSize, condition);

    model.addAttribute("page", page);
    model.addAttribute("pageSize", pageSize);
    PagingHandler<UseTableDTO, UseSearchForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);
    model.addAttribute("pageSizeOptions", List.of(10, 30, 50));

    log.info("result: {}", page.getContent());

    return "admin/payment/fragments/result/use";
  }

}
