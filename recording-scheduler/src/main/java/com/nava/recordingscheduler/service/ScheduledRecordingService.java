package com.nava.recordingscheduler.service;

import com.nava.recordingscheduler.model.Recorder;
import com.nava.recordingscheduler.model.RecordingTask;
import org.moormanity.smpte.timecode.FrameRate;
import org.moormanity.smpte.timecode.TimecodeOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScheduledRecordingService {
    @Value("${recording.start.url}")
    private String startUrlWithoutChId;

    @Value("${recording.stop.url}")
    private String stopUrlWithoutChId;

    @Value("${recording.puffer.minutes.before}")
    private String pufferBefore;

    @Value("${recording.puffer.minutes.after}")
    private String pufferAfter;

    private final FrameRate fps;

    private final Map<String, Recorder> channelRecorders;

    public ScheduledRecordingService(FrameRate fps) {
        this.channelRecorders = new HashMap<>();
        this.fps = fps;
    }

    public void addRecordingTask(String channelId, String startTime, String durationTC) {
        if (!channelRecorders.containsKey(channelId)) {
            channelRecorders.put(channelId, Recorder
                    .builder()
                    .isRecording(false)
                    .recordStarted(null)
                    .recordingTasks(new TreeSet<>((a, b) -> TimecodeOperations
                            .subtract(a.getStartTime(), b.getStartTime()).getFrames()))
                    .build());
        }
        channelRecorders.get(channelId).addRecordingTask(RecordingTask
                .builder()
                .startTime(
                        TimecodeOperations.subtract(
                                TimecodeOperations.fromTimecodeString(startTime, fps),
                                TimecodeOperations.fromTimecodeString(pufferBefore, fps)))
                .stopTime(TimecodeOperations.add(
                        TimecodeOperations.add(
                                TimecodeOperations.fromTimecodeString(startTime, fps),
                                TimecodeOperations.fromTimecodeString(durationTC, fps)
                        ),
                        TimecodeOperations.fromTimecodeString(pufferAfter, fps)
                ))
                .build());
    }

    @Scheduled(fixedDelay = 30000)
    public void printRecordingSchedule() {
        if (channelRecorders.containsKey("D1")) {
            channelRecorders.get("D1").getRecordingTasks().forEach(r -> {
                System.out.println(TimecodeOperations.toTimecodeString(r.getStartTime()));
                System.out.println(TimecodeOperations.toTimecodeString(r.getStopTime()));
            });
        }
    }
}
