package com.example.musica.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
// o número da faixa só pode repetir em álbuns diferentes
@Table(name = "musicas", uniqueConstraints = @UniqueConstraint(columnNames = { "album_id", "numeroFaixa" }))
public class Musica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private Integer numeroFaixa;

    private Integer duracaoSegundos;

    @ManyToOne(optional = false)
    @JoinColumn(name = "album_id")
    private Album album;

    public Musica() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumeroFaixa() {
        return numeroFaixa;
    }

    public void setNumeroFaixa(Integer numeroFaixa) {
        this.numeroFaixa = numeroFaixa;
    }

    public Integer getDuracaoSegundos() {
        return duracaoSegundos;
    }

    public void setDuracaoSegundos(Integer duracaoSegundos) {
        this.duracaoSegundos = duracaoSegundos;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
}
