package com.nava.recordingscheduler.service;

import com.nava.recordingscheduler.model.Event;
import com.nava.recordingscheduler.model.Recorder;
import com.nava.recordingscheduler.model.RecordingTask;
import org.moormanity.smpte.timecode.FrameRate;
import org.moormanity.smpte.timecode.TimecodeOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ScheduledRecordingService {

    private final ScheduleService scheduleService;

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

    public ScheduledRecordingService(ScheduleService scheduleService, FrameRate fps) {
        this.channelRecorders = new HashMap<>();
        this.scheduleService = scheduleService;
        this.fps = fps;
    }

    public void addRecordingTask(Event event) {
        if (!channelRecorders.containsKey(scheduleService.getChannelId())) {
            channelRecorders.put(scheduleService.getChannelId(), Recorder
                    .builder()
                    .isRecording(false)
                    .currentRecordingTask(null)
                    .recordingTasks(new TreeSet<>((a, b) -> TimecodeOperations
                            .subtract(a.getStartTime(), b.getStartTime()).getFrames()))
                    .build());
        }
        channelRecorders.get(scheduleService.getChannelId()).addRecordingTask(RecordingTask
                .builder()
                .scheduleDay(scheduleService.getScheduleDay())
                .startTime(
                        TimecodeOperations.subtract(
                                TimecodeOperations.fromTimecodeString(event.getStartTime(), fps),
                                TimecodeOperations.fromTimecodeString(pufferBefore, fps)))
                .stopTime(TimecodeOperations.add(
                        TimecodeOperations.add(
                                TimecodeOperations.fromTimecodeString(event.getStartTime(), fps),
                                TimecodeOperations.fromTimecodeString(event.getDurationTC(), fps)
                        ),
                        TimecodeOperations.fromTimecodeString(pufferAfter, fps)
                ))
                .build());
    }

    @Scheduled(fixedDelay = 30000)
    public void printRecordingSchedule() {
        if (channelRecorders.containsKey("D1")) {
            channelRecorders.get("D1").getRecordingTasks().forEach(r -> {
                System.out.println(r.getScheduleDay().format(DateTimeFormatter.ISO_DATE));
                System.out.println(TimecodeOperations.toTimecodeString(r.getStartTime()));
                System.out.println(TimecodeOperations.toTimecodeString(r.getStopTime()));
            });
        }
    }
}
