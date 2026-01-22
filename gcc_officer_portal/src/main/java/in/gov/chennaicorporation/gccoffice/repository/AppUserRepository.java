package in.gov.chennaicorporation.gccoffice.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.gov.chennaicorporation.gccoffice.entity.AppUserEntity;
import in.gov.chennaicorporation.gccoffice.taxcollection.entity.TaxCollectionRequestEntity;

@Repository
@Transactional
public interface AppUserRepository extends JpaRepository<AppUserEntity, Integer>{
	@Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Integer getLastInsertedId();
	
	@Query(value = "SELECT * FROM `appusers`", nativeQuery = true)
    List<AppUserEntity> showAll();
	
	@Query(value = "SELECT * FROM `appusers` WHERE (userid=:userId AND `isdelete`=0)", nativeQuery = true)
    List<AppUserEntity> showById(String userId);
	
	AppUserEntity findByUsername(String username);
}