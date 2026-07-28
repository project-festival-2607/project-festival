## MySQL 초기 설정
### `chook_database` 데이터베이스 생성
1. 터미널 (cmd 등) 에서 MySQL의 root 계정으로 로그인
2. `00_create_db.sql`의 구문을 실행해 데이터베이스 생성
```cmd
mysql -u root -p
```
```mysql
CREATE DATABASE chook_database;
```

### `chook_admin` 사용자 생성
1. `01_create_user.sql`의 구문을 실행해 데이터베이스 생성
- 비밀번호는 `/src/main/resources/application.properties` 를 참고
```mysql
CREATE USER 'chook_admin'@'localhost' IDENTIFIED BY '';
```

### `chook_admin` 사용자 권한 설정
```mysql
GRANT ALL PRIVILEGES ON chook_database.* 
TO 'chook_admin'@'localhost';
FLUSH PRIVILEGES;
```