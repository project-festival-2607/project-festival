package com.example.chook.recruitment.controller;

import com.example.chook.festival.Festival;
import com.example.chook.festival.FestivalRepository;
import com.example.chook.file.dto.FileDTO;
import com.example.chook.file.record.FileResource;
import com.example.chook.file.service.FileService;
import com.example.chook.recruitment.dto.RecruitmentCreateDTO;
import com.example.chook.recruitment.dto.RecruitmentListDTO;
import com.example.chook.recruitment.dto.RecruitmentManagementListDTO;
import com.example.chook.recruitment.dto.RecruitmentResponseDTO;
import com.example.chook.recruitment.form.RecruitmentCreateForm;
import com.example.chook.recruitment.form.RecruitmentManagementSearchForm;
import com.example.chook.recruitment.form.RecruitmentSearchForm;
import com.example.chook.recruitment.handler.PagingHandler;
import com.example.chook.recruitment.mapper.RecruitmentMapper;
import com.example.chook.recruitment.record.RecruitmentSearchCondition;
import com.example.chook.recruitment.service.RecruitmentService;
import com.example.chook.region.dto.RegionDTO;
import com.example.chook.region.service.RegionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/recruitment/*")
@RequiredArgsConstructor
@Slf4j
public class RecruitmentController {

  // ponytail: 폴더명으로 못 쓰는 문자만 제거, admin_board와 동일한 규칙
  private static final Pattern INVALID_FOLDER_CHARS = Pattern.compile("[\\\\/:*?\"<>|]");

  private final RecruitmentService recruitmentService;
  private final RecruitmentMapper mapper;
  private final FestivalRepository festivalRepository;
  private final RegionService regionService;
  private final FileService fileService;

  // 헤더 "모집공고"/"축제인력모집" 진입점. 구직자/비로그인은 검색 필터가 붙은 리스트,
  // 구인자는 검색 없이 "구인공고 등록" 버튼만 보이는 동일한 리스트를 봄 (recruiter 쿼리스트링으로 임시 구분)
  @GetMapping("/list")
  public void list(Model model,
                   @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
                   @Valid @ModelAttribute RecruitmentSearchForm form,
                   BindingResult bindingResult,
                   // ponytail: 로그인 구현 전 임시 - admin_board의 ?admin=true와 동일하게 쿼리스트링으로 구인자 여부 확인
                   @RequestParam(name = "recruiter", required = false, defaultValue = "false") boolean recruiter) {
    if (form.workingStartDate() != null && form.workingEndDate() != null
      && form.workingStartDate().isAfter(form.workingEndDate()))
      bindingResult.rejectValue("workingEndDate",
        "workingEndDate.outOfRange",
        "근무시작날짜는 근무종료날짜보다 늦을 수 없습니다."
      );
    if (bindingResult.hasErrors()) return;

    RecruitmentSearchCondition condition = mapper.toCondition(form);
    Page<RecruitmentListDTO> page = recruitmentService.getPage(pageIdx, condition);
    model.addAttribute("page", page);

    PagingHandler<RecruitmentListDTO, RecruitmentSearchForm> pagingHandler =
      new PagingHandler<>(page, pageIdx, form);
    model.addAttribute("pagingHandler", pagingHandler);

    model.addAttribute("recruiter", recruiter);
    model.addAttribute("sidoList", regionService.getSidoList());
  }

  @GetMapping("/manage/list")
  public void manageList(Model model,
                         @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
                         @Valid @ModelAttribute RecruitmentManagementSearchForm form,
                         BindingResult bindingResult) {
    if (bindingResult.hasErrors()) return;
    RecruitmentSearchCondition condition = mapper.toCondition(form);
    Page<RecruitmentManagementListDTO> page = recruitmentService.getManagementPage(pageIdx, condition);
    model.addAttribute("page", page);

    PagingHandler<RecruitmentManagementListDTO, RecruitmentManagementSearchForm> pagingHandler =
      new PagingHandler<>(page, pageIdx, form);
    model.addAttribute("pagingHandler", pagingHandler);

  }

  @GetMapping("/{id}")
  public String view(@PathVariable Long id, Model model) {
    RecruitmentResponseDTO responseDto = recruitmentService.getRecruitment(id);
    model.addAttribute("recruitment", responseDto);
    return "recruitment/detail";
  }

  @GetMapping("/register")
  public void register(Model model) {
    List<Festival> festivals = festivalRepository.findAll();
    List<RegionDTO> sidoList = regionService.getSidoList();
    model.addAttribute("festivals", festivals);
    model.addAttribute("sidoList", sidoList);
  }

  // 에디터에 이미지를 삽입하는 시점에 비동기로 호출됨 (admin_board와 동일한 패턴)
  @PostMapping("/uploadImage")
  @ResponseBody
  public ResponseEntity<FileDTO> uploadImage(
    @RequestParam("image") MultipartFile image,
    @RequestParam("title") String title
  ) {
    String relativePath = "recruit/" + toFolderName(title);
    FileDTO fileDto = fileService.toDto(fileService.upload(image, relativePath));
    log.info("recruit image uploaded: {}", fileDto);
    return ResponseEntity.ok(fileDto);
  }

  @GetMapping("/image/{uuid}")
  @ResponseBody
  public ResponseEntity<Resource> getImage(@PathVariable UUID uuid) {
    FileResource file = fileService.getFile(uuid);
    return ResponseEntity.ok()
      .contentType(MediaType.parseMediaType(file.mimeType()))
      .body(file.resource());
  }

  private String toFolderName(String title) {
    String trimmed = title == null ? "" : title.trim();
    return trimmed.isEmpty()
      ? "untitled"
      : INVALID_FOLDER_CHARS.matcher(trimmed).replaceAll("_");
  }

  // 검증 실패로 등록 폼을 다시 보여줄 때, GET /register에서 채우던 드롭다운 데이터를 다시 채움
  private String registerFormWithReloadedOptions(Model model) {
    model.addAttribute("festivals", festivalRepository.findAll());
    model.addAttribute("sidoList", regionService.getSidoList());
    model.addAttribute("hasError", true);
    return "recruitment/register";
  }

  @PostMapping("/register")
  public String register(@Valid @ModelAttribute RecruitmentCreateForm recruitmentCreateForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) return registerFormWithReloadedOptions(model);
    if (recruitmentCreateForm.workingStartDate().isAfter(
      recruitmentCreateForm.workingEndDate()))
      bindingResult.rejectValue("workingEndDate",
        "workingEndDate.outOfRange",
        "업무시작날짜는 업무종료날짜보다 늦을 수 없습니다."
      );
    if (bindingResult.hasErrors()) return registerFormWithReloadedOptions(model);
    RecruitmentCreateDTO recruitmentCreateDTO = mapper.toCreateDto(recruitmentCreateForm);
    Long recruitmentId = recruitmentService.createRecruitment(recruitmentCreateDTO);

    redirectAttributes.addAttribute("id", recruitmentId);
    redirectAttributes.addFlashAttribute("successMsg", "공고 초안이 등록되었습니다.");

    return "redirect:/recruitment/{id}";
  }





}
