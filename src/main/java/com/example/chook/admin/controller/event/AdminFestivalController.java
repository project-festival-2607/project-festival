package com.example.chook.admin.controller.event;

import com.example.chook.admin.condition.event.FestivalSearchCondition;
import com.example.chook.admin.dto.event.FestivalTableDTO;
import com.example.chook.admin.entity.enums.DateRangeAutofillOption;
import com.example.chook.admin.entity.enums.festival.FestivalDateRangeType;
import com.example.chook.admin.entity.enums.festival.FestivalKeywordType;
import com.example.chook.admin.form.event.FestivalSearchForm;
import com.example.chook.admin.provider.AdminMemberInfoFieldProvider;
import com.example.chook.admin.service.AdminEventService;
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
@RequestMapping("/admin/event/festival")
@RequiredArgsConstructor
@Slf4j
public class AdminFestivalController {

  private static final int PAGINATION_SIZE = 10;
  private final AdminEventService adminEventService;
  private final AdminMemberInfoFieldProvider infoFieldProvider;

  @GetMapping
  public void loadFestivalPage(
    Model model,
    @Valid @ModelAttribute FestivalSearchForm form
  ) {
    model.addAttribute("form", form);
    model.addAttribute("keywordOptions", List.of(FestivalKeywordType.values()));
    model.addAttribute("dateRangeOptions", List.of(FestivalDateRangeType.values()));
    model.addAttribute("dateRangeAutofillOptions", List.of(DateRangeAutofillOption.values()));
  }

  @GetMapping("/result")
  public String getFestivalResultFragment(
    Model model,
    @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
    @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
    @Valid @ModelAttribute FestivalSearchForm form
  ) {

    FestivalSearchCondition condition = FestivalSearchCondition.from(form);
    log.info("condition: {}", condition);
    Page<FestivalTableDTO> page = adminEventService.getFestivalPage(pageIdx, pageSize, condition);

    model.addAttribute("page", page);
    model.addAttribute("pageSize", pageSize);
    PagingHandler<FestivalTableDTO, FestivalSearchForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);
    model.addAttribute("pageSizeOptions", List.of(10, 30, 50));

    log.info("form: {}", form);
    log.info("model: {}", model);
    return "admin/event/fragments/result/festival";
  }

}
