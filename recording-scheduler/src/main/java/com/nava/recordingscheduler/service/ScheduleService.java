package com.nava.recordingscheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nava.recordingscheduler.model.Event;
import com.nava.recordingscheduler.model.Schedule;
import com.nava.recordingscheduler.model.ScheduleDAO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class ScheduleService {
    private static final int MILLISECOND_PER_SECOND = 1000;
    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Budapest");
    private final ObjectMapper mapper;
    private Schedule schedule;
    public String scheduleJsonPathString;

    public ScheduleService(ObjectMapper mapper, String scheduleJsonPathString) {
        this.scheduleJsonPathString = scheduleJsonPathString;
        this.mapper = mapper;
    }

    public Schedule getScheduleDTO(String requestType) {
        return this.schedule == null ? null : Schedule
                .builder()
                .formattedTxDayDate(this.schedule.getFormattedTxDayDate())
                .channelID(this.schedule.getChannelID())
                .events(this.schedule.getEvents().stream()
                        .filter(event -> event.getRequestType().equals(requestType)).toList())
                .build();
    }

    public LocalDateTime getScheduleDay() {
        return this.schedule == null ? null : this.schedule.getTxDayDate();
    }

    public String getFormattedScheduleDay() {
        return this.schedule == null ? null : this.schedule.getFormattedTxDayDate();
    }

    public String getChannelId() {
        return this.schedule == null ? null : this.schedule.getChannelID();
    }

    public void setSchedule(File file) {
        try {
            scheduleBuilder(mapper.readValue(file, ScheduleDAO.class));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setSchedule(String text) {
        try {
            scheduleBuilder(mapper.readValue(text, ScheduleDAO.class));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void scheduleBuilder(ScheduleDAO scheduleDAO) {
        LocalDateTime txDayDate = LocalDateTime.ofEpochSecond(
                Long.parseLong(scheduleDAO.getTxDayDate()) / MILLISECOND_PER_SECOND,
                0,
                ZONE_ID.getRules().getOffset(LocalDateTime.now()));
        this.schedule = Schedule
                .builder()
                .txDayDate(txDayDate)
                .formattedTxDayDate(txDayDate.format(DateTimeFormatter.ISO_DATE))
                .channelID(scheduleDAO.getChannelID())
                .events(scheduleDAO
                        .getEventList()
                        .get("event")
                        .stream()
                        .map(eventDTO -> Event
                                .builder()
                                .startTime(eventDTO.getStart().get("startTime"))
                                .durationTC(eventDTO.getStart().get("durationTC"))
                                .requestType(eventDTO.getProgrammeProperties().get("requestType"))
                                .seriesTitle(eventDTO.getProgrammeProperties().get("seriesTitle"))
                                .programmeTitle(eventDTO.getProgrammeProperties().get("programmeTitle"))
                                .FPS(eventDTO.getFPS())
                                .recordable(true)
                                .build())
                        .toList())
                .build();
    }

    public Event returnAndDisableEvent(String startTime) {
        if (schedule == null) {
            return null;
        }
        Event event = schedule.getEvents().stream().filter(e -> e.getStartTime().equals(startTime)).findAny().orElse(null);
        if (event != null) {
            event.setRecordable(false);
        }
        return event;
    }

    public File writeJsonToFile(MultipartFile scheduleFile) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(scheduleFile.getOriginalFilename()));
        Path path = Path.of(scheduleJsonPathString + fileName);
        try {
            Files.copy(scheduleFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return new File(scheduleJsonPathString + fileName);
    }
}
