-- ############################################################################
-- 1. 외래키 제약 비활성화
-- 실제 테이블에 데이터가 없음에도 TRUNCATE 시도 시 제약이 발생하는 경우가 있어
-- TRUNCATE 실행 전 외래키 제약을 해제함
-- ############################################################################

SET FOREIGN_KEY_CHECKS = 0;


-- ############################################################################
-- 2. 관련 테이블 TRUNCATE
-- ############################################################################

-- 이력서 테이블
TRUNCATE TABLE resume_portfolio;
TRUNCATE TABLE resume_file;
TRUNCATE TABLE resume_career;
TRUNCATE TABLE profile_file;
TRUNCATE TABLE resume;

-- 북마크 테이블
TRUNCATE TABLE recruitment_bookmark;
TRUNCATE TABLE festival_bookmark;

-- 1:1 문의 테이블
TRUNCATE TABLE inquiry_file;
TRUNCATE TABLE inquiry;

-- 지원 테이블
TRUNCATE TABLE application;

-- 공지사항 테이블   // CRUD 권한을 각 Member가 아닌 MemberRole.ADMIN에게 부여할 경우 스킵
TRUNCATE TABLE admin_board_file;
TRUNCATE TABLE admin_board;

-- 채팅 기록
TRUNCATE TABLE chat_history;

-- members 하위 테이블
TRUNCATE TABLE job_seeker_profiles;
TRUNCATE TABLE employer_profiles;
TRUNCATE TABLE business_registrations;
TRUNCATE TABLE social_logins;

-- 본 테이블
TRUNCATE TABLE members;

-- ############################################################################
-- 3. 외래키 제약 활성화
-- TRUNCATE를 마쳤다면 외래키 제약을 다시 활성화함
-- ############################################################################

SET FOREIGN_KEY_CHECKS = 1;
