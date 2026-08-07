package com.example.chook.common.handler;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@ToString
public class PagingHandler<Element, Form> {

  private static final int PAGE_GROUP_SIZE = 10;

  private final int startPageIdx;
  private int endPageIdx;
  private final int totalPageCount;
  private final long totalElementCount;
  private final int pageIdx;

  private final boolean prevBtnActive;
  private final boolean nextBtnActive;

  private final List<Element> elementList;

  private final Form form;

  public PagingHandler(Page<Element> page, int pageIdx, Form form) {

    this.elementList = page.getContent();
    this.pageIdx = pageIdx;
    this.totalPageCount = page.getTotalPages();
    this.totalElementCount = page.getTotalElements();

    this.endPageIdx = (int) Math.ceil(this.pageIdx / (double) PAGE_GROUP_SIZE) * PAGE_GROUP_SIZE;
    this.startPageIdx = endPageIdx - PAGE_GROUP_SIZE + 1;
    this.endPageIdx = Math.max(Math.min(this.endPageIdx, this.totalPageCount), startPageIdx);

    this.prevBtnActive = (this.startPageIdx != 1);
    this.nextBtnActive = (this.endPageIdx < this.totalPageCount);

    this.form = form;

  }

}
