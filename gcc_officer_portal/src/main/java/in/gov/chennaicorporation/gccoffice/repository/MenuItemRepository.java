package in.gov.chennaicorporation.gccoffice.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gov.chennaicorporation.gccoffice.entity.MenuItemEntity;
import in.gov.chennaicorporation.gccoffice.entity.SubMenuItemAccessEntity;

@Repository
@Transactional
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Integer>{
	@Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Integer getLastInsertedId();
	
	@Query(value = "SELECT * FROM `menuitem` WHERE (`isactive`=1 AND `isdelete`=0) AND `module_id`=:module_id ORDER BY `orderby`", nativeQuery = true)
    List<MenuItemEntity> getMenuItemByModuleId(@Param("module_id") int module_id);
	
	@Query(value = "SELECT * FROM `menuitem` WHERE (`isactive`=1 AND `isdelete`=0) AND (`module_id`=:module_id AND `id` IN (:checkMenuid)) ORDER BY `orderby`", nativeQuery = true)
    List<MenuItemEntity> getMenuItemByModuleIdAccess(@Param("module_id") int module_id, String[] checkMenuid);
	
	@Query(value = "SELECT * FROM `menuitem` WHERE (`isactive`=1 AND `isdelete`=0) AND (`url`=:url) LIMIT 0,1", nativeQuery = true)
    List<MenuItemEntity> findByUrl(@Param("url") String url);
	
}