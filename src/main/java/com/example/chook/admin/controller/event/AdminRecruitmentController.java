package com.example.chook.admin.controller.event;

import com.example.chook.admin.condition.event.RecruitmentSearchCondition;
import com.example.chook.admin.dto.event.RecruitmentTableDTO;
import com.example.chook.admin.enums.DateRangeAutofillOption;
import com.example.chook.admin.enums.event.festival.FestivalDateRangeType;
import com.example.chook.admin.enums.event.festival.FestivalKeywordType;
import com.example.chook.admin.form.event.RecruitmentSearchForm;
import com.example.chook.admin.provider.ModalInfoFieldProvider;
import com.example.chook.admin.service.AdminEventService;
import com.example.chook.common.handler.PagingHandler;
import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
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
@RequestMapping("/admin/event/recruitment")
@RequiredArgsConstructor
@Slf4j
public class AdminRecruitmentController {

  private static final int PAGINATION_SIZE = 10;
  private final AdminEventService adminEventService;
  private final ModalInfoFieldProvider infoFieldProvider;

  @GetMapping
  public void loadFestivalPage(
    Model model,
    @Valid @ModelAttribute RecruitmentSearchForm form
  ) {
    model.addAttribute("form", form);
    model.addAttribute("keywordOptions", List.of(FestivalKeywordType.values()));
    model.addAttribute("dateRangeOptions", List.of(FestivalDateRangeType.values()));
    model.addAttribute("dateRangeAutofillOptions", List.of(DateRangeAutofillOption.values()));
  }

  @GetMapping("/result")
  public String getRecruitmentResultFragment(
    Model model,
    @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
    @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
    @Valid @ModelAttribute RecruitmentSearchForm form
  ) {

    RecruitmentSearchCondition condition = RecruitmentSearchCondition.from(form);
    String sidoCode = condition.sidoCode();
    Page<RecruitmentTableDTO> page = adminEventService.getRecruitmentPage(pageIdx, pageSize, condition);

    model.addAttribute("page", page);
    model.addAttribute("pageSize", pageSize);
    PagingHandler<RecruitmentTableDTO, RecruitmentSearchForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);
    model.addAttribute("pageSizeOptions", List.of(10, 30, 50));

    // thead status dropdown용
    model.addAttribute("recruitmentCategoryList", List.of(RecruitmentCategory.values()));
    model.addAttribute("recruitmentStatusList", List.of(RecruitmentStatus.values()));
    model.addAttribute("regionSidoList", adminEventService.getSidoOptionList());
    model.addAttribute("regionSigunguList", adminEventService.getSigunguOptionListFromSidoCode(sidoCode));

    log.info("form: {}", form);
    log.info("model: {}", model);
    log.info("page: {}", page.getContent());
    return "admin/event/fragments/result/recruitment";
  }

}
