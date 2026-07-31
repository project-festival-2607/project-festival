package com.example.chook.file;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

import static com.example.chook.entity.QUploadedFile.uploadedFile;

@Slf4j
public class UploadedFileCustomRepositoryImpl implements UploadedFileCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public UploadedFileCustomRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Set<FilePathRecord> findAllFilePaths() {
    return jpaQueryFactory
      .select(uploadedFile.relativePath, uploadedFile.storedName)
      .from(uploadedFile)
      .fetch()
      .stream()
      .map(tuple -> new FilePathRecord(
        tuple.get(uploadedFile.relativePath),
        tuple.get(uploadedFile.storedName)
      ))
      .collect(Collectors.toSet());
  }
}
