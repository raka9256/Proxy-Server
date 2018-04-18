package com.ps.service;

import com.ps.domain.Dummy;
import com.ps.repository.DummyRepository;
import com.ps.service.dto.DummyDTO;
import com.ps.service.mapper.DummyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * Service Implementation for managing Dummy.
 */
@Service
public class DummyService {

    private final Logger log = LoggerFactory.getLogger(DummyService.class);

    private final DummyRepository dummyRepository;

    private final DummyMapper dummyMapper;
    public DummyService(DummyRepository dummyRepository, DummyMapper dummyMapper) {
        this.dummyRepository = dummyRepository;
        this.dummyMapper = dummyMapper;
    }

    /**
     * Save a dummy.
     *
     * @param dummyDTO the entity to save
     * @return the persisted entity
     */
    public DummyDTO save(DummyDTO dummyDTO) {
        log.debug("Request to save Dummy : {}", dummyDTO);
        Dummy dummy = dummyMapper.toEntity(dummyDTO);
        dummy = dummyRepository.save(dummy);
        return dummyMapper.toDto(dummy);
    }

    /**
     *  Get all the dummies.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    public Page<DummyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Dummies");
        return dummyRepository.findAll(pageable)
            .map(dummyMapper::toDto);
    }

    /**
     *  Get one dummy by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    public DummyDTO findOne(String id) {
        log.debug("Request to get Dummy : {}", id);
        Dummy dummy = dummyRepository.findOne(id);
        return dummyMapper.toDto(dummy);
    }

    /**
     *  Delete the  dummy by id.
     *
     *  @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete Dummy : {}", id);
        dummyRepository.delete(id);
    }
}
