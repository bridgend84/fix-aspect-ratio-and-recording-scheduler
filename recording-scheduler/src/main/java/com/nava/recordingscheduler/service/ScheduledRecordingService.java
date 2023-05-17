package com.nava.recordingscheduler.service;

import com.nava.recordingscheduler.model.Event;
import com.nava.recordingscheduler.model.RecordCommand;
import com.nava.recordingscheduler.model.Recorder;
import com.nava.recordingscheduler.model.RecordingTask;
import org.moormanity.smpte.timecode.FrameRate;
import org.moormanity.smpte.timecode.TimecodeOperations;
import org.moormanity.smpte.timecode.TimecodeRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ScheduledRecordingService {

    private final ScheduleService scheduleService;

    private final LogService logService;

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

    public ScheduledRecordingService(ScheduleService scheduleService, LogService logService, FrameRate fps) {
        this.channelRecorders = new HashMap<>();
        this.scheduleService = scheduleService;
        this.logService = logService;
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

    @Scheduled(fixedDelay = 1000)
    public void commandScheduler() {
        for (Map.Entry<String, Recorder> entry : channelRecorders.entrySet()) {
            String channelId = entry.getKey();
            Recorder recorder = entry.getValue();
            RecordingTask currentRecordingTask = recorder.getCurrentRecordingTask();
            if (!recorder.isRecording()) {
                if (isItNowBySecond(currentRecordingTask.getScheduleDay(), currentRecordingTask.getStartTime())) {
                    int responseCode = sendRecordCommand(RecordCommand.START, channelId);
                    recorder.setRecording(true);
                    logService.logCommand(
                            channelId,
                            convertToLocalDateTime(
                                    currentRecordingTask.getScheduleDay(),
                                    currentRecordingTask.getStartTime()),
                            RecordCommand.START,
                            responseCode);
                }
            } else {
                if (isItNowBySecond(currentRecordingTask.getScheduleDay(), currentRecordingTask.getStopTime())) {
                    LocalDateTime currentStop = convertToLocalDateTime(
                            currentRecordingTask.getScheduleDay(),
                            currentRecordingTask.getStopTime());
                    LocalDateTime nextStart = recorder.peekNextTask() == null ? null : convertToLocalDateTime(
                            recorder.peekNextTask().getScheduleDay(),
                            recorder.peekNextTask().getStartTime());
                    if (nextStart == null) {
                        int responseCode = sendRecordCommand(RecordCommand.STOP, channelId);
                        recorder.setRecording(false);
                        logService.logCommand(
                                channelId,
                                convertToLocalDateTime(
                                        currentRecordingTask.getScheduleDay(),
                                        currentRecordingTask.getStartTime()),
                                RecordCommand.STOP,
                                responseCode);
                        recorder.setCurrentRecordingTask(null);
                    } else if (currentStop.isAfter(nextStart) || currentStop.isEqual(nextStart)) {
                        recorder.discardCurrentAndCueNextTask();
                    } else {
                        int responseCode = sendRecordCommand(RecordCommand.STOP, channelId);
                        recorder.setRecording(false);
                        logService.logCommand(
                                channelId,
                                convertToLocalDateTime(
                                        currentRecordingTask.getScheduleDay(),
                                        currentRecordingTask.getStartTime()),
                                RecordCommand.STOP,
                                responseCode);
                        recorder.discardCurrentAndCueNextTask();
                    }
                }
            }
        }
    }

    private boolean isItNowBySecond(LocalDateTime scheduleDay, TimecodeRecord timeCode) {
        LocalDateTime taskLocalDateTime = LocalDateTime.of(
                scheduleDay.getYear(),
                scheduleDay.getMonth(),
                scheduleDay.getDayOfMonth(),
                timeCode.getHours(),
                timeCode.getMinutes(),
                timeCode.getSeconds());
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).equals(taskLocalDateTime);
    }

    private LocalDateTime convertToLocalDateTime(LocalDateTime scheduleDay, TimecodeRecord timeCode) {
        return LocalDateTime.of(
                scheduleDay.getYear(),
                scheduleDay.getMonth(),
                scheduleDay.getDayOfMonth(),
                timeCode.getHours(),
                timeCode.getMinutes(),
                timeCode.getSeconds());
    }

    private int sendRecordCommand(RecordCommand command, String channelId) {
        int responseStatus = 0;
        try {
            URL url = new URL(
                    (command == RecordCommand.START ? startUrlWithoutChId : stopUrlWithoutChId) + channelId);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            responseStatus = con.getResponseCode();
            con.disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return responseStatus;
    }
}
