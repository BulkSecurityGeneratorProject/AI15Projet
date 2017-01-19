package com.ai15.projet.repository;

import com.ai15.projet.domain.Bibliotheque;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Bibliotheque entity.
 */
@SuppressWarnings("unused")
public interface BibliothequeRepository extends JpaRepository<Bibliotheque,Long> {

    @Query("select bibliotheque from Bibliotheque bibliotheque where bibliotheque.user.login = ?#{principal.username}")
    List<Bibliotheque> findByUserIsCurrentUser();

}
