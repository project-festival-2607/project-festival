package com.example.chook.file.repository;

import com.example.chook.file.record.FilePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

import static com.example.chook.file.entity.QUploadedFile.uploadedFile;

@Slf4j
public class UploadedFileCustomRepositoryImpl implements UploadedFileCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public UploadedFileCustomRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Set<FilePath> findAllFilePaths() {
    return jpaQueryFactory
      .select(uploadedFile.relativePath, uploadedFile.storedName)
      .from(uploadedFile)
      .fetch()
      .stream()
      .map(tuple -> new FilePath(
        tuple.get(uploadedFile.relativePath),
        tuple.get(uploadedFile.storedName)
      ))
      .collect(Collectors.toSet());
  }
}
