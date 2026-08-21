package com.example.chook.support.repository;

import com.example.chook.support.entity.Inquiry;
import com.example.chook.support.entity.InquiryFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryFileRepository extends JpaRepository<InquiryFile, Long> {

  List<InquiryFile> findByInquiry(Inquiry inquiry);

}