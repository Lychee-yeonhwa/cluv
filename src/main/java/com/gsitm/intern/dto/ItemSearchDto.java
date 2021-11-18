package com.gsitm.intern.dto;

import com.gsitm.intern.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemSearchDto {

    private String searchDateType;  //현재 시간과 상품 등록일 비교하여 상품 데이터 조회

    private ItemSellStatus searchSellStatus;  //상품의 판매상태를 기준으로 상품 데이터 조회

    private String searchBy;  //상품 조회할 때 어떤 유형으로 조회할지 선택 (itemNm, createdBy)

    private String searchQuery = "";  //조회할 검색어 저장 변수
}
