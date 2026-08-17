package com.example.chook.support.repository;

import com.example.chook.support.entity.Notice;
import com.example.chook.support.entity.NoticeFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {

  List<NoticeFile> findByNotice(Notice notice);

  boolean existsByUploadedFile_Uuid(UUID uuid);
}