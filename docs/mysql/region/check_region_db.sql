SELECT A.sido_short_name, B.sigungu_name
FROM region_sido A
  JOIN region_sigungu B ON A.sido_code = B.sido_code;