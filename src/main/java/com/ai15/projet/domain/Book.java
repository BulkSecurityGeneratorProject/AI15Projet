package com.ai15.projet.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

import com.ai15.projet.domain.enumeration.Booktype;

/**
 * A Book.
 */
@Entity
@Table(name = "book")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "book")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "titre", nullable = false)
    private String titre;

    @NotNull
    @Column(name = "auteur", nullable = false)
    private String auteur;

    @NotNull
    @Column(name = "annee", nullable = false)
    private Integer annee;

    @NotNull
    @Column(name = "prix", nullable = false)
    private Float prix;

    @Column(name = "nb_page")
    private Integer nbPage;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Booktype type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public Book titre(String titre) {
        this.titre = titre;
        return this;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public Book auteur(String auteur) {
        this.auteur = auteur;
        return this;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public Integer getAnnee() {
        return annee;
    }

    public Book annee(Integer annee) {
        this.annee = annee;
        return this;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public Float getPrix() {
        return prix;
    }

    public Book prix(Float prix) {
        this.prix = prix;
        return this;
    }

    public void setPrix(Float prix) {
        this.prix = prix;
    }

    public Integer getNbPage() {
        return nbPage;
    }

    public Book nbPage(Integer nbPage) {
        this.nbPage = nbPage;
        return this;
    }

    public void setNbPage(Integer nbPage) {
        this.nbPage = nbPage;
    }

    public Booktype getType() {
        return type;
    }

    public Book type(Booktype type) {
        this.type = type;
        return this;
    }

    public void setType(Booktype type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Book book = (Book) o;
        if (book.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Book{" +
            "id=" + id +
            ", titre='" + titre + "'" +
            ", auteur='" + auteur + "'" +
            ", annee='" + annee + "'" +
            ", prix='" + prix + "'" +
            ", nbPage='" + nbPage + "'" +
            ", type='" + type + "'" +
            '}';
    }
}
