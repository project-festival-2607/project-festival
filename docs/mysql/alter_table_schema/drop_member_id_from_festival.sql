-- ############################################################################
-- 1. festival 테이블 백업
-- PK 제약을 포함한 다른 제약 조건 복사 없이 데이터만 복사됨에 주의
-- ############################################################################

CREATE TABLE festival_backup AS
SELECT * FROM festival;


-- ############################################################################
-- 2. Member 엔티티 연결 해제
-- 외래키 제약 삭제
-- ############################################################################

ALTER TABLE festival
DROP FOREIGN KEY fk_festival_member_id;

-- member_id COLUMN 삭제
ALTER TABLE festival
DROP COLUMN member_id;


-- ############################################################################
-- 2-A. 필요 시 데이터 복구
-- ############################################################################

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE festival;
INSERT INTO festival (
  content_id, address, age_limit, end_date, event_place, first_image, homepage, map_x, map_y, overview, play_time,
  program, second_image, start_date, tel, tel_name, title, use_time, zip_code
)
SELECT
  content_id, address, age_limit, end_date, event_place, first_image, homepage, map_x, map_y, overview, play_time,
  program, second_image, start_date, tel, tel_name, title, use_time, zip_code
FROM festival_backup;

SET FOREIGN_KEY_CHECKS = 1;


-- ############################################################################
-- 3. 정상 작업 완료 시 백업 삭제
-- ############################################################################

DROP TABLE festival_backup;
