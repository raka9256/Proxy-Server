package com.ps.web.rest;

import com.ps.ProxyServerApp;

import com.ps.domain.Dummy;
import com.ps.repository.DummyRepository;
import com.ps.service.DummyService;
import com.ps.service.dto.DummyDTO;
import com.ps.service.mapper.DummyMapper;
import com.ps.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DummyResource REST controller.
 *
 * @see DummyResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProxyServerApp.class)
public class DummyResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_WEBSITE = "AAAAAAAAAA";
    private static final String UPDATED_WEBSITE = "BBBBBBBBBB";

    @Autowired
    private DummyRepository dummyRepository;

    @Autowired
    private DummyMapper dummyMapper;

    @Autowired
    private DummyService dummyService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restDummyMockMvc;

    private Dummy dummy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DummyResource dummyResource = new DummyResource(dummyService);
        this.restDummyMockMvc = MockMvcBuilders.standaloneSetup(dummyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dummy createEntity() {
        Dummy dummy = new Dummy()
            .name(DEFAULT_NAME)
            .username(DEFAULT_USERNAME)
            .email(DEFAULT_EMAIL)
            .website(DEFAULT_WEBSITE);
        return dummy;
    }

    @Before
    public void initTest() {
        dummyRepository.deleteAll();
        dummy = createEntity();
    }

    @Test
    public void createDummy() throws Exception {
        int databaseSizeBeforeCreate = dummyRepository.findAll().size();

        // Create the Dummy
        DummyDTO dummyDTO = dummyMapper.toDto(dummy);
        restDummyMockMvc.perform(post("/api/dummies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dummyDTO)))
            .andExpect(status().isCreated());

        // Validate the Dummy in the database
        List<Dummy> dummyList = dummyRepository.findAll();
        assertThat(dummyList).hasSize(databaseSizeBeforeCreate + 1);
        Dummy testDummy = dummyList.get(dummyList.size() - 1);
        assertThat(testDummy.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDummy.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testDummy.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testDummy.getWebsite()).isEqualTo(DEFAULT_WEBSITE);
    }

    @Test
    public void createDummyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = dummyRepository.findAll().size();

        // Create the Dummy with an existing ID
        dummy.setId("existing_id");
        DummyDTO dummyDTO = dummyMapper.toDto(dummy);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDummyMockMvc.perform(post("/api/dummies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dummyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Dummy> dummyList = dummyRepository.findAll();
        assertThat(dummyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllDummies() throws Exception {
        // Initialize the database
        dummyRepository.save(dummy);

        // Get all the dummyList
        restDummyMockMvc.perform(get("/api/dummies?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dummy.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].website").value(hasItem(DEFAULT_WEBSITE.toString())));
    }

    @Test
    public void getDummy() throws Exception {
        // Initialize the database
        dummyRepository.save(dummy);

        // Get the dummy
        restDummyMockMvc.perform(get("/api/dummies/{id}", dummy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(dummy.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.website").value(DEFAULT_WEBSITE.toString()));
    }

    @Test
    public void getNonExistingDummy() throws Exception {
        // Get the dummy
        restDummyMockMvc.perform(get("/api/dummies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateDummy() throws Exception {
        // Initialize the database
        dummyRepository.save(dummy);
        int databaseSizeBeforeUpdate = dummyRepository.findAll().size();

        // Update the dummy
        Dummy updatedDummy = dummyRepository.findOne(dummy.getId());
        updatedDummy
            .name(UPDATED_NAME)
            .username(UPDATED_USERNAME)
            .email(UPDATED_EMAIL)
            .website(UPDATED_WEBSITE);
        DummyDTO dummyDTO = dummyMapper.toDto(updatedDummy);

        restDummyMockMvc.perform(put("/api/dummies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dummyDTO)))
            .andExpect(status().isOk());

        // Validate the Dummy in the database
        List<Dummy> dummyList = dummyRepository.findAll();
        assertThat(dummyList).hasSize(databaseSizeBeforeUpdate);
        Dummy testDummy = dummyList.get(dummyList.size() - 1);
        assertThat(testDummy.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDummy.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testDummy.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testDummy.getWebsite()).isEqualTo(UPDATED_WEBSITE);
    }

    @Test
    public void updateNonExistingDummy() throws Exception {
        int databaseSizeBeforeUpdate = dummyRepository.findAll().size();

        // Create the Dummy
        DummyDTO dummyDTO = dummyMapper.toDto(dummy);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDummyMockMvc.perform(put("/api/dummies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dummyDTO)))
            .andExpect(status().isCreated());

        // Validate the Dummy in the database
        List<Dummy> dummyList = dummyRepository.findAll();
        assertThat(dummyList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteDummy() throws Exception {
        // Initialize the database
        dummyRepository.save(dummy);
        int databaseSizeBeforeDelete = dummyRepository.findAll().size();

        // Get the dummy
        restDummyMockMvc.perform(delete("/api/dummies/{id}", dummy.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Dummy> dummyList = dummyRepository.findAll();
        assertThat(dummyList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dummy.class);
        Dummy dummy1 = new Dummy();
        dummy1.setId("id1");
        Dummy dummy2 = new Dummy();
        dummy2.setId(dummy1.getId());
        assertThat(dummy1).isEqualTo(dummy2);
        dummy2.setId("id2");
        assertThat(dummy1).isNotEqualTo(dummy2);
        dummy1.setId(null);
        assertThat(dummy1).isNotEqualTo(dummy2);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DummyDTO.class);
        DummyDTO dummyDTO1 = new DummyDTO();
        dummyDTO1.setId("id1");
        DummyDTO dummyDTO2 = new DummyDTO();
        assertThat(dummyDTO1).isNotEqualTo(dummyDTO2);
        dummyDTO2.setId(dummyDTO1.getId());
        assertThat(dummyDTO1).isEqualTo(dummyDTO2);
        dummyDTO2.setId("id2");
        assertThat(dummyDTO1).isNotEqualTo(dummyDTO2);
        dummyDTO1.setId(null);
        assertThat(dummyDTO1).isNotEqualTo(dummyDTO2);
    }
}
