package com.example.chook.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FileSystemProperties {

  private String uploadDir;
  private String systemDir;
  private String deleteFailLogFile;

}
