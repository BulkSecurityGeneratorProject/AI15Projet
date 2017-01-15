package com.ai15.projet.repository.search;

import com.ai15.projet.domain.Stats;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Stats entity.
 */
public interface StatsSearchRepository extends ElasticsearchRepository<Stats, Long> {
}
