package com.nava.recordingscheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nava.recordingscheduler.model.Event;
import com.nava.recordingscheduler.model.Schedule;
import com.nava.recordingscheduler.model.ScheduleDTO;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class ScheduleService {
    private static final int MILLISECOND_PER_SECOND = 1000;
    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Budapest");
    private final ObjectMapper mapper;
    private Schedule schedule;

    public ScheduleService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Schedule getSchedule(String requestType) {
        return this.schedule == null ? null : Schedule
                .builder()
                .txDayDate(this.schedule.getTxDayDate())
                .channelID(this.schedule.getChannelID())
                .events(this.schedule.getEvents().stream()
                        .filter(event -> event.getRequestType().equals(requestType)).toList())
                .build();
    }

    public void setSchedule(File file) throws IOException {
        scheduleBuilder(mapper.readValue(file, ScheduleDTO.class));
    }

    public void setSchedule(String text) throws IOException {
        scheduleBuilder(mapper.readValue(text, ScheduleDTO.class));
    }

    private void scheduleBuilder(ScheduleDTO scheduleDTO) {
        this.schedule = Schedule
                .builder()
                .txDayDate(LocalDateTime.ofEpochSecond(
                        Long.parseLong(scheduleDTO.getTxDayDate())/MILLISECOND_PER_SECOND,
                        0,
                        ZONE_ID.getRules().getOffset(LocalDateTime.now())).format(DateTimeFormatter.ISO_DATE))
                .channelID(scheduleDTO.getChannelID())
                .events(scheduleDTO
                        .getEventList()
                        .get("event")
                        .stream()
                        .map(eventDTO -> Event
                                .builder()
                                .startTime(eventDTO.getStart().get("startTime"))
                                .requestType(eventDTO.getProgrammeProperties().get("requestType"))
                                .seriesTitle(eventDTO.getProgrammeProperties().get("seriesTitle"))
                                .programmeTitle(eventDTO.getProgrammeProperties().get("programmeTitle"))
                                .recordable(true)
                                .build())
                        .toList())
                .build();
    }

    public void disableEvent(String startTime) {
        Event event = schedule.getEvents().stream().filter(e -> e.getStartTime().equals(startTime)).findAny().orElseThrow();
        event.setRecordable(false);
    }
}
