package com.ai15.projet.repository.search;

import com.ai15.projet.domain.Bibliotheque;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Bibliotheque entity.
 */
public interface BibliothequeSearchRepository extends ElasticsearchRepository<Bibliotheque, Long> {
}
