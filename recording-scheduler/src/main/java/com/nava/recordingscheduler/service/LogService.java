package com.nava.recordingscheduler.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
public class LogService {
    Path path = Path.of("src/main/resources/log.txt");

    public LogService() throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    public void addLogLine(String line) throws IOException {
        Files.write(path, line.getBytes(), StandardOpenOption.APPEND);
    }
}
