package com.example.chook.admin.record;

import jakarta.validation.constraints.*;

public record AddProductRequest(

  @NotBlank(message = "상품명을 입력해 주시기 바랍니다.")
  @Size(max = 100, message="상품명 길이는 100을 넘을 수 없습니다.")
  String productName,

  @NotNull(message = "포인트 양을 입력해 주시기 바랍니다.")
  @Min(value=1000, message="포인트 양은 1000 이상의 정수여야 합니다.")
  @Max(value=1000000, message="포인트 양은 1000000 이하의 정수여야 합니다.")
  Integer productPointGet

) {
}
