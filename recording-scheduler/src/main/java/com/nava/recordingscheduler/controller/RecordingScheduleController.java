package com.nava.recordingscheduler.controller;

import com.nava.recordingscheduler.model.Event;
import com.nava.recordingscheduler.service.LogService;
import com.nava.recordingscheduler.service.ScheduleService;
import com.nava.recordingscheduler.service.ScheduledRecordingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Controller
public class RecordingScheduleController {
    private final ScheduleService scheduleService;
    private final LogService logService;
    private final ScheduledRecordingService scheduledRecordingService;

    public RecordingScheduleController(ScheduleService scheduleService,
                                       LogService logService,
                                       ScheduledRecordingService scheduledRecordingService) {
        this.scheduleService = scheduleService;
        this.logService = logService;
        this.scheduledRecordingService = scheduledRecordingService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("schedule", scheduleService.getScheduleDTO("Main"));
        return "index";
    }

    @PostMapping("/upload-text")
    public String setText(@RequestParam("schedule") String schedule) {
        scheduleService.setSchedule(schedule);
        return "redirect:/";
    }

    @PostMapping("/upload-file")
    public String uploadFile(@RequestParam("file") MultipartFile scheduleFile) {
        if (scheduleFile.isEmpty()) {
            return "redirect:/";
        }
        File file = scheduleService.writeJsonToFile(scheduleFile);
        scheduleService.setSchedule(file);
        return "redirect:/";
    }

    @PostMapping("/record")
    public String scheduleRecord(@RequestParam("startTime") String startTime) {
        Event event = scheduleService.returnAndDisableEvent(startTime);
        if (event != null) {
            scheduledRecordingService.addRecordingTask(event);
            logService.logRegisterRecord(event);
        }
        return "redirect:/";
    }
}
