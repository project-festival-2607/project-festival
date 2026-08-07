package com.example.chook.common.handler;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@ToString
public class PagingHandler<Element, Form> {

  private final int startPageIdx;
  private final int totalPageCount;
  private final long totalElementCount;
  private final int pageIdx;
  private final boolean prevBtnActive;
  private final boolean nextBtnActive;
  private final List<Element> elementList;
  private final Form form;

  private int endPageIdx;

  public PagingHandler(Page<Element> page,
                       Form form,
                       int paginationSize,
                       int pageIdx) {

    this.elementList = page.getContent();
    this.pageIdx = pageIdx;
    this.totalPageCount = page.getTotalPages();
    this.totalElementCount = page.getTotalElements();

    this.endPageIdx = (int) Math.ceil(this.pageIdx / (double) paginationSize) * paginationSize;
    this.startPageIdx = endPageIdx - paginationSize + 1;
    this.endPageIdx = Math.max(Math.min(this.endPageIdx, this.totalPageCount), startPageIdx);

    this.prevBtnActive = page.hasPrevious();
    this.nextBtnActive = page.hasNext();

    this.form = form;

  }

}
