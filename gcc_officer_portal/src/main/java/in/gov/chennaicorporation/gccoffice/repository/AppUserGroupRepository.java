package in.gov.chennaicorporation.gccoffice.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.gov.chennaicorporation.gccoffice.entity.AppUserEntity;
import in.gov.chennaicorporation.gccoffice.entity.AppUserGroupEntity;

@Repository
@Transactional
public interface AppUserGroupRepository extends JpaRepository<AppUserGroupEntity, Integer>{
	@Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Integer getLastInsertedId();
}