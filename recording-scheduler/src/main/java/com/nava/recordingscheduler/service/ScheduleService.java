package com.nava.recordingscheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nava.recordingscheduler.model.Event;
import com.nava.recordingscheduler.model.Schedule;
import com.nava.recordingscheduler.model.ScheduleDTO;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ScheduleService {
    private final ObjectMapper mapper;
    private Schedule schedule;

    public ScheduleService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Schedule getSchedule() {
        return schedule;
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
                .txDayDate(scheduleDTO.getTxDayDate())
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
                                .build())
                        .toList())
                .build();
    }
}
