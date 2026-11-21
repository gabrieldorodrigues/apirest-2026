package com.example.musica.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.musica.model.Genero;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, Long> {
    Page<Genero> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}