package com.example.chook.admin.controller.board;

import com.example.chook.admin.condition.board.InquirySearchCondition;
import com.example.chook.admin.dto.board.AdminInquiryTableDTO;
import com.example.chook.admin.entity.enums.DateRangeAutofillOption;
import com.example.chook.admin.entity.enums.inquiry.InquiryDateRangeType;
import com.example.chook.admin.entity.enums.inquiry.InquiryKeywordType;
import com.example.chook.admin.entity.enums.inquiry.InquiryReplyStatus;
import com.example.chook.admin.form.board.InquirySearchForm;
import com.example.chook.admin.service.AdminBoardService;
import com.example.chook.common.handler.PagingHandler;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.support.entity.Inquiry;
import com.example.chook.support.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/board/inquiry")
@RequiredArgsConstructor
@Slf4j
public class AdminInquiryController {

  private static final int PAGINATION_SIZE = 10;
  private final AdminBoardService adminBoardService;
  private final InquiryService inquiryService;

  @GetMapping
  public void loadInquiryListPage(
    Model model,
    @Valid @ModelAttribute InquirySearchForm form
  ) {
    model.addAttribute("form", form);
    model.addAttribute("keywordOptions", List.of(InquiryKeywordType.values()));
    model.addAttribute("dateRangeOptions", List.of(InquiryDateRangeType.values()));
    model.addAttribute("dateRangeAutofillOptions", List.of(DateRangeAutofillOption.values()));
  }

  @GetMapping("/result")
  public String getInquiryResultFragment(
    Model model,
    @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
    @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
    @Valid @ModelAttribute InquirySearchForm form
  ) {
    InquirySearchCondition condition = InquirySearchCondition.from(form);
    log.info("condition: {}", condition);
    Page<AdminInquiryTableDTO> page = adminBoardService.getInquiryListPage(pageIdx, pageSize, condition);

    model.addAttribute("page", page);
    model.addAttribute("pageSize", pageSize);
    PagingHandler<AdminInquiryTableDTO, InquirySearchForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);
    model.addAttribute("pageSizeOptions", List.of(10, 30, 50));

    // thead status dropdown용
    model.addAttribute("inquiryReplyStatusList", List.of(InquiryReplyStatus.values()));
    model.addAttribute("memberRoleList", List.of(MemberRole.values()));

    log.info("form: {}", form);
    log.info("model: {}", model);
    return "admin/board/fragments/result/inquiry";
  }

  @GetMapping("/{ino}")
  public String adminDetail(@PathVariable Long ino, Model model) {
    model.addAttribute("inquiry", inquiryService.getDetail(ino));
    return "admin/board/inquiry-detail";
  }

  @PostMapping("/{ino}/answer")
  public String answer(@PathVariable Long ino, @RequestParam String comment) {
    Inquiry answered = inquiryService.answer(ino, comment);
    log.info("inquiry answered: {}", answered);
    return "redirect:/admin/board/inquiry";
  }
}
