package com.example.chook.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FileSystemProperties {

  private String uploadDir;
  private String systemDir;
  private String deleteFailLogFile;

  private Sweep sweep;

  @Getter
  @Setter
  public static class Sweep {
    private Duration gracePeriod;
  }

}
