package com.ai15.projet.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ai15.projet.domain.Bibliotheque;

import com.ai15.projet.repository.BibliothequeRepository;
import com.ai15.projet.repository.search.BibliothequeSearchRepository;
import com.ai15.projet.web.rest.util.HeaderUtil;
import com.ai15.projet.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Bibliotheque.
 */
@RestController
@RequestMapping("/api")
public class BibliothequeResource {

    private final Logger log = LoggerFactory.getLogger(BibliothequeResource.class);
        
    @Inject
    private BibliothequeRepository bibliothequeRepository;

    @Inject
    private BibliothequeSearchRepository bibliothequeSearchRepository;

    /**
     * POST  /bibliotheques : Create a new bibliotheque.
     *
     * @param bibliotheque the bibliotheque to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bibliotheque, or with status 400 (Bad Request) if the bibliotheque has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/bibliotheques")
    @Timed
    public ResponseEntity<Bibliotheque> createBibliotheque(@RequestBody Bibliotheque bibliotheque) throws URISyntaxException {
        log.debug("REST request to save Bibliotheque : {}", bibliotheque);
        if (bibliotheque.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("bibliotheque", "idexists", "A new bibliotheque cannot already have an ID")).body(null);
        }
        Bibliotheque result = bibliothequeRepository.save(bibliotheque);
        bibliothequeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/bibliotheques/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("bibliotheque", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bibliotheques : Updates an existing bibliotheque.
     *
     * @param bibliotheque the bibliotheque to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bibliotheque,
     * or with status 400 (Bad Request) if the bibliotheque is not valid,
     * or with status 500 (Internal Server Error) if the bibliotheque couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/bibliotheques")
    @Timed
    public ResponseEntity<Bibliotheque> updateBibliotheque(@RequestBody Bibliotheque bibliotheque) throws URISyntaxException {
        log.debug("REST request to update Bibliotheque : {}", bibliotheque);
        if (bibliotheque.getId() == null) {
            return createBibliotheque(bibliotheque);
        }
        Bibliotheque result = bibliothequeRepository.save(bibliotheque);
        bibliothequeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("bibliotheque", bibliotheque.getId().toString()))
            .body(result);
    }

    /**
     * GET  /bibliotheques : get all the bibliotheques.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of bibliotheques in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/bibliotheques")
    @Timed
    public ResponseEntity<List<Bibliotheque>> getAllBibliotheques(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Bibliotheques");
        Page<Bibliotheque> page = bibliothequeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bibliotheques");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /bibliotheques/:id : get the "id" bibliotheque.
     *
     * @param id the id of the bibliotheque to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the bibliotheque, or with status 404 (Not Found)
     */
    @GetMapping("/bibliotheques/{id}")
    @Timed
    public ResponseEntity<Bibliotheque> getBibliotheque(@PathVariable Long id) {
        log.debug("REST request to get Bibliotheque : {}", id);
        Bibliotheque bibliotheque = bibliothequeRepository.findOne(id);
        return Optional.ofNullable(bibliotheque)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /bibliotheques/:id : delete the "id" bibliotheque.
     *
     * @param id the id of the bibliotheque to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/bibliotheques/{id}")
    @Timed
    public ResponseEntity<Void> deleteBibliotheque(@PathVariable Long id) {
        log.debug("REST request to delete Bibliotheque : {}", id);
        bibliothequeRepository.delete(id);
        bibliothequeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("bibliotheque", id.toString())).build();
    }

    /**
     * SEARCH  /_search/bibliotheques?query=:query : search for the bibliotheque corresponding
     * to the query.
     *
     * @param query the query of the bibliotheque search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/bibliotheques")
    @Timed
    public ResponseEntity<List<Bibliotheque>> searchBibliotheques(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Bibliotheques for query {}", query);
        Page<Bibliotheque> page = bibliothequeSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/bibliotheques");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
