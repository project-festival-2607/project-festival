package com.example.chook.recruitment.handler;

import com.example.chook.recruitment.form.RecruitmentSearchForm;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@ToString
public class PagingHandler<T> {

  private static final int PAGE_GROUP_SIZE = 10;

  private final int startPageIdx;
  private int endPageIdx;
  private final int totalPageCount;
  private final long totalElementCount;
  private final int pageIdx;

  private final boolean prevBtnActive;
  private final boolean nextBtnActive;

  private final List<T> elementList;

  private final RecruitmentSearchForm form;

  public PagingHandler(Page<T> page, int pageIdx, RecruitmentSearchForm form) {

    this.elementList = page.getContent();
    this.pageIdx = pageIdx;
    this.totalPageCount = page.getTotalPages();
    this.totalElementCount = page.getTotalElements();

    this.endPageIdx = (int) Math.ceil(this.pageIdx / (double) PAGE_GROUP_SIZE) * PAGE_GROUP_SIZE;
    this.startPageIdx = endPageIdx - PAGE_GROUP_SIZE + 1;
    this.endPageIdx = Math.min(this.endPageIdx, this.totalPageCount);

    this.prevBtnActive = (this.startPageIdx != 1);
    this.nextBtnActive = (this.endPageIdx < this.totalPageCount);

    this.form = form;

  }

}
