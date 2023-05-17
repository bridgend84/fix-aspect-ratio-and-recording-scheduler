package com.nava.recordingscheduler.model;

import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Recorder {
    private RecordingTask currentRecordingTask;
    private boolean isRecording;
    private Set<RecordingTask> recordingTasks;

    public void addRecordingTask(RecordingTask recordingTask) {
        if (currentRecordingTask == null) {
            this.currentRecordingTask = recordingTask;
        } else {
            recordingTasks.add(recordingTask);
        }
    }

    public void removeRecordingTask(RecordingTask recordingTask) {
        recordingTasks.remove(recordingTask);
    }
}
