package in.gov.chennaicorporation.gccoffice.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gov.chennaicorporation.gccoffice.entity.MenuItemEntity;
import in.gov.chennaicorporation.gccoffice.entity.SubMenuItemEntity;

@Repository
@Transactional
public interface SubMenuItemRepository extends JpaRepository<SubMenuItemEntity, Integer>{
	@Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Integer getLastInsertedId();
	
	@Query(value = "SELECT * FROM `submenuitem` WHERE (`isactive`=1 AND `isdelete`=0) AND `module_id`=:module_id ORDER BY `orderby`", nativeQuery = true)
    List<SubMenuItemEntity> getSubMenuItemByModuleId(@Param("module_id") int module_id);
	
	@Query(value = "SELECT * FROM `submenuitem` WHERE (`isactive`=1 AND `isdelete`=0) AND `menuitem_id`=:menuitem_id ORDER BY `orderby`", nativeQuery = true)
    List<SubMenuItemEntity> getSubMenuItemByMenuItemId(@Param("menuitem_id") int menuitem_id);
	
	@Query(value = "SELECT * FROM `submenuitem` WHERE (`isactive`=1 AND `isdelete`=0) AND (`menuitem_id`=:menuitem_id AND `id` IN (:checkSubMenuid)) ORDER BY `orderby`", nativeQuery = true)
    List<SubMenuItemEntity> getSubMenuItemByMenuItemIdAccess(@Param("menuitem_id") int menuitem_id,String[] checkSubMenuid);
	
	@Query(value = "SELECT * FROM `submenuitem` WHERE (`url`=:url) AND (`isactive`=1 AND `isdelete`=0) LIMIT 0,1", nativeQuery = true)
    List<SubMenuItemEntity> findByUrl(@Param("url") String url);
	
}