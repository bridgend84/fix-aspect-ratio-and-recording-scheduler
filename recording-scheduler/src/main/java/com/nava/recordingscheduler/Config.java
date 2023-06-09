package com.nava.recordingscheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.moormanity.smpte.timecode.FrameRate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${recording.logfile.path}")
    public String recordingLogfilePath;

    @Value("${schedule.json.path}")
    public String scheduleJsonPath;

    @Bean
    public String loggingFilePathString() {
        return recordingLogfilePath;
    }

    @Bean
    public String scheduleJsonPathString() {
        return scheduleJsonPath;
    }

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }

    @Bean
    public FrameRate fps() {
        return FrameRate._25;
    }
}
