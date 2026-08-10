package com.example.chook.dev;

import com.example.chook.payment.entity.Product;
import com.example.chook.payment.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class DevProductInitializer {

  private final ProductRepository productRepository;

  public void generateSampleProducts() {

    log.info("테스트용 상품 데이터 삽입 시작");

    List<Integer> values = List.of(5000, 10000, 20000);
    for (Integer value : values) {
      productRepository.save(Product.builder()
        .pointGet(value)
        .productPrice(value)
        .productName(String.format("%d 포인트", value))
        .build());
    }

    log.info("테스트용 상품 데이터 삽입 완료");

  }
}
