package com.example.chook.recruitment.controller;

import com.example.chook.bookmark.service.RecruitmentBookmarkService;
import com.example.chook.common.handler.PagingHandler;
import com.example.chook.festival.FestivalDTO;
import com.example.chook.festival.FestivalService;
import com.example.chook.file.dto.FileDTO;
import com.example.chook.file.record.FileResource;
import com.example.chook.file.service.FileService;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.security.CustomUserDetails;
import com.example.chook.recruitment.dto.*;
import com.example.chook.recruitment.form.RecruitmentCreateForm;
import com.example.chook.recruitment.form.RecruitmentManagementForm;
import com.example.chook.recruitment.form.RecruitmentSearchForm;
import com.example.chook.recruitment.mapper.RecruitmentMapper;
import com.example.chook.recruitment.record.RecruitmentManagementCondition;
import com.example.chook.recruitment.record.RecruitmentSearchCondition;
import com.example.chook.recruitment.service.RecruitmentService;
import com.example.chook.region.dto.RegionDTO;
import com.example.chook.region.service.RegionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/recruitment/*")
@RequiredArgsConstructor
@Slf4j
public class RecruitmentController {

  // ponytail: 폴더명으로 못 쓰는 문자만 제거, admin_board와 동일한 규칙
  private static final Pattern INVALID_FOLDER_CHARS = Pattern.compile("[\\\\/:*?\"<>|]");
  private static final int PAGINATION_SIZE = 5;

  private final RecruitmentService recruitmentService;
  private final RecruitmentMapper mapper;
  private final FestivalService festivalService;
  private final RegionService regionService;
  private final FileService fileService;
  private final RecruitmentBookmarkService recruitmentBookmarkService;

  @Value("${apikey.map}")
  private String mapApiKey;

  // 헤더 "모집공고"/"축제인력모집" 진입점. 구직자/비로그인은 검색 필터가 붙은 리스트,
  // 구인자는 검색 없이 "구인공고 등록" 버튼만 보이는 동일한 리스트를 봄 (세션의 로그인 회원 role로 구분)
  @GetMapping("/list")
  public void list(Model model,
                   @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
                   @Valid @ModelAttribute RecruitmentSearchForm form,
//                   BindingResult bindingResult,
                   @AuthenticationPrincipal CustomUserDetails user) {

//    if (form.workingStartDate() != null && form.workingEndDate() != null
//      && form.workingStartDate().isAfter(form.workingEndDate()))
//      bindingResult.rejectValue("workingEndDate",
//        "workingEndDate.outOfRange",
//        "근무시작날짜는 근무종료날짜보다 늦을 수 없습니다."
//      );
//
//    if (bindingResult.hasErrors()) return;

    boolean recruiter = user != null && user.getRole() == MemberRole.RECRUITER;
    boolean jobSeeker = user != null && user.getRole() == MemberRole.JOB_SEEKER;
    Long memberId = jobSeeker ? user.getId() : null;

    RecruitmentSearchCondition condition = mapper.toCondition(form, memberId);
    Page<RecruitmentListDTO> page = recruitmentService.getPage(pageIdx, condition);

    // 찜하기 버튼: 구직자로 로그인한 경우에만 카드별 찜 여부를 채움
    if (memberId != null) {
      Set<Long> bookmarkedIds = recruitmentBookmarkService.getBookmarkedRecruitmentIds(memberId);
      page.getContent().forEach(dto -> dto.setBookmarked(bookmarkedIds.contains(dto.getRecruitmentId())));
    }
    model.addAttribute("page", page);

    PagingHandler<RecruitmentListDTO, RecruitmentSearchForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);

    // 찜하기 버튼은 구인자에게는 안 보이고 구직자/비로그인에게만 보임
    model.addAttribute("recruiter", recruiter);
    model.addAttribute("jobSeeker", jobSeeker);
    model.addAttribute("sidoList", regionService.getSidoList());
  }
//  @GetMapping("/pay")
//  public String pay(@RequestParam Long id){
//
//    return
//  }


  @GetMapping("/manage")
  public void manageList(Model model,
                         @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
                         @Valid @ModelAttribute RecruitmentManagementForm form,
                         @AuthenticationPrincipal UserDetails user,
                         BindingResult bindingResult) {
    if (bindingResult.hasErrors()) return;

    String username = user.getUsername();
    RecruitmentManagementCondition condition = mapper.toCondition(form, username);
    Page<RecruitmentManagementListDTO> page = recruitmentService.getPage(pageIdx, condition);
    model.addAttribute("page", page);
    model.addAttribute("festivals", festivalService.getByUsername(user.getUsername()));

    PagingHandler<RecruitmentManagementListDTO, RecruitmentManagementForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);

  }

  @GetMapping("/{id}")
  public String view(@PathVariable Long id,
                     @RequestParam(required = false) String from,
                     Model model,
                     @AuthenticationPrincipal CustomUserDetails user) {
    RecruitmentResponseDTO responseDto = recruitmentService.getRecruitment(id);
    model.addAttribute("recruitment", responseDto);
    model.addAttribute("from", validateFrom(from));

    FestivalDTO festivalDto = festivalService.getDetail(responseDto.getFestivalContentId());
    model.addAttribute("fes", festivalDto);
    model.addAttribute("mapApiKey", mapApiKey);

    boolean recruiter = user != null && user.getRole() == MemberRole.RECRUITER;
    boolean jobSeeker = user != null && user.getRole() == MemberRole.JOB_SEEKER;
    model.addAttribute("recruiter", recruiter);
    model.addAttribute("jobSeeker", jobSeeker);

    // 찜하기 버튼은 구인자에게는 안 보이고 구직자/비로그인에게만 보임
    boolean bookmarked = jobSeeker && recruitmentBookmarkService.isBookmarked(id, user.getId());
    model.addAttribute("bookmarked", bookmarked);

    // 수정/삭제는 이 공고가 속한 행사를 주최한 구인자 본인만 가능
    boolean isOwner = recruiter
      && responseDto.getOrganizerMemberId() != null
      && responseDto.getOrganizerMemberId().equals(user.getId());
    model.addAttribute("isOwner", isOwner);

    model.addAttribute("expired", user != null && responseDto.getApplicationDeadline().isBefore(LocalDate.now()));

    log.info("recruitment view: {}", responseDto);
    return "recruitment/detail";
  }

  @GetMapping("/register")
  public void register(Model model,
                       @AuthenticationPrincipal UserDetails user) {
    List<FestivalDTO> festivals = festivalService.getByUsername(user.getUsername());
    List<RegionDTO> sidoList = regionService.getSidoList();
    model.addAttribute("festivals", festivals);
    model.addAttribute("sidoList", sidoList);
  }

  @GetMapping("/modify/{id}")
  public String modifyForm(@PathVariable Long id,
                           @RequestParam(required = false) String from,
                           Model model, @AuthenticationPrincipal CustomUserDetails user,
                           RedirectAttributes redirectAttributes) {
    from = validateFrom(from);
    RecruitmentResponseDTO current = requireOwnedRecruitment(id, user);
    if (current == null) {
      redirectAttributes.addFlashAttribute("errorMsg", "수정 권한이 없습니다.");
      return redirectToRecruitment(id, from, redirectAttributes);
    }
    if (current.getApplicationDeadline().isBefore(LocalDate.now())) {
      redirectAttributes.addFlashAttribute("errorMsg", "모집마감일이 지난 공고는 수정할 수 없습니다.");
      return redirectToRecruitment(id, from, redirectAttributes);
    }

    RecruitmentUpdateDTO update = recruitmentService.getRecruitmentForUpdate(id);
    model.addAttribute("recruitmentId", id);
    model.addAttribute("update", update);
    model.addAttribute("festivalContentId", current.getFestivalContentId());
    model.addAttribute("category", current.getCategory());
    model.addAttribute("festivals", festivalService.getAll());
    model.addAttribute("sidoList", regionService.getSidoList());
    model.addAttribute("sigunguList", regionService.getSigunguList(update.getRegionSidoCode()));
    model.addAttribute("from", from);
    return "recruitment/modify";
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
  // (입력했던 값도 recruitmentCreateForm으로 함께 다시 채워짐, 시/도가 없으면 시/군/구 목록은 비워둠)
  private String registerFormWithReloadedOptions(RecruitmentCreateForm recruitmentCreateForm,
                                                  Model model, BindingResult bindingResult) {
    model.addAttribute("festivals", festivalService.getAll());
    model.addAttribute("sidoList", regionService.getSidoList());
    if (recruitmentCreateForm.regionSidoCode() != null) {
      model.addAttribute("sigunguList", regionService.getSigunguList(recruitmentCreateForm.regionSidoCode()));
    }
    model.addAttribute("hasError", true);
    List<String> errorMessages = bindingResult.getFieldErrors().stream()
      .map(error -> error.getField() + ": " + error.getDefaultMessage())
      .toList();
    model.addAttribute("errorMessages", errorMessages);
    return "recruitment/register";
  }

  // 로그인한 구인자가 이 공고가 속한 행사를 주최한 본인일 때만 응답 DTO를 돌려주고, 아니면 null (수정/삭제 공통 권한 체크)
  private RecruitmentResponseDTO requireOwnedRecruitment(Long id, CustomUserDetails user) {
    if (!user.isRecruiter()) return null;
    RecruitmentResponseDTO current = recruitmentService.getRecruitment(id);
    boolean owner = current.getOrganizerMemberId() != null
      && current.getOrganizerMemberId().equals(user.getId());
    return owner ? current : null;
  }

  // 상세/목록 복귀 경로 힌트("어디서 왔는지")로 허용하는 값만 통과시킴 (그 외엔 공개 리스트로 취급)
  private String validateFrom(String from) {
    return "manage".equals(from) ? "manage" : null;
  }

  // 권한/마감 체크 실패로 상세 페이지로 되돌아갈 때, 어디서 왔는지(from)를 잃지 않고 그대로 실어서 리다이렉트
  // (다른 곳의 성공 리다이렉트와 동일하게 RedirectAttributes + {id} 템플릿 방식을 사용)
  private String redirectToRecruitment(Long id, String from, RedirectAttributes redirectAttributes) {
    redirectAttributes.addAttribute("id", id);
    if (from != null) redirectAttributes.addAttribute("from", from);
    return "redirect:/recruitment/{id}";
  }

  // 검증 실패로 수정 폼을 다시 보여줄 때, GET /modify/{id}에서 채우던 데이터를 다시 채움
  private String modifyFormWithReloadedOptions(Long id, RecruitmentResponseDTO current, String from,
                                               Model model, BindingResult bindingResult) {
    RecruitmentUpdateDTO update = recruitmentService.getRecruitmentForUpdate(id);
    model.addAttribute("recruitmentId", id);
    model.addAttribute("update", update);
    model.addAttribute("festivalContentId", current.getFestivalContentId());
    model.addAttribute("category", current.getCategory());
    model.addAttribute("festivals", festivalService.getAll());
    model.addAttribute("sidoList", regionService.getSidoList());
    model.addAttribute("sigunguList", regionService.getSigunguList(update.getRegionSidoCode()));
    model.addAttribute("from", from);
    model.addAttribute("hasError", true);
    List<String> errorMessages = bindingResult.getFieldErrors().stream()
      .map(error -> error.getField() + ": " + error.getDefaultMessage())
      .toList();
    model.addAttribute("errorMessages", errorMessages);
    return "recruitment/modify";
  }

  @PostMapping("/register")
  public String register(@Valid @ModelAttribute RecruitmentCreateForm recruitmentCreateForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) return registerFormWithReloadedOptions(recruitmentCreateForm, model, bindingResult);
    if (recruitmentCreateForm.workingStartDate().isAfter(
      recruitmentCreateForm.workingEndDate()))
      bindingResult.rejectValue("workingEndDate",
        "workingEndDate.outOfRange",
        "업무시작날짜는 업무종료날짜보다 늦을 수 없습니다."
      );
    if (bindingResult.hasErrors()) return registerFormWithReloadedOptions(recruitmentCreateForm, model, bindingResult);
    RecruitmentCreateDTO recruitmentCreateDTO = mapper.toCreateDto(recruitmentCreateForm);
    Long recruitmentId = recruitmentService.createRecruitment(recruitmentCreateDTO);

    // /recruitment/register는 manage.html의 "구인공고 등록" 버튼으로만 진입하므로 항상 관리 목록에서 온 것으로 취급
    redirectAttributes.addAttribute("id", recruitmentId);
    redirectAttributes.addAttribute("from", "manage");
    redirectAttributes.addFlashAttribute("successMsg", "공고 초안이 등록되었습니다.");

    return "redirect:/recruitment/{id}";
  }

  @PostMapping("/modify/{id}")
  public String modify(@PathVariable Long id,
                       @RequestParam(required = false) String from,
                       @Valid @ModelAttribute RecruitmentCreateForm recruitmentCreateForm,
                       BindingResult bindingResult,
                       Model model,
                       @AuthenticationPrincipal CustomUserDetails user,
                       RedirectAttributes redirectAttributes) {

    from = validateFrom(from);
    RecruitmentResponseDTO current = requireOwnedRecruitment(id, user);
    if (current == null) {
      redirectAttributes.addFlashAttribute("errorMsg", "수정 권한이 없습니다.");
      return redirectToRecruitment(id, from, redirectAttributes);
    }
    if (current.getApplicationDeadline().isBefore(LocalDate.now())) {
      redirectAttributes.addFlashAttribute("errorMsg", "모집마감일이 지난 공고는 수정할 수 없습니다.");
      return redirectToRecruitment(id, from, redirectAttributes);
    }

    if (bindingResult.hasErrors()) return modifyFormWithReloadedOptions(id, current, from, model, bindingResult);
    if (recruitmentCreateForm.workingStartDate().isAfter(
      recruitmentCreateForm.workingEndDate()))
      bindingResult.rejectValue("workingEndDate",
        "workingEndDate.outOfRange",
        "업무시작날짜는 업무종료날짜보다 늦을 수 없습니다."
      );
    if (bindingResult.hasErrors()) return modifyFormWithReloadedOptions(id, current, from, model, bindingResult);

    RecruitmentUpdateDTO recruitmentUpdateDTO = mapper.toUpdateDto(recruitmentCreateForm);
    recruitmentService.updateRecruitment(id, recruitmentUpdateDTO);

    redirectAttributes.addAttribute("id", id);
    if (from != null) redirectAttributes.addAttribute("from", from);
    redirectAttributes.addFlashAttribute("successMsg", "공고가 수정되었습니다.");

    return "redirect:/recruitment/{id}";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Long id,
                       @RequestParam(required = false) String from,
                       @AuthenticationPrincipal CustomUserDetails user,
                       RedirectAttributes redirectAttributes) {
    from = validateFrom(from);
    RecruitmentResponseDTO current = requireOwnedRecruitment(id, user);
    if (current == null) {
      redirectAttributes.addFlashAttribute("errorMsg", "삭제 권한이 없습니다.");
      return redirectToRecruitment(id, from, redirectAttributes);
    }

    recruitmentService.deleteRecruitment(id);
    redirectAttributes.addFlashAttribute("successMsg", "공고가 삭제되었습니다.");

    return "manage".equals(from) ? "redirect:/recruitment/manage" : "redirect:/recruitment/list";
  }

}
