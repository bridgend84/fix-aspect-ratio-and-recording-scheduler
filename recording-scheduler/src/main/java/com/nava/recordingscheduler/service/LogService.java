package com.nava.recordingscheduler.service;

import com.nava.recordingscheduler.model.Event;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
public class LogService {
    public String loggingFilePathString;
    private final Path loggingFilePath;
    private final ScheduleService scheduleService;

    public LogService(String loggingFilePathString, ScheduleService scheduleService) {
        this.loggingFilePathString = loggingFilePathString;
        this.loggingFilePath = Path.of(loggingFilePathString);
        this.scheduleService = scheduleService;
        createLogFile();
    }

    private void createLogFile() {
        try {
            if (!Files.exists(loggingFilePath)) {
                Files.createFile(loggingFilePath);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addLogLine(String line) {
        try {
            Files.write(loggingFilePath, line.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void logRegisterRecord(Event event) {
        String logLine = "RECORD_REG - CH_ID: " +
                scheduleService.getChannelId() +
                ", TX_DAY_DATE: " +
                scheduleService.getFormattedScheduleDay() +
                ", START_TIME: " +
                event.getStartTime().substring(0, 8) +
                ", DURATION_TC: " +
                event.getDurationTC().substring(0, 8) +
                ";\n";
        addLogLine(logLine);
    }
}
