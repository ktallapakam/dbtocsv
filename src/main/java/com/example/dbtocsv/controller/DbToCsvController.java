package com.example.dbtocsv.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/createCSV")
@RequiredArgsConstructor
@Slf4j
public class DbToCsvController
{
    private final JobLauncher jobLauncher;
    private final Job job;

    @GetMapping("/create")
    public void createFile()
    {
        log.info("**** DbToCsvController   ****");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startedAt",System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(job, jobParameters);
            
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            throw new RuntimeException(e);
        }
    }
}
