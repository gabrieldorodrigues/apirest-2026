package com.example.musica.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.musica.dto.UsuarioRequestDto;
import com.example.musica.dto.UsuarioResponseDto;
import com.example.musica.exception.ConflitoException;
import com.example.musica.exception.RecursoNaoEncontradoException;
import com.example.musica.mapper.UsuarioMapper;
import com.example.musica.model.Usuario;
import com.example.musica.repository.AvaliacaoRepository;
import com.example.musica.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, AvaliacaoRepository avaliacaoRepository,
            UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDto> listar(String nome, String email, Pageable pageable) {
        return usuarioRepository.buscar(Filtro.like(nome), Filtro.like(email), pageable)
                .map(usuarioMapper::toDto);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDto buscar(Long id) {
        return usuarioMapper.toDto(buscarEntidade(id));
    }

    @Transactional
    public UsuarioResponseDto criar(UsuarioRequestDto dto) {
        // salva sempre em minúsculo pra "Ana@Email.com" e "ana@email.com" contarem como o mesmo
        String email = dto.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflitoException("Já existe um usuário com o e-mail " + email);
        }
        Usuario usuario = new Usuario(dto.nome(), email);
        return usuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDto atualizar(Long id, UsuarioRequestDto dto) {
        Usuario usuario = buscarEntidade(id);
        String email = dto.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ConflitoException("Já existe um usuário com o e-mail " + email);
        }
        usuario.setNome(dto.nome());
        usuario.setEmail(email);
        return usuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    @Transactional
    public void remover(Long id) {
        Usuario usuario = buscarEntidade(id);
        if (avaliacaoRepository.existsByUsuarioId(id)) {
            throw new ConflitoException("Não é possível remover um usuário que possui avaliações");
        }
        usuarioRepository.delete(usuario);
    }

    Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }
}