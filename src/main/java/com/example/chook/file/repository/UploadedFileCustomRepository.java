package com.example.chook.file.repository;

import com.example.chook.file.FilePathRecord;

import java.util.Set;

public interface UploadedFileCustomRepository {

  Set<FilePathRecord> findAllFilePaths();

}
