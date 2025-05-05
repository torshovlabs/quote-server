package com.torshovlabs.quote.config;

import com.torshovlabs.quote.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    private final GroupService groupService;

    @Autowired
    public SchedulingConfig(GroupService groupService) {
        this.groupService = groupService;
    }

    // Run at midnight every day
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleQuotePermissionRotation() {
        groupService.rotateQuotePermissions();
    }
}