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

  // ponytail: 검색/필터 리스트(2번 작업)는 나중에 이 메서드를 다시 "/list"에 매핑해서 완성할 것.
  // 지금은 /recruit/list를 festivals 화면으로 임시 연결해뒀기 때문에 라우팅을 비워둠.
  public void searchListDraft(Model model,
                              @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
                              @Valid @ModelAttribute RecruitmentSearchForm form,
                              BindingResult bindingResult) {
    if (bindingResult.hasErrors()) return;
    if (form.workingStartDate().isAfter(
      form.workingEndDate()))
      bindingResult.rejectValue("workingEndDate",
        "workingEndDate.outOfRange",
        "업무시작날짜는 업무종료날짜보다 늦을 수 없습니다."
      );
    if (bindingResult.hasErrors()) return;
    RecruitmentSearchCondition condition = mapper.toCondition(form);
    Page<RecruitmentListDTO> page = recruitmentService.getPage(pageIdx, condition);
    model.addAttribute("page", page);

    PagingHandler<RecruitmentListDTO, RecruitmentSearchForm> pagingHandler =
      new PagingHandler<>(page, pageIdx, form);
    model.addAttribute("pagingHandler", pagingHandler);
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
    return "recruit/detail";
  }

  // 헤더 "모집공고"/"축제인력모집" 진입점: 구인공고를 올릴 행사를 고르는 화면
  // ponytail: 검색/필터가 붙은 진짜 리스트(2번 작업)가 완성되면 이 화면 대신 그걸 "/list"에 연결할 것
  @GetMapping("/list")
  public void list(Model model,
                   // ponytail: 로그인 구현 전 임시 - admin_board의 ?admin=true와 동일하게 쿼리스트링으로 구인자 여부 확인
                   @RequestParam(name = "recruiter", required = false, defaultValue = "false") boolean recruiter) {
    List<Festival> festivals = festivalRepository.findAll();
    model.addAttribute("festivals", festivals);
    model.addAttribute("recruiter", recruiter);
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
