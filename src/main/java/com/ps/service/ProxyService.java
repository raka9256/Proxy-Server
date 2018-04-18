package com.ps.service;

import com.ps.domain.Proxy;
import com.ps.repository.ProxyRepository;
import com.ps.service.dto.ProxyDTO;
import com.ps.service.mapper.ProxyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * Service Implementation for managing Proxy.
 */
@Service
public class ProxyService {

    private final Logger log = LoggerFactory.getLogger(ProxyService.class);

    private final ProxyRepository proxyRepository;

    private final ProxyMapper proxyMapper;
    public ProxyService(ProxyRepository proxyRepository, ProxyMapper proxyMapper) {
        this.proxyRepository = proxyRepository;
        this.proxyMapper = proxyMapper;
    }

    /**
     * Save a proxy.
     *
     * @param proxyDTO the entity to save
     * @return the persisted entity
     */
    public ProxyDTO save(ProxyDTO proxyDTO) {
        log.debug("Request to save Proxy : {}", proxyDTO);
        Proxy proxy = proxyMapper.toEntity(proxyDTO);
        proxy = proxyRepository.save(proxy);
        return proxyMapper.toDto(proxy);
    }

    /**
     *  Get all the proxies.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    public Page<ProxyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Proxies");
        return proxyRepository.findAll(pageable)
            .map(proxyMapper::toDto);
    }

    /**
     *  Get one proxy by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    public ProxyDTO findOne(String id) {
        log.debug("Request to get Proxy : {}", id);
        Proxy proxy = proxyRepository.findOne(id);
        return proxyMapper.toDto(proxy);
    }

    /**
     *  Delete the  proxy by id.
     *
     *  @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete Proxy : {}", id);
        proxyRepository.delete(id);
    }
}
