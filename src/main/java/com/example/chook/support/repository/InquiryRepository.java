package com.example.chook.support.repository;

import com.example.chook.support.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, InquiryRepositoryCustom {

  List<Inquiry> findByIdOrderByCreatedAtDesc(Long id);

}