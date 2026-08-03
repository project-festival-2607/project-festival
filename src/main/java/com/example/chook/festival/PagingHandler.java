package com.example.chook.festival;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@ToString
public class PagingHandler {
    private int startPage;
    private int endPage;
    private int totalPage;
    private long totalElements;
    private int pageNo;
    private boolean prev, next;
    private List<FestivalDTO> list;

    public PagingHandler(Page<FestivalDTO> list, int pageNo){
        this.list = list.getContent();
        this.pageNo = pageNo;
        this.totalPage = list.getTotalPages();
        this.totalElements = list.getTotalElements();
        this.endPage = (int)Math.ceil(this.pageNo / 10.0) * 10;
        this.startPage = this.endPage - 9;

        this.endPage = Math.min(this.endPage, this.totalPage);

        this.prev = list.hasPrevious();
        this.next = list.hasNext();

    }
}
