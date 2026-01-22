package in.gov.chennaicorporation.gccoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gov.chennaicorporation.gccoffice.entity.UserActivityLog;

import java.util.List;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    List<UserActivityLog> findByUserIdOrderByTimestampDesc(String userId);
}