-- 예전 DevFestivalDataInitializer/DevRecruitmentInitializer(랜덤 생성기)가 남긴
-- "예시 행사 #NN" / "일반 구인 공고 #N" 등 레거시 더미 데이터만 제목 패턴으로 골라서 지움.
-- 04/05번 실사례형 더미나 실제 API 동기화 축제는 이 패턴에 걸리지 않으므로 안전하게 매번 실행 가능함
-- (DevSqlDataInitializer 에 의해 앱 기동 시마다 무조건 실행됨 - 지울 대상이 없으면 그냥 아무 일도 안 함)

SET FOREIGN_KEY_CHECKS = 0;

DELETE rb FROM recruitment_bookmark rb JOIN recruitment r ON r.recruitment_id = rb.recruit_id
  WHERE r.title LIKE '일반 구인 공고 #%' OR r.title LIKE '기타 공고 #%' OR r.title LIKE '장비 공고 #%' OR r.title LIKE '푸드트럭 공고 #%';
DELETE rf FROM recruitment_file rf JOIN recruitment r ON r.recruitment_id = rf.recruit_id
  WHERE r.title LIKE '일반 구인 공고 #%' OR r.title LIKE '기타 공고 #%' OR r.title LIKE '장비 공고 #%' OR r.title LIKE '푸드트럭 공고 #%';
DELETE a FROM application a JOIN recruitment r ON r.recruitment_id = a.recruit_id
  WHERE r.title LIKE '일반 구인 공고 #%' OR r.title LIKE '기타 공고 #%' OR r.title LIKE '장비 공고 #%' OR r.title LIKE '푸드트럭 공고 #%';
DELETE ph FROM point_history ph JOIN recruitment r ON r.recruitment_id = ph.recruit_id
  WHERE r.title LIKE '일반 구인 공고 #%' OR r.title LIKE '기타 공고 #%' OR r.title LIKE '장비 공고 #%' OR r.title LIKE '푸드트럭 공고 #%';
DELETE ri FROM recruitment_individual ri JOIN recruitment r ON r.recruitment_id = ri.recruitment_id
  WHERE r.title LIKE '일반 구인 공고 #%' OR r.title LIKE '기타 공고 #%' OR r.title LIKE '장비 공고 #%' OR r.title LIKE '푸드트럭 공고 #%';
DELETE rft FROM recruitment_food_truck rft JOIN recruitment r ON r.recruitment_id = rft.recruitment_id
  WHERE r.title LIKE '일반 구인 공고 #%' OR r.title LIKE '기타 공고 #%' OR r.title LIKE '장비 공고 #%' OR r.title LIKE '푸드트럭 공고 #%';
DELETE FROM recruitment
  WHERE title LIKE '일반 구인 공고 #%' OR title LIKE '기타 공고 #%' OR title LIKE '장비 공고 #%' OR title LIKE '푸드트럭 공고 #%';

DELETE fb FROM festival_bookmark fb JOIN festival f ON f.content_id = fb.festival_content_id
  WHERE f.title LIKE '예시 행사 #%';
DELETE ff FROM festival_file ff JOIN festival f ON f.content_id = ff.content_id
  WHERE f.title LIKE '예시 행사 #%';
DELETE FROM festival WHERE title LIKE '예시 행사 #%';

SET FOREIGN_KEY_CHECKS = 1;