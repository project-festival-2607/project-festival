package com.example.chook.admin.controller.event;

import com.example.chook.admin.condition.event.FestivalSearchCondition;
import com.example.chook.admin.dto.event.FestivalTableDTO;
import com.example.chook.admin.entity.enums.DateRangeAutofillOption;
import com.example.chook.admin.entity.enums.festival.FestivalDateRangeType;
import com.example.chook.admin.entity.enums.festival.FestivalKeywordType;
import com.example.chook.admin.form.event.FestivalSearchForm;
import com.example.chook.admin.provider.ModalInfoFieldProvider;
import com.example.chook.admin.record.AdminActionResponse;
import com.example.chook.admin.service.AdminEventService;
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
@RequestMapping("/admin/event/festival")
@RequiredArgsConstructor
@Slf4j
public class AdminFestivalController {

  private static final int PAGINATION_SIZE = 10;
  private final AdminEventService adminEventService;
  private final ModalInfoFieldProvider infoFieldProvider;

  @GetMapping
  public void loadFestivalPage(
    Model model,
    @Valid @ModelAttribute FestivalSearchForm form
  ) {
    model.addAttribute("form", form);
    model.addAttribute("keywordOptions", List.of(FestivalKeywordType.values()));
    model.addAttribute("dateRangeOptions", List.of(FestivalDateRangeType.values()));
    model.addAttribute("dateRangeAutofillOptions", List.of(DateRangeAutofillOption.values()));

    // MODAL에 표시할 정보 특정용 attribute
    model.addAttribute("assignFestivalManagerFestival", infoFieldProvider.assignFestivalManagerFestival());
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

  @PostMapping("/{contentId}/assign-manager/{memberId}")
  @ResponseBody
  public AdminActionResponse assignFestivalMember(@PathVariable String contentId,
                                                  @PathVariable Long memberId) {

    boolean isChanged = adminEventService.assignFestivalMember(contentId, memberId);
    return AdminActionResponse.builder()
      .result(isChanged)
      .message(isChanged
        ? String.format("id가 [%d]인 사용자를 contentId가 [%s]인 행사의 담당자로 지정했습니다.", memberId, contentId)
        : "이미 해당 사용자가 해당 행사의 담당자로 지정되어 있습니다.")
      .build()
      ;

  }

  @PostMapping("/{contentId}/unassign-manager")
  @ResponseBody
  public AdminActionResponse unassignFestivalMember(@PathVariable String contentId) {

    boolean isChanged = adminEventService.unassignFestivalMember(contentId);
    return AdminActionResponse.builder()
      .result(isChanged)
      .message(isChanged
        ? String.format("contentId가 [%s]인 행사의 담당자 정보를 삭제했습니다.", contentId)
        : "해당 행사의 담당자가 지정되어있지 않습니다.")
      .build()
      ;

  }

}
