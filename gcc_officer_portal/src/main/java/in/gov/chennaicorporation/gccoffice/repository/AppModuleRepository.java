package in.gov.chennaicorporation.gccoffice.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import in.gov.chennaicorporation.gccoffice.entity.AppModuleEntity;

@Repository
@Transactional
public interface AppModuleRepository extends JpaRepository<AppModuleEntity, Integer>{
	@Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Integer getLastInsertedId();
	
	@Query(value = "SELECT * FROM `modules` WHERE `isactive`=1 AND `isdelete`=0 ORDER BY `orderby`", nativeQuery = true)
    List<AppModuleEntity> showAll();
	
	@Query(value = "SELECT * FROM `modules` WHERE `id` IN (:checkModuleid) AND (`isactive`=1 AND `isdelete`=0) ORDER BY `id`", nativeQuery = true)
    List<AppModuleEntity> showAllById(String[] checkModuleid);
	
	@Query(value = "SELECT * FROM `modules` WHERE (`module_path` = :module_path) AND (`isactive`=1 AND `isdelete`=0) LIMIT 0,1", nativeQuery = true)
    List<AppModuleEntity> findByModulePath(String module_path);
}