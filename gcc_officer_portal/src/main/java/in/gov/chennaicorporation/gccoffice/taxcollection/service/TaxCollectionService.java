package in.gov.chennaicorporation.gccoffice.taxcollection.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.chennaicorporation.gccoffice.taxcollection.entity.TaxCollectionRequestEntity;
import in.gov.chennaicorporation.gccoffice.taxcollection.entity.TaxCollectionRequestStatusEntity;
import in.gov.chennaicorporation.gccoffice.taxcollection.repository.TaxCollectionRequestRepository;

@Service
public class TaxCollectionService {
	private final TaxCollectionRequestRepository taxCollectionRequestRepository;
	
	
	@Autowired
	public TaxCollectionService(TaxCollectionRequestRepository taxCollectionRequestRepository) {
		this.taxCollectionRequestRepository = taxCollectionRequestRepository;
	}
	
	@Transactional
	public Integer getLastInsertedId() {
		return taxCollectionRequestRepository.getLastInsertedId();
	}
	
	@Transactional
	public Integer saveTaxCollectionRequest(TaxCollectionRequestEntity taxCollectionRequestData) {
		TaxCollectionRequestEntity taxCollectionRequestEntity = taxCollectionRequestRepository.save(taxCollectionRequestData);
		return taxCollectionRequestEntity.getRequest_id();
	}
	/*
	@Transactional
	public Integer saveTaxCollectionRequestStatus(TaxCollectionRequestEntity taxCollectionRequestData) {
		TaxCollectionRequestEntity taxCollectionRequestEntity = taxCollectionRequestRepository.save(taxCollectionRequestData);
		return taxCollectionRequestStatusEntity.();
	}
	*/
}
