package com.example.chook.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FileProperties {

  private String uploadDir;
  private String systemDir;
  private String deletionFailureLogFile;

  private Sweep sweep;

  @Getter
  @Setter
  public static class Sweep {

    private Unreferenced unreferenced;
    private Untracked untracked;

    @Getter
    @Setter
    public static class Unreferenced {
      private String cron;
      private Duration gracePeriod;
    }

    @Getter
    @Setter
    public static class Untracked {
      private String cron;
      private Duration gracePeriod;
    }

  }

}
