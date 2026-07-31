package com.example.chook.file.repository;

import com.example.chook.file.record.FilePath;

import java.util.Set;

public interface UploadedFileCustomRepository {

  Set<FilePath> findAllFilePaths();

}
