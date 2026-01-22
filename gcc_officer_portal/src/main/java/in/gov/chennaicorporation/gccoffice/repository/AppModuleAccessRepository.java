package in.gov.chennaicorporation.gccoffice.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gov.chennaicorporation.gccoffice.entity.AppModuleAccessEntity;
import in.gov.chennaicorporation.gccoffice.entity.AppModuleEntity;

@Repository
@Transactional
public interface AppModuleAccessRepository extends JpaRepository<AppModuleAccessEntity, Integer>{
	@Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Integer getLastInsertedId();
	
	@Query(value = "SELECT * FROM `modules_access` WHERE `usergroup_id`=:usergroup_id AND `isactive`=1 AND `isdelete`=0", nativeQuery = true)
    List<AppModuleAccessEntity> findByUserGroupID(@Param("usergroup_id") String usergroup_id);
	
	@Query(value = "SELECT * FROM `modules_access` WHERE (`usergroup_id`=:usergroup_id AND `module_id`=:module_id) AND (`isactive`=1 AND `isdelete`=0)", nativeQuery = true)
    List<AppModuleAccessEntity> findByAll(@Param("usergroup_id") String usergroup_id,@Param("module_id") String module_id);
	
	@Modifying
	@Query(value = "INSERT INTO `modules_access`(`module_id`, `usergroup_id`) VALUES (:module_id,:usergroup_id)", nativeQuery = true)
	void insert(@Param("usergroup_id") String usergroup_id, @Param("module_id") String module_id);
	
	@Modifying
	@Query(value = "UPDATE `modules_access` SET `isactive`=:isactive WHERE `module_id`=:module_id AND `usergroup_id`=:usergroup_id", nativeQuery = true)
	void update(@Param("isactive") Boolean isactive, @Param("usergroup_id") String usergroup_id, @Param("module_id") String module_id);
	
	@Modifying
	@Query(value = "UPDATE `modules_access` SET `isactive`=:isactive WHERE `usergroup_id`=:usergroup_id", nativeQuery = true)
	void updateActiveStatus(@Param("isactive") Boolean isactive, @Param("usergroup_id") String usergroup_id);

}