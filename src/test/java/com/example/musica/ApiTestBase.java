package com.example.musica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base dos testes de integração: sobe a API inteira com H2 em memória e os dados do SeedConfig.
 * Cada teste roda numa transação desfeita no final, então um teste não interfere no outro.
 *
 * Ids do seed usados nos testes:
 * usuários 1 Gabriel, 2 Ana, 3 João; artistas 1 Radiohead, 4 Os Mutantes (sem álbuns);
 * gêneros 1 Rock, 2 Alternativo; álbuns 1 OK Computer, 2 In Rainbows, 4 Kind of Blue;
 * músicas 1 Paranoid Android (faixa 2 do álbum 1), 4 Reckoner.
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:api-teste")
@AutoConfigureMockMvc
@Transactional
public abstract class ApiTestBase {

    @Autowired
    protected MockMvc mockMvc;
}
