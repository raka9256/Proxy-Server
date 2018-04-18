package com.ps.web.rest;

import com.ps.ProxyServerApp;

import com.ps.domain.Proxy;
import com.ps.repository.ProxyRepository;
import com.ps.service.ProxyService;
import com.ps.service.dto.ProxyDTO;
import com.ps.service.mapper.ProxyMapper;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProxyResource REST controller.
 *
 * @see ProxyResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProxyServerApp.class)
public class ProxyResourceIntTest {

    private static final String DEFAULT_API = "AAAAAAAAAA";
    private static final String UPDATED_API = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_HEADER = "AAAAAAAAAA";
    private static final String UPDATED_HEADER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_MODIFIED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_MODIFIED = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private ProxyRepository proxyRepository;

    @Autowired
    private ProxyMapper proxyMapper;

    @Autowired
    private ProxyService proxyService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restProxyMockMvc;

    private Proxy proxy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProxyResource proxyResource = new ProxyResource(proxyService);
        this.restProxyMockMvc = MockMvcBuilders.standaloneSetup(proxyResource)
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
    public static Proxy createEntity() {
        Proxy proxy = new Proxy()
            .api(DEFAULT_API)
            .contentType(DEFAULT_CONTENT_TYPE)
            .header(DEFAULT_HEADER)
            .dateModified(DEFAULT_DATE_MODIFIED);
        return proxy;
    }

    @Before
    public void initTest() {
        proxyRepository.deleteAll();
        proxy = createEntity();
    }

    @Test
    public void createProxy() throws Exception {
        int databaseSizeBeforeCreate = proxyRepository.findAll().size();

        // Create the Proxy
        ProxyDTO proxyDTO = proxyMapper.toDto(proxy);
        restProxyMockMvc.perform(post("/api/proxies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(proxyDTO)))
            .andExpect(status().isCreated());

        // Validate the Proxy in the database
        List<Proxy> proxyList = proxyRepository.findAll();
        assertThat(proxyList).hasSize(databaseSizeBeforeCreate + 1);
        Proxy testProxy = proxyList.get(proxyList.size() - 1);
        assertThat(testProxy.getApi()).isEqualTo(DEFAULT_API);
        assertThat(testProxy.getContentType()).isEqualTo(DEFAULT_CONTENT_TYPE);
        assertThat(testProxy.getHeader()).isEqualTo(DEFAULT_HEADER);
        assertThat(testProxy.getDateModified()).isEqualTo(DEFAULT_DATE_MODIFIED);
    }

    @Test
    public void createProxyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = proxyRepository.findAll().size();

        // Create the Proxy with an existing ID
        proxy.setId("existing_id");
        ProxyDTO proxyDTO = proxyMapper.toDto(proxy);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProxyMockMvc.perform(post("/api/proxies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(proxyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Proxy> proxyList = proxyRepository.findAll();
        assertThat(proxyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkApiIsRequired() throws Exception {
        int databaseSizeBeforeTest = proxyRepository.findAll().size();
        // set the field null
        proxy.setApi(null);

        // Create the Proxy, which fails.
        ProxyDTO proxyDTO = proxyMapper.toDto(proxy);

        restProxyMockMvc.perform(post("/api/proxies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(proxyDTO)))
            .andExpect(status().isBadRequest());

        List<Proxy> proxyList = proxyRepository.findAll();
        assertThat(proxyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllProxies() throws Exception {
        // Initialize the database
        proxyRepository.save(proxy);

        // Get all the proxyList
        restProxyMockMvc.perform(get("/api/proxies?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(proxy.getId())))
            .andExpect(jsonPath("$.[*].api").value(hasItem(DEFAULT_API.toString())))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].header").value(hasItem(DEFAULT_HEADER.toString())))
            .andExpect(jsonPath("$.[*].dateModified").value(hasItem(DEFAULT_DATE_MODIFIED.toString())));
    }

    @Test
    public void getProxy() throws Exception {
        // Initialize the database
        proxyRepository.save(proxy);

        // Get the proxy
        restProxyMockMvc.perform(get("/api/proxies/{id}", proxy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(proxy.getId()))
            .andExpect(jsonPath("$.api").value(DEFAULT_API.toString()))
            .andExpect(jsonPath("$.contentType").value(DEFAULT_CONTENT_TYPE.toString()))
            .andExpect(jsonPath("$.header").value(DEFAULT_HEADER.toString()))
            .andExpect(jsonPath("$.dateModified").value(DEFAULT_DATE_MODIFIED.toString()));
    }

    @Test
    public void getNonExistingProxy() throws Exception {
        // Get the proxy
        restProxyMockMvc.perform(get("/api/proxies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateProxy() throws Exception {
        // Initialize the database
        proxyRepository.save(proxy);
        int databaseSizeBeforeUpdate = proxyRepository.findAll().size();

        // Update the proxy
        Proxy updatedProxy = proxyRepository.findOne(proxy.getId());
        updatedProxy
            .api(UPDATED_API)
            .contentType(UPDATED_CONTENT_TYPE)
            .header(UPDATED_HEADER)
            .dateModified(UPDATED_DATE_MODIFIED);
        ProxyDTO proxyDTO = proxyMapper.toDto(updatedProxy);

        restProxyMockMvc.perform(put("/api/proxies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(proxyDTO)))
            .andExpect(status().isOk());

        // Validate the Proxy in the database
        List<Proxy> proxyList = proxyRepository.findAll();
        assertThat(proxyList).hasSize(databaseSizeBeforeUpdate);
        Proxy testProxy = proxyList.get(proxyList.size() - 1);
        assertThat(testProxy.getApi()).isEqualTo(UPDATED_API);
        assertThat(testProxy.getContentType()).isEqualTo(UPDATED_CONTENT_TYPE);
        assertThat(testProxy.getHeader()).isEqualTo(UPDATED_HEADER);
        assertThat(testProxy.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
    }

    @Test
    public void updateNonExistingProxy() throws Exception {
        int databaseSizeBeforeUpdate = proxyRepository.findAll().size();

        // Create the Proxy
        ProxyDTO proxyDTO = proxyMapper.toDto(proxy);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProxyMockMvc.perform(put("/api/proxies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(proxyDTO)))
            .andExpect(status().isCreated());

        // Validate the Proxy in the database
        List<Proxy> proxyList = proxyRepository.findAll();
        assertThat(proxyList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteProxy() throws Exception {
        // Initialize the database
        proxyRepository.save(proxy);
        int databaseSizeBeforeDelete = proxyRepository.findAll().size();

        // Get the proxy
        restProxyMockMvc.perform(delete("/api/proxies/{id}", proxy.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Proxy> proxyList = proxyRepository.findAll();
        assertThat(proxyList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Proxy.class);
        Proxy proxy1 = new Proxy();
        proxy1.setId("id1");
        Proxy proxy2 = new Proxy();
        proxy2.setId(proxy1.getId());
        assertThat(proxy1).isEqualTo(proxy2);
        proxy2.setId("id2");
        assertThat(proxy1).isNotEqualTo(proxy2);
        proxy1.setId(null);
        assertThat(proxy1).isNotEqualTo(proxy2);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProxyDTO.class);
        ProxyDTO proxyDTO1 = new ProxyDTO();
        proxyDTO1.setId("id1");
        ProxyDTO proxyDTO2 = new ProxyDTO();
        assertThat(proxyDTO1).isNotEqualTo(proxyDTO2);
        proxyDTO2.setId(proxyDTO1.getId());
        assertThat(proxyDTO1).isEqualTo(proxyDTO2);
        proxyDTO2.setId("id2");
        assertThat(proxyDTO1).isNotEqualTo(proxyDTO2);
        proxyDTO1.setId(null);
        assertThat(proxyDTO1).isNotEqualTo(proxyDTO2);
    }
}
