package com.example.chook.support.service;

import com.example.chook.support.dto.InquiryDTO;
import com.example.chook.support.entity.Inquiry;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InquiryService {

  Inquiry register(InquiryDTO dto, List<MultipartFile> files);

  List<InquiryDTO> getList(Long memberId);

  List<InquiryDTO> getAdminList(String searchType, String keyword, String answered);

  InquiryDTO getDetail(Long ino);

  Inquiry answer(Long ino, String comment);

}