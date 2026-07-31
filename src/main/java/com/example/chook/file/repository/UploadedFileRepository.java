package com.example.chook.file.repository;

import com.example.chook.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long>, UploadedFileCustomRepository {

  UploadedFile findByUuid(UUID uuid);

}
