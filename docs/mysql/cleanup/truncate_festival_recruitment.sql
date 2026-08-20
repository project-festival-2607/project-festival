-- festival/recruitment 및 그 하위(개인/푸드트럭 상세, 북마크, 첨부파일, 지원) 테이블을 비움
-- members 등 다른 테이블은 건드리지 않음

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE recruitment_bookmark;
TRUNCATE TABLE recruitment_file;
TRUNCATE TABLE application;
TRUNCATE TABLE recruitment_individual;
TRUNCATE TABLE recruitment_food_truck;
TRUNCATE TABLE recruitment;

TRUNCATE TABLE festival_bookmark;
TRUNCATE TABLE festival_file;
TRUNCATE TABLE festival;

SET FOREIGN_KEY_CHECKS = 1;