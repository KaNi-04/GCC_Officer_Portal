package in.gov.chennaicorporation.gccoffice.taxcollection.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gov.chennaicorporation.gccoffice.taxcollection.entity.TaxCollectionRequestEntity;

@Repository
@Transactional
public interface TaxCollectionRequestRepository extends JpaRepository<TaxCollectionRequestEntity, Integer> {
 
	@Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Integer getLastInsertedId();
	
	@Query(value = "SELECT * FROM `collection_request`", nativeQuery = true)
    List<TaxCollectionRequestEntity> showAll();
	
	// Insert
	@Modifying
    @Query(value ="insert into `collection_request` (`name`,`mobile`,`ptax`,`usertype`,`availability_date`, `created_by`) values (:name, :mobile, :ptax, :usertype, :availabilitydate, :created_by)", nativeQuery = true)
	void saveData(String name, String mobile, String ptax, String usertype, String availabilitydate, Integer created_by);
    
}