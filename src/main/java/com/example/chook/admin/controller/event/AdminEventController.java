package com.example.chook.admin.controller.event;

import com.example.chook.admin.dto.member.RecruiterTableDTO;
import com.example.chook.admin.provider.ModalInfoFieldProvider;
import com.example.chook.admin.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/event")
@RequiredArgsConstructor
@Slf4j
public class AdminEventController {

  private final AdminMemberService adminMemberService;
  private final ModalInfoFieldProvider infoFieldProvider;

  @GetMapping("/fragment/member")
  public String getMemberFragment(Model model) {
    model.addAttribute("targetInfoList", infoFieldProvider.assignFestivalManagerMember());
    return "admin/fragments/modal :: modal-member-info";
  }

  @GetMapping("/recruiter/{id}")
  @ResponseBody
  public RecruiterTableDTO getRecruiter(@PathVariable Long id) {
    return adminMemberService.getRecruiterDto(id);
  }

}
