package com.ai15.projet.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ai15.projet.domain.Stats;

import com.ai15.projet.repository.StatsRepository;
import com.ai15.projet.repository.search.StatsSearchRepository;
import com.ai15.projet.web.rest.util.HeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing Stats.
 */
@RestController
@RequestMapping("/api")
public class StatsResource {

    private final Logger log = LoggerFactory.getLogger(StatsResource.class);
        
    @Inject
    private StatsRepository statsRepository;

    @Inject
    private StatsSearchRepository statsSearchRepository;

    /**
     * POST  /stats : Create a new stats.
     *
     * @param stats the stats to create
     * @return the ResponseEntity with status 201 (Created) and with body the new stats, or with status 400 (Bad Request) if the stats has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/stats")
    @Timed
    public ResponseEntity<Stats> createStats(@RequestBody Stats stats) throws URISyntaxException {
        log.debug("REST request to save Stats : {}", stats);
        if (stats.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("stats", "idexists", "A new stats cannot already have an ID")).body(null);
        }
        Stats result = statsRepository.save(stats);
        statsSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/stats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("stats", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /stats : Updates an existing stats.
     *
     * @param stats the stats to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated stats,
     * or with status 400 (Bad Request) if the stats is not valid,
     * or with status 500 (Internal Server Error) if the stats couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/stats")
    @Timed
    public ResponseEntity<Stats> updateStats(@RequestBody Stats stats) throws URISyntaxException {
        log.debug("REST request to update Stats : {}", stats);
        if (stats.getId() == null) {
            return createStats(stats);
        }
        Stats result = statsRepository.save(stats);
        statsSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("stats", stats.getId().toString()))
            .body(result);
    }

    /**
     * GET  /stats : get all the stats.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of stats in body
     */
    @GetMapping("/stats")
    @Timed
    public List<Stats> getAllStats() {
        log.debug("REST request to get all Stats");
        List<Stats> stats = statsRepository.findAll();
        return stats;
    }

    /**
     * GET  /stats/:id : get the "id" stats.
     *
     * @param id the id of the stats to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the stats, or with status 404 (Not Found)
     */
    @GetMapping("/stats/{id}")
    @Timed
    public ResponseEntity<Stats> getStats(@PathVariable Long id) {
        log.debug("REST request to get Stats : {}", id);
        Stats stats = statsRepository.findOne(id);
        return Optional.ofNullable(stats)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /stats/:id : delete the "id" stats.
     *
     * @param id the id of the stats to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/stats/{id}")
    @Timed
    public ResponseEntity<Void> deleteStats(@PathVariable Long id) {
        log.debug("REST request to delete Stats : {}", id);
        statsRepository.delete(id);
        statsSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("stats", id.toString())).build();
    }

    /**
     * SEARCH  /_search/stats?query=:query : search for the stats corresponding
     * to the query.
     *
     * @param query the query of the stats search 
     * @return the result of the search
     */
    @GetMapping("/_search/stats")
    @Timed
    public List<Stats> searchStats(@RequestParam String query) {
        log.debug("REST request to search Stats for query {}", query);
        return StreamSupport
            .stream(statsSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
