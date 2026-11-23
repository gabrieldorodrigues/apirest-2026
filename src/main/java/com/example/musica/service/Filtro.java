package com.example.musica.service;

final class Filtro {

    private Filtro() {
    }

    // monta o padrão do LIKE; sem filtro vira "%" e traz tudo
    static String like(String valor) {
        return (valor == null || valor.isBlank()) ? "%" : "%" + valor.trim().toLowerCase() + "%";
    }
}