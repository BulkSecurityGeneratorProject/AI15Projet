package com.ai15.projet.web.rest;

import com.ai15.projet.Ai15App;

import com.ai15.projet.domain.Stats;
import com.ai15.projet.repository.StatsRepository;
import com.ai15.projet.repository.search.StatsSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the StatsResource REST controller.
 *
 * @see StatsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Ai15App.class)
public class StatsResourceIntTest {

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    @Inject
    private StatsRepository statsRepository;

    @Inject
    private StatsSearchRepository statsSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restStatsMockMvc;

    private Stats stats;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StatsResource statsResource = new StatsResource();
        ReflectionTestUtils.setField(statsResource, "statsSearchRepository", statsSearchRepository);
        ReflectionTestUtils.setField(statsResource, "statsRepository", statsRepository);
        this.restStatsMockMvc = MockMvcBuilders.standaloneSetup(statsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stats createEntity(EntityManager em) {
        Stats stats = new Stats()
                .titre(DEFAULT_TITRE);
        return stats;
    }

    @Before
    public void initTest() {
        statsSearchRepository.deleteAll();
        stats = createEntity(em);
    }

    @Test
    @Transactional
    public void createStats() throws Exception {
        int databaseSizeBeforeCreate = statsRepository.findAll().size();

        // Create the Stats

        restStatsMockMvc.perform(post("/api/stats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stats)))
            .andExpect(status().isCreated());

        // Validate the Stats in the database
        List<Stats> statsList = statsRepository.findAll();
        assertThat(statsList).hasSize(databaseSizeBeforeCreate + 1);
        Stats testStats = statsList.get(statsList.size() - 1);
        assertThat(testStats.getTitre()).isEqualTo(DEFAULT_TITRE);

        // Validate the Stats in ElasticSearch
        Stats statsEs = statsSearchRepository.findOne(testStats.getId());
        assertThat(statsEs).isEqualToComparingFieldByField(testStats);
    }

    @Test
    @Transactional
    public void createStatsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = statsRepository.findAll().size();

        // Create the Stats with an existing ID
        Stats existingStats = new Stats();
        existingStats.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatsMockMvc.perform(post("/api/stats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingStats)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Stats> statsList = statsRepository.findAll();
        assertThat(statsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllStats() throws Exception {
        // Initialize the database
        statsRepository.saveAndFlush(stats);

        // Get all the statsList
        restStatsMockMvc.perform(get("/api/stats?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stats.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE.toString())));
    }

    @Test
    @Transactional
    public void getStats() throws Exception {
        // Initialize the database
        statsRepository.saveAndFlush(stats);

        // Get the stats
        restStatsMockMvc.perform(get("/api/stats/{id}", stats.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(stats.getId().intValue()))
            .andExpect(jsonPath("$.titre").value(DEFAULT_TITRE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStats() throws Exception {
        // Get the stats
        restStatsMockMvc.perform(get("/api/stats/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStats() throws Exception {
        // Initialize the database
        statsRepository.saveAndFlush(stats);
        statsSearchRepository.save(stats);
        int databaseSizeBeforeUpdate = statsRepository.findAll().size();

        // Update the stats
        Stats updatedStats = statsRepository.findOne(stats.getId());
        updatedStats
                .titre(UPDATED_TITRE);

        restStatsMockMvc.perform(put("/api/stats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedStats)))
            .andExpect(status().isOk());

        // Validate the Stats in the database
        List<Stats> statsList = statsRepository.findAll();
        assertThat(statsList).hasSize(databaseSizeBeforeUpdate);
        Stats testStats = statsList.get(statsList.size() - 1);
        assertThat(testStats.getTitre()).isEqualTo(UPDATED_TITRE);

        // Validate the Stats in ElasticSearch
        Stats statsEs = statsSearchRepository.findOne(testStats.getId());
        assertThat(statsEs).isEqualToComparingFieldByField(testStats);
    }

    @Test
    @Transactional
    public void updateNonExistingStats() throws Exception {
        int databaseSizeBeforeUpdate = statsRepository.findAll().size();

        // Create the Stats

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restStatsMockMvc.perform(put("/api/stats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stats)))
            .andExpect(status().isCreated());

        // Validate the Stats in the database
        List<Stats> statsList = statsRepository.findAll();
        assertThat(statsList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteStats() throws Exception {
        // Initialize the database
        statsRepository.saveAndFlush(stats);
        statsSearchRepository.save(stats);
        int databaseSizeBeforeDelete = statsRepository.findAll().size();

        // Get the stats
        restStatsMockMvc.perform(delete("/api/stats/{id}", stats.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean statsExistsInEs = statsSearchRepository.exists(stats.getId());
        assertThat(statsExistsInEs).isFalse();

        // Validate the database is empty
        List<Stats> statsList = statsRepository.findAll();
        assertThat(statsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchStats() throws Exception {
        // Initialize the database
        statsRepository.saveAndFlush(stats);
        statsSearchRepository.save(stats);

        // Search the stats
        restStatsMockMvc.perform(get("/api/_search/stats?query=id:" + stats.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stats.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE.toString())));
    }
}
