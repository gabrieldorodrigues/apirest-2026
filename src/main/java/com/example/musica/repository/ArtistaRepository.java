package com.example.musica.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.musica.model.Artista;

@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Long> {

    @Query("""
            select a from Artista a
            where lower(a.nome) like :nome
              and lower(coalesce(a.pais, '')) like :pais
            """)
    Page<Artista> buscar(String nome, String pais, Pageable pageable);
}