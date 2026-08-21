package com.example.chook.file.repository;

import com.example.chook.file.record.FilePath;
import com.example.chook.file.record.UnreferencedFile;

import java.util.List;
import java.util.Set;

public interface UploadedFileRepositoryCustom {

  Set<FilePath> findAllFilePaths();

  List<UnreferencedFile> findUnreferencedFiles();

}
