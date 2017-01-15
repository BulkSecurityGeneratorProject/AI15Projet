package com.ai15.projet.repository;

import com.ai15.projet.domain.Stats;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Stats entity.
 */
@SuppressWarnings("unused")
public interface StatsRepository extends JpaRepository<Stats,Long> {

}
