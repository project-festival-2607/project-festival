package com.example.chook.admin.controller.payment;

import com.example.chook.admin.condition.payment.ProductSearchCondition;
import com.example.chook.admin.dto.payment.ProductTableDTO;
import com.example.chook.admin.enums.DateRangeAutofillOption;
import com.example.chook.admin.enums.payment.product.ProductDateRangeType;
import com.example.chook.admin.enums.payment.product.ProductKeywordType;
import com.example.chook.admin.enums.payment.product.ProductStatus;
import com.example.chook.admin.form.payment.ProductSearchForm;
import com.example.chook.admin.provider.ModalInfoFieldProvider;
import com.example.chook.admin.record.AdminActionResponse;
import com.example.chook.admin.service.AdminPaymentService;
import com.example.chook.common.handler.PagingHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/payment/product")
@RequiredArgsConstructor
@Slf4j
public class AdminProductController {

  private static final int PAGINATION_SIZE = 10;
  private final AdminPaymentService adminPaymentService;
  private final ModalInfoFieldProvider infoFieldProvider;

  @GetMapping
  public void loadRefundPage(
    Model model,
    @Valid @ModelAttribute ProductSearchForm form
  ) {
    model.addAttribute("form", form);
    model.addAttribute("keywordOptions", List.of(ProductKeywordType.values()));
    model.addAttribute("dateRangeOptions", List.of(ProductDateRangeType.values()));
    model.addAttribute("dateRangeAutofillOptions", List.of(DateRangeAutofillOption.values()));

    // MODAL에 표시할 정보 특정용 attribute
    model.addAttribute("deleteProductInfo", infoFieldProvider.deleteProduct());
  }

  @GetMapping("/result")
  public String getProductResultFragment(
    Model model,
    @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
    @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
    @Valid @ModelAttribute ProductSearchForm form
  ) {

    ProductSearchCondition condition = ProductSearchCondition.from(form);
    log.info("condition: {}", condition);
    Page<ProductTableDTO> page = adminPaymentService.getProductPage(pageIdx, pageSize, condition);

    model.addAttribute("page", page);
    model.addAttribute("pageSize", pageSize);
    PagingHandler<ProductTableDTO, ProductSearchForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);
    model.addAttribute("pageSizeOptions", List.of(10, 30, 50));

    // thead status dropdown용
    model.addAttribute("productStatusList", List.of(ProductStatus.values()));

    log.info("form: {}", form);
    log.info("condition: {}", condition);
    log.info("model: {}", model);
    log.info("result: {}", page.getContent());
    return "admin/payment/fragments/result/product";
  }

  @PostMapping("/{productId}/delete")
  @ResponseBody
  public AdminActionResponse deleteProduct(@PathVariable Integer productId) {

    boolean isChanged = adminPaymentService.deleteProduct(productId);
    return AdminActionResponse.builder()
      .result(isChanged)
      .message(isChanged
        ? String.format("productId가 [%s]인 상품을 삭제 처리했습니다.", productId)
        : "해당 상품은 이미 삭제 처리되어 있습니다. ")
      .build()
      ;

  }

}
