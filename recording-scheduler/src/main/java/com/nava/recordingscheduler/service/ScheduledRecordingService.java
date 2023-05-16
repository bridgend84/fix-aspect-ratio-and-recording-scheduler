package com.nava.recordingscheduler.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ScheduledRecordingService {
    @Scheduled(fixedDelay = 10000)
    public void whatTimeIsIt() {
        System.out.println(LocalDateTime.now());
    }
}
