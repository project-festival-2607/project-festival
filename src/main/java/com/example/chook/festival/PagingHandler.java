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
    private String type;
    private String keyword;
    private String month;

    public PagingHandler(Page<FestivalDTO> list, int pageNo){
        this.list = list.getContent();
        this.pageNo = pageNo;
        this.totalPage = list.getTotalPages();
        this.totalElements = list.getTotalElements();
        this.endPage = (int)Math.ceil(this.pageNo / 10.0) * 10;
        this.startPage = this.endPage - 9;

        this.endPage = (int)Math.ceil(this.pageNo / 10.0) * 10;
        this.startPage = this.endPage - 9;

        if (this.totalPage == 0) {
            this.endPage = 1;
            this.startPage = 1;
        } else {
            this.endPage = Math.min(this.endPage, this.totalPage);
            if (this.startPage > this.endPage) {
                this.startPage = this.endPage;
            }
        }

        this.prev = list.hasPrevious();
        this.next = list.hasNext();

    }

    public PagingHandler(Page<FestivalDTO> list, int pageNo, String type, String keyword, String month){
        this(list, pageNo);
        this.type = type;
        this.keyword = keyword;
        this.month = month;
    }
}
