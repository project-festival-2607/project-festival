package com.example.chook.dev;

import com.example.chook.festival.FestivalRepository;
import com.example.chook.file.FileProperties;
import com.example.chook.support.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// docs/mysql 의 실사례형 더미 데이터(.sql)를 읽어 그대로 실행함.
// 각 파일은 고정된 PK(festival.content_id, notice 존재 여부)로 이미 삽입됐는지 확인하는 방식으로 idempotent함.
@RequiredArgsConstructor
@Service
@Slf4j
public class DevSqlDataInitializer {

  private static final String LEGACY_CLEANUP_SQL_PATH = "docs/mysql/cleanup/delete_legacy_generated_dummy_data.sql";
  private static final String FESTIVAL_SQL_PATH = "docs/mysql/04_insert_festival_dummy_data.sql";
  private static final String RECRUITMENT_SQL_PATH = "docs/mysql/05_insert_recruitment_dummy_data.sql";
  private static final String SEED_MARKER_CONTENT_ID = "dummy-fes-01";

  private static final String ADMIN_BOARD_SQL_PATH = "docs/mysql/03_insert_admin_board_dummy_data.sql";
  private static final String ADMIN_BOARD_IMAGE_RELATIVE_PATH = "adminBoard-dummy";
  // 03_insert_admin_board_dummy_data.sql 이 참조하는 uploaded_file 3건 실제 이미지를
  // 이미 저장소에 있는 축제 더미 이미지로 대신 채워, 파일 업로드 없이도 임베드 이미지가 정상적으로 보이게 함
  private static final Map<String, String> ADMIN_BOARD_DUMMY_IMAGES = Map.of(
    "696c4b6d-c06e-461f-a8ef-be1ca77ae2a4.png", "dummyFesImg_05.png",
    "5b761605-da8e-4393-aa88-d4c672f88cdd.png", "dummyFesImg_06.png",
    "9597d036-61be-40fc-8e1b-c789fda9bec7.png", "dummyFesImg_18.png"
  );

  private final FestivalRepository festivalRepository;
  private final NoticeRepository noticeRepository;
  private final FileProperties fileProperties;
  private final DataSource dataSource;

  // festival/recruitment 를 각각 독립적으로 확인 후 삽입함 - 둘 중 하나만 먼저 존재하는 상태에서
  // (예: 이전 실행에서 festival은 성공하고 recruitment는 SQL 오류 등으로 실패한 경우) 재기동해도
  // festival 존재 여부만으로 전체를 건너뛰지 않고 빠진 쪽을 마저 채워 넣도록 함
  public void importFestivalAndRecruitmentDummyData() {

    // festival 존재 여부와 무관하게 매번 시도함 - "예시 행사 #NN"/"일반 구인 공고 #N" 등 제목 패턴에만
    // 걸리는 레거시 데이터를 지우므로, 이미 dummy-fes-01이 들어가 있는 환경(예: festival만 성공하고
    // recruitment는 실패했던 이전 실행)에서도 남아있는 레거시 데이터를 놓치지 않고 정리함.
    // 지울 대상이 없으면 그냥 아무 일도 안 하므로 매번 실행해도 안전함
    runSqlFileSafely(LEGACY_CLEANUP_SQL_PATH);

    if (!festivalRepository.existsById(SEED_MARKER_CONTENT_ID)) {
      log.info("실사례형 축제 더미 데이터 삽입 시작");
      if (!runSqlFileSafely(FESTIVAL_SQL_PATH)) return;
      log.info("실사례형 축제 더미 데이터 삽입 완료");
    }

    if (!existsDummyRecruitment()) {
      log.info("실사례형 구인공고 더미 데이터 삽입 시작");
      if (!runSqlFileSafely(RECRUITMENT_SQL_PATH)) return;
      log.info("실사례형 구인공고 더미 데이터 삽입 완료");
    }
  }

  private boolean existsDummyRecruitment() {
    String sql = "SELECT 1 FROM recruitment WHERE festival_id = ? LIMIT 1";
    try (Connection conn = dataSource.getConnection();
         java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, SEED_MARKER_CONTENT_ID);
      try (java.sql.ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      log.warn("더미 구인공고 존재 여부 확인 실패: {}", e.getMessage());
      return true; // 확인 자체가 실패하면 안전하게 재삽입을 시도하지 않음
    }
  }

  public void importAdminBoardDummyData() {

    if (noticeRepository.count() > 0) return;

    log.info("공지사항 더미 데이터 삽입 시작");
    prepareAdminBoardDummyImages();
    if (!runSqlFileSafely(ADMIN_BOARD_SQL_PATH)) return;
    log.info("공지사항 더미 데이터 삽입 완료");
  }

  // admin_board 더미 글이 본문에 임베드하는 이미지 3건을 업로드 디렉터리에 미리 채워둠
  // (이미 존재하면 손대지 않음 - 실제 운영자가 올린 파일을 덮어쓰지 않기 위함)
  private void prepareAdminBoardDummyImages() {
    Path targetDir = Path.of(fileProperties.getUploadDir(), ADMIN_BOARD_IMAGE_RELATIVE_PATH);
    try {
      Files.createDirectories(targetDir);
      for (Map.Entry<String, String> entry : ADMIN_BOARD_DUMMY_IMAGES.entrySet()) {
        Path target = targetDir.resolve(entry.getKey());
        if (Files.exists(target)) continue;
        try (InputStream in = new ClassPathResource("static/images/dummy/festival/" + entry.getValue()).getInputStream()) {
          Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
      }
    } catch (IOException e) {
      log.warn("공지사항 더미 이미지 준비 실패 (본문 임베드 이미지가 깨져 보일 수 있음): {}", e.getMessage());
    }
  }

  private boolean runSqlFileSafely(String path) {
    try {
      runSqlFile(path);
      return true;
    } catch (IOException e) {
      log.warn("더미 데이터 SQL 파일을 읽을 수 없어 삽입을 건너뜀: {}", e.getMessage());
      return false;
    } catch (SQLException e) {
      log.warn("더미 데이터 SQL 실행 중 오류가 발생해 삽입을 건너뜀: {}", e.getMessage());
      return false;
    }
  }

  private void runSqlFile(String path) throws IOException, SQLException {
    Path file = Path.of(path);
    if (!Files.exists(file)) {
      log.warn("더미 데이터 SQL 파일을 찾을 수 없음: {}", path);
      return;
    }

    // 파일 맨 앞의 설명용 주석/빈 줄만 건너뜀 - 본문(공지글 내용)에 등장하는 "---" 같은 줄은
    // SQL 주석이 아니라 실제 데이터이므로 절대 제거하면 안 됨
    List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
    int bodyStart = 0;
    while (bodyStart < lines.size()) {
      String stripped = lines.get(bodyStart).strip();
      if (stripped.isEmpty() || stripped.startsWith("--")) {
        bodyStart++;
      } else {
        break;
      }
    }
    String sql = lines.subList(bodyStart, lines.size()).stream().collect(Collectors.joining("\n"));

    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {
      for (String statement : sql.split(";\\s*\\R")) {
        String trimmed = statement.strip();
        if (trimmed.isEmpty()) continue;
        stmt.execute(trimmed);
      }
    }
  }
}