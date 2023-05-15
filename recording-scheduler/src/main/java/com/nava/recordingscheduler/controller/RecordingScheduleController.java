package com.nava.recordingscheduler.controller;

import com.nava.recordingscheduler.service.LogService;
import com.nava.recordingscheduler.service.ScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Controller
public class RecordingScheduleController {
    private final ScheduleService scheduleService;
    private final LogService logService;

    public RecordingScheduleController(ScheduleService scheduleService, LogService logService) {
        this.scheduleService = scheduleService;
        this.logService = logService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("schedule", scheduleService.getSchedule("Main"));
        return "index";
    }

    @PostMapping("/upload-text")
    public String setText(@RequestParam("schedule") String schedule) throws IOException {
        scheduleService.setSchedule(schedule);
        return "redirect:/";
    }

    @PostMapping("/upload-file")
    public String uploadFile(@RequestParam("file") MultipartFile scheduleFile) throws IOException {
        if (scheduleFile.isEmpty()) {
            return "redirect:/";
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(scheduleFile.getOriginalFilename()));
        Path path = Path.of("src/main/resources/" + fileName);
        Files.copy(scheduleFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        File file = new File("src/main/resources/" + fileName);
        scheduleService.setSchedule(file);
        return "redirect:/";
    }

    @PostMapping("/record")
    public String scheduleRecord(@RequestParam("channelID") String channelID,
                                 @RequestParam("txDayDate") String txDayDate,
                                 @RequestParam("startTime") String startTime) throws IOException {
        logService.addLogLine(channelID + " " + txDayDate + " " + startTime + "\n");
        scheduleService.disableEvent(startTime);
        return "redirect:/";
    }
}
