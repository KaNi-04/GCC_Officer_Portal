package in.gov.chennaicorporation.gccoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.chennaicorporation.gccoffice.entity.UserActivityLog;
import in.gov.chennaicorporation.gccoffice.repository.UserActivityLogRepository;

import java.util.List;

@Service
public class UserActivityTimelineService {

    private final UserActivityLogRepository userActivityLogRepository;
    
    @Autowired
    public UserActivityTimelineService(UserActivityLogRepository userActivityLogRepository) {
        this.userActivityLogRepository = userActivityLogRepository;
    }

    public List<UserActivityLog> getUserActivityTimeline(String userId) {
        return userActivityLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}

