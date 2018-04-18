package com.ps.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ps.domain.Proxy;
import com.ps.repository.ProxyRepository;
import com.ps.service.ProxyService;
import com.ps.service.dto.DummyDTO;
import com.ps.web.rest.util.HeaderUtil;
import com.ps.web.rest.util.PaginationUtil;
import com.ps.service.dto.ProxyDTO;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Base64;
import javax.inject.Inject;
import java.time.LocalDate;

/**
 * REST controller for managing Proxy.
 */
@RestController
@RequestMapping("/api")
public class ProxyResource {

    private final Logger log = LoggerFactory.getLogger(ProxyResource.class);

    private static final String ENTITY_NAME = "proxy";

    private final ProxyService proxyService;

    @Inject
    private ProxyRepository proxyRepository;

    public ProxyResource(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    /**
     * POST  /proxies : Create a new proxy.
     *
     * @param proxyDTO the proxyDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new proxyDTO, or with status 400 (Bad Request) if the proxy has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/proxies")
    @Timed
    public ResponseEntity<ProxyDTO> createProxy(@Valid @RequestBody ProxyDTO proxyDTO) throws URISyntaxException {
        log.debug("REST request to save Proxy : {}", proxyDTO);
        if (proxyDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new proxy cannot already have an ID")).body(null);
        }
        ProxyDTO result = proxyService.save(proxyDTO);
        return ResponseEntity.created(new URI("/api/proxies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /proxies : Updates an existing proxy.
     *
     * @param proxyDTO the proxyDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated proxyDTO,
     * or with status 400 (Bad Request) if the proxyDTO is not valid,
     * or with status 500 (Internal Server Error) if the proxyDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/proxies")
    @Timed
    public ResponseEntity<ProxyDTO> updateProxy(@Valid @RequestBody ProxyDTO proxyDTO) throws URISyntaxException {
        log.debug("REST request to update Proxy : {}", proxyDTO);
        if (proxyDTO.getId() == null) {
            return createProxy(proxyDTO);
        }
        ProxyDTO result = proxyService.save(proxyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, proxyDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /proxies : get all the proxies.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of proxies in body
     */
    @GetMapping("/proxies")
    @Timed
    public ResponseEntity<List<ProxyDTO>> getAllProxies(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Proxies");
        Page<ProxyDTO> page = proxyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/proxies");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /proxies/:id : get the "id" proxy.
     *
     * @param id the id of the proxyDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the proxyDTO, or with status 404 (Not Found)
     */
    @GetMapping("/proxies/{id}")
    @Timed
    public ResponseEntity<ProxyDTO> getProxy(@PathVariable String id) {
        log.debug("REST request to get Proxy : {}", id);
        ProxyDTO proxyDTO = proxyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(proxyDTO));
    }

    /**
     * GET  /proxiesEnc/:enc: get all the dummies using proxy.
     *
     * @param enc enc is encoded url of the actual endpoint
     * @return the ResponseEntity with status 200 (OK) and with body the dummyDTO, or with status 404 (Not Found)
     */
    @GetMapping("/proxiesEnc/{enc}")
    @Timed
    public ResponseEntity<DummyDTO[]> getDummyProxy(@PathVariable String enc) throws URISyntaxException {
        log.debug("REST request to get Proxy : {}",enc);

        //url decoded
        byte[] decodedBytes = Base64.getDecoder().decode(enc);
        String dec = new String(decodedBytes);
        //calling actual endpoint
        final String uri = "http://localhost:8080/api/"+dec;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<DummyDTO[]> result = restTemplate.getForEntity(uri,DummyDTO[].class);

        if(result.getStatusCodeValue() == 200){
            //deleting all data(cache here) in proxy first
            deleteAll();
            ProxyDTO proxyDTO = new ProxyDTO();
            proxyDTO.setApi(uri);
            proxyDTO.setHeader(result.getHeaders().toString());
            proxyDTO.setContentType(result.getHeaders().getContentType().toString());
            proxyDTO.setDateModified(LocalDate.now());
            updateProxy(proxyDTO);
            return new ResponseEntity<>(result.getBody(), result.getHeaders(), HttpStatus.OK);
        }else {
            String header = null;
            List<Proxy> list = proxyRepository.findAll();
            for (int i = 0; i < list.size(); i++) {
                header = list.get(i).getHeader();
            }
            return new ResponseEntity<>(null, null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE  /proxy/all: delete all the proxies(here caches).
     *
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/proxies/all")
    public ResponseEntity<Void> deleteAll() {
        log.debug("REST request to delete all Proxies : {}");
        proxyRepository.deleteAll();
        return ResponseEntity.ok().build();
    }


    /**
     * DELETE  /proxies/:id : delete the "id" proxy.
     *
     * @param id the id of the proxyDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/proxies/{id}")
    @Timed
    public ResponseEntity<Void> deleteProxy(@PathVariable String id) {
        log.debug("REST request to delete Proxy : {}", id);
        proxyService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
