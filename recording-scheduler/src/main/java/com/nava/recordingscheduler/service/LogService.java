package com.nava.recordingscheduler.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
public class LogService {
    public String loggingFilePathString;
    private final Path loggingFilePath;

    public LogService(String loggingFilePathString) {
        this.loggingFilePathString = loggingFilePathString;
        this.loggingFilePath = Path.of(loggingFilePathString);
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

    public void logRegisterRecord(String channelID,
                                  String txDayDate,
                                  String startTime,
                                  String durationTC) {
        String logLine = "RECORD_REG - CH_ID: " +
                channelID +
                " TX_DAY_DATE: " +
                txDayDate +
                " START_TIME: " +
                startTime +
                " DURATION_TC: " +
                durationTC +
                ";";
        addLogLine(logLine);
    }
}
