package com.ai15.projet.web.rest;

import com.ai15.projet.Ai15App;

import com.ai15.projet.domain.Bibliotheque;
import com.ai15.projet.repository.BibliothequeRepository;
import com.ai15.projet.repository.search.BibliothequeSearchRepository;

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
 * Test class for the BibliothequeResource REST controller.
 *
 * @see BibliothequeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Ai15App.class)
public class BibliothequeResourceIntTest {

    @Inject
    private BibliothequeRepository bibliothequeRepository;

    @Inject
    private BibliothequeSearchRepository bibliothequeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restBibliothequeMockMvc;

    private Bibliotheque bibliotheque;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BibliothequeResource bibliothequeResource = new BibliothequeResource();
        ReflectionTestUtils.setField(bibliothequeResource, "bibliothequeSearchRepository", bibliothequeSearchRepository);
        ReflectionTestUtils.setField(bibliothequeResource, "bibliothequeRepository", bibliothequeRepository);
        this.restBibliothequeMockMvc = MockMvcBuilders.standaloneSetup(bibliothequeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bibliotheque createEntity(EntityManager em) {
        Bibliotheque bibliotheque = new Bibliotheque();
        return bibliotheque;
    }

    @Before
    public void initTest() {
        bibliothequeSearchRepository.deleteAll();
        bibliotheque = createEntity(em);
    }

    @Test
    @Transactional
    public void createBibliotheque() throws Exception {
        int databaseSizeBeforeCreate = bibliothequeRepository.findAll().size();

        // Create the Bibliotheque

        restBibliothequeMockMvc.perform(post("/api/bibliotheques")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bibliotheque)))
            .andExpect(status().isCreated());

        // Validate the Bibliotheque in the database
        List<Bibliotheque> bibliothequeList = bibliothequeRepository.findAll();
        assertThat(bibliothequeList).hasSize(databaseSizeBeforeCreate + 1);
        Bibliotheque testBibliotheque = bibliothequeList.get(bibliothequeList.size() - 1);

        // Validate the Bibliotheque in ElasticSearch
        Bibliotheque bibliothequeEs = bibliothequeSearchRepository.findOne(testBibliotheque.getId());
        assertThat(bibliothequeEs).isEqualToComparingFieldByField(testBibliotheque);
    }

    @Test
    @Transactional
    public void createBibliothequeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bibliothequeRepository.findAll().size();

        // Create the Bibliotheque with an existing ID
        Bibliotheque existingBibliotheque = new Bibliotheque();
        existingBibliotheque.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBibliothequeMockMvc.perform(post("/api/bibliotheques")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingBibliotheque)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Bibliotheque> bibliothequeList = bibliothequeRepository.findAll();
        assertThat(bibliothequeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllBibliotheques() throws Exception {
        // Initialize the database
        bibliothequeRepository.saveAndFlush(bibliotheque);

        // Get all the bibliothequeList
        restBibliothequeMockMvc.perform(get("/api/bibliotheques?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bibliotheque.getId().intValue())));
    }

    @Test
    @Transactional
    public void getBibliotheque() throws Exception {
        // Initialize the database
        bibliothequeRepository.saveAndFlush(bibliotheque);

        // Get the bibliotheque
        restBibliothequeMockMvc.perform(get("/api/bibliotheques/{id}", bibliotheque.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(bibliotheque.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingBibliotheque() throws Exception {
        // Get the bibliotheque
        restBibliothequeMockMvc.perform(get("/api/bibliotheques/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBibliotheque() throws Exception {
        // Initialize the database
        bibliothequeRepository.saveAndFlush(bibliotheque);
        bibliothequeSearchRepository.save(bibliotheque);
        int databaseSizeBeforeUpdate = bibliothequeRepository.findAll().size();

        // Update the bibliotheque
        Bibliotheque updatedBibliotheque = bibliothequeRepository.findOne(bibliotheque.getId());

        restBibliothequeMockMvc.perform(put("/api/bibliotheques")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBibliotheque)))
            .andExpect(status().isOk());

        // Validate the Bibliotheque in the database
        List<Bibliotheque> bibliothequeList = bibliothequeRepository.findAll();
        assertThat(bibliothequeList).hasSize(databaseSizeBeforeUpdate);
        Bibliotheque testBibliotheque = bibliothequeList.get(bibliothequeList.size() - 1);

        // Validate the Bibliotheque in ElasticSearch
        Bibliotheque bibliothequeEs = bibliothequeSearchRepository.findOne(testBibliotheque.getId());
        assertThat(bibliothequeEs).isEqualToComparingFieldByField(testBibliotheque);
    }

    @Test
    @Transactional
    public void updateNonExistingBibliotheque() throws Exception {
        int databaseSizeBeforeUpdate = bibliothequeRepository.findAll().size();

        // Create the Bibliotheque

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBibliothequeMockMvc.perform(put("/api/bibliotheques")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bibliotheque)))
            .andExpect(status().isCreated());

        // Validate the Bibliotheque in the database
        List<Bibliotheque> bibliothequeList = bibliothequeRepository.findAll();
        assertThat(bibliothequeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBibliotheque() throws Exception {
        // Initialize the database
        bibliothequeRepository.saveAndFlush(bibliotheque);
        bibliothequeSearchRepository.save(bibliotheque);
        int databaseSizeBeforeDelete = bibliothequeRepository.findAll().size();

        // Get the bibliotheque
        restBibliothequeMockMvc.perform(delete("/api/bibliotheques/{id}", bibliotheque.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean bibliothequeExistsInEs = bibliothequeSearchRepository.exists(bibliotheque.getId());
        assertThat(bibliothequeExistsInEs).isFalse();

        // Validate the database is empty
        List<Bibliotheque> bibliothequeList = bibliothequeRepository.findAll();
        assertThat(bibliothequeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBibliotheque() throws Exception {
        // Initialize the database
        bibliothequeRepository.saveAndFlush(bibliotheque);
        bibliothequeSearchRepository.save(bibliotheque);

        // Search the bibliotheque
        restBibliothequeMockMvc.perform(get("/api/_search/bibliotheques?query=id:" + bibliotheque.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bibliotheque.getId().intValue())));
    }
}
