package com.example.chook.file.repository;

import com.example.chook.file.record.FilePath;
import com.example.chook.file.record.UnreferencedFile;
import com.example.chook.recruitment.entity.QRecruitmentFile;
import com.example.chook.resume.entity.QProfileFile;
import com.example.chook.resume.entity.QResumeFile;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
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

  @Override
  public List<UnreferencedFile> findUnreferencedFiles() {
    QProfileFile profileFile = QProfileFile.profileFile;
    QRecruitmentFile recruitmentFile = QRecruitmentFile.recruitmentFile;
    QResumeFile resumeFile = QResumeFile.resumeFile;

    return jpaQueryFactory
      .select(uploadedFile.uuid, uploadedFile.uploadedAt)
      .from(uploadedFile)
      .where(

        // ProfileFile
        JPAExpressions
          .selectOne()
          .from(profileFile)
          .where(profileFile.uploadedFile.eq(uploadedFile))
          .notExists(),

        // RecruitmentFile
        JPAExpressions
          .selectOne()
          .from(recruitmentFile)
          .where(recruitmentFile.uploadedFile.eq(uploadedFile))
          .notExists(),

        // ResumeFile
        JPAExpressions
          .selectOne()
          .from(resumeFile)
          .where(resumeFile.uploadedFile.eq(uploadedFile))
          .notExists()
      )
      .fetch()
      .stream()
      .map(tuple -> new UnreferencedFile(
        tuple.get(uploadedFile.uuid),
        tuple.get(uploadedFile.uploadedAt)
      ))
      .collect(Collectors.toList());
  }
}
