package in.gov.chennaicorporation.gccoffice.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.gov.chennaicorporation.gccoffice.entity.UserInfoEntity;

@Repository
@Transactional
public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Integer>{
	@Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Integer getLastInsertedId();
}
