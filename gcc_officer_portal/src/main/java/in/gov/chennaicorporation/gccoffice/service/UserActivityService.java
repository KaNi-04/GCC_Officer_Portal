package in.gov.chennaicorporation.gccoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.chennaicorporation.gccoffice.entity.UserActivityLog;
import in.gov.chennaicorporation.gccoffice.repository.UserActivityLogRepository;

import java.time.LocalDateTime;

@Service
public class UserActivityService {

    private final UserActivityLogRepository userActivityLogRepository;

    @Autowired
    public UserActivityService(UserActivityLogRepository userActivityLogRepository) {
        this.userActivityLogRepository = userActivityLogRepository;
    }

    public void logUserActivity(String userId, String activityType) {
        UserActivityLog userActivityLog = new UserActivityLog();
        userActivityLog.setUserId(userId);
        userActivityLog.setActivityType(activityType);
        userActivityLog.setTimestamp(LocalDateTime.now());

        userActivityLogRepository.save(userActivityLog);
    }
}