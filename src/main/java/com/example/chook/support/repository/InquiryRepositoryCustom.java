package com.example.chook.support.repository;

import com.example.chook.support.entity.Inquiry;

import java.util.List;

public interface InquiryRepositoryCustom {

  List<Inquiry> search(String searchType, String keyword, String answered);

}