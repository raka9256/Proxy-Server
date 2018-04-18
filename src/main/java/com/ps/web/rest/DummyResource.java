package com.ps.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ps.service.DummyService;
import com.ps.web.rest.util.HeaderUtil;
import com.ps.web.rest.util.PaginationUtil;
import com.ps.service.dto.DummyDTO;
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

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Dummy.
 */
@RestController
@RequestMapping("/api")
public class DummyResource {

    private final Logger log = LoggerFactory.getLogger(DummyResource.class);

    private static final String ENTITY_NAME = "dummy";

    private final DummyService dummyService;

    public DummyResource(DummyService dummyService) {
        this.dummyService = dummyService;
    }

    /**
     * POST  /dummies : Create a new dummy.
     *
     * @param dummyDTO the dummyDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dummyDTO, or with status 400 (Bad Request) if the dummy has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/dummies")
    @Timed
    public ResponseEntity<DummyDTO> createDummy(@RequestBody DummyDTO dummyDTO) throws URISyntaxException {
        log.debug("REST request to save Dummy : {}", dummyDTO);
        if (dummyDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new dummy cannot already have an ID")).body(null);
        }
        DummyDTO result = dummyService.save(dummyDTO);
        return ResponseEntity.created(new URI("/api/dummies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /dummies : Updates an existing dummy.
     *
     * @param dummyDTO the dummyDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated dummyDTO,
     * or with status 400 (Bad Request) if the dummyDTO is not valid,
     * or with status 500 (Internal Server Error) if the dummyDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/dummies")
    @Timed
    public ResponseEntity<DummyDTO> updateDummy(@RequestBody DummyDTO dummyDTO) throws URISyntaxException {
        log.debug("REST request to update Dummy : {}", dummyDTO);
        if (dummyDTO.getId() == null) {
            return createDummy(dummyDTO);
        }
        DummyDTO result = dummyService.save(dummyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, dummyDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /dummies : get all the dummies.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of dummies in body
     */
    @GetMapping("/dummies")
    @Timed
    public ResponseEntity<List<DummyDTO>> getAllDummies(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Dummies");
        Page<DummyDTO> page = dummyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/dummies");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /dummies/:id : get the "id" dummy.
     *
     * @param id the id of the dummyDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the dummyDTO, or with status 404 (Not Found)
     */
    @GetMapping("/dummies/{id}")
    @Timed
    public ResponseEntity<DummyDTO> getDummy(@PathVariable String id) {
        log.debug("REST request to get Dummy : {}", id);
        DummyDTO dummyDTO = dummyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(dummyDTO));
    }

    /**
     * DELETE  /dummies/:id : delete the "id" dummy.
     *
     * @param id the id of the dummyDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/dummies/{id}")
    @Timed
    public ResponseEntity<Void> deleteDummy(@PathVariable String id) {
        log.debug("REST request to delete Dummy : {}", id);
        dummyService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
