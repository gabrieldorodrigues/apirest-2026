package com.example.musica.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.musica.dto.UsuarioRequestDto;
import com.example.musica.dto.UsuarioResponseDto;
import com.example.musica.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Usuários")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Lista usuários", description = "Filtros opcionais e combináveis. Ordenação padrão: `nome`.")
    @ApiResponse(responseCode = "200", description = "Página de usuários")
    @ApiResponse(responseCode = "400", description = "Campo de `sort` inexistente")
    @GetMapping
    public Page<UsuarioResponseDto> listar(
            @Parameter(description = "Parte do nome, sem diferenciar maiúsculas", example = "ana")
            @RequestParam(required = false) String nome,
            @Parameter(description = "Parte do e-mail, sem diferenciar maiúsculas", example = "email.com")
            @RequestParam(required = false) String email,
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return usuarioService.listar(nome, email, pageable);
    }

    @Operation(summary = "Busca um usuário pelo id")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @ApiResponse(responseCode = "400", description = "Id inválido na rota")
    @ApiResponse(responseCode = "404", description = "Usuário não existe")
    @GetMapping("/{id}")
    public UsuarioResponseDto buscar(@Parameter(description = "Id do usuário", example = "1") @PathVariable Long id) {
        return usuarioService.buscar(id);
    }

    @Operation(summary = "Cadastra um usuário", description = "O e-mail é único e é salvo em minúsculo.")
    @ApiResponse(responseCode = "201", description = "Usuário criado")
    @ApiResponse(responseCode = "400", description = "Campos inválidos ou JSON mal formatado")
    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDto criar(@RequestBody @Valid UsuarioRequestDto dto) {
        return usuarioService.criar(dto);
    }

    @Operation(summary = "Atualiza um usuário")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado")
    @ApiResponse(responseCode = "400", description = "Campos inválidos ou JSON mal formatado")
    @ApiResponse(responseCode = "404", description = "Usuário não existe")
    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado por outro usuário")
    @PutMapping("/{id}")
    public UsuarioResponseDto atualizar(@Parameter(description = "Id do usuário", example = "1") @PathVariable Long id,
            @RequestBody @Valid UsuarioRequestDto dto) {
        return usuarioService.atualizar(id, dto);
    }

    @Operation(summary = "Remove um usuário", description = "Só é possível remover usuários sem avaliações.")
    @ApiResponse(responseCode = "204", description = "Usuário removido")
    @ApiResponse(responseCode = "404", description = "Usuário não existe")
    @ApiResponse(responseCode = "409", description = "Usuário possui avaliações")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@Parameter(description = "Id do usuário", example = "1") @PathVariable Long id) {
        usuarioService.remover(id);
    }
}
