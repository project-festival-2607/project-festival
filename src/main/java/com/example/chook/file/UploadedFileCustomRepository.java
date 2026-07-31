package com.example.chook.file;

import java.util.Set;

public interface UploadedFileCustomRepository {

  Set<FilePathRecord> findAllFilePaths();

}
