package com.example.SistemaBarbearia.controller;

import com.example.SistemaBarbearia.dto.UsuarioResponseDTO;
import com.example.SistemaBarbearia.dto.UsuarioUpdateDTO;
import com.example.SistemaBarbearia.entity.Usuario;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.SistemaBarbearia.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Endpoint para criar um novo usuário.
     * Mapeado para o verbo HTTP POST.
     * @param usuario O objeto Usuario a ser criado, vindo do corpo da requisição.
     * @return ResponseEntity com o Usuario criado e status 201 Created.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario criarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.criarUsuario(usuario);
    }

    /**
     * Endpoint para listar todos os usuários.
     * Mapeado para o verbo HTTP GET na URL base.
     * @return Uma lista de todos os usuários cadastrados.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('BARBEIRO')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodosUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Endpoint para buscar um usuário por email.
     * Mapeado para o verbo HTTP GET com um parâmetro de requisição.
     * @param email O email do usuário, vindo de um query parameter na URL.
     * @return O objeto Usuario encontrado e status 200 OK.
     */
    @GetMapping(params = "email")
    @PreAuthorize("hasAuthority('BARBEIRO')")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorEmail(@RequestParam String email) {
        UsuarioResponseDTO usuario = usuarioService.buscarUsuarioPorEmail(email);
        return ResponseEntity.ok(usuario);
    }
    /**
     * Endpoint para um usuário (CLIENTE ou BARBEIRO) atualizar SEU PRÓPRIO perfil.
     */
    @PutMapping("/me")
    @PreAuthorize("hasAuthority('CLIENTE')") // Permite que CLIENTE e BARBEIRO atualizem a si mesmos
    public ResponseEntity<UsuarioResponseDTO> atualizarMeuPerfil(
            @RequestBody @Valid UsuarioUpdateDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        UsuarioResponseDTO usuarioAtualizado = usuarioService.atualizarMeuPerfil(dto, usuarioLogado);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    /**
     * Endpoint EXCLUSIVO para o BARBEIRO atualizar QUALQUER usuário pelo ID.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BARBEIRO')") // Apenas BARBEIROS podem acessar este endpoint
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuarioPeloBarbeiro(
            @PathVariable String id,
            @RequestBody @Valid UsuarioUpdateDTO dto) {

        UsuarioResponseDTO usuarioAtualizado = usuarioService.atualizarUsuarioPeloBarbeiro(id, dto);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    /**
     * Endpoint para excluir um usuário por email.
     * Mapeado para o verbo HTTP DELETE com o email do usuário na URL.
     * @param email O email do usuário a ser excluído, vindo do path da URL.
     * @return Resposta sem conteúdo e status 204 No Content.
     */
    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirUsuarioPorEmail(@PathVariable String email) {
        usuarioService.excluirUsuarioPorEmail(email);
    }

}
