package com.example.SistemaBarbearia.controller;

import com.example.SistemaBarbearia.entity.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED) // Retorna status 201 Created
    public Usuario criarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.criarUsuario(usuario);
    }

    /**
     * Endpoint para listar todos os usuários.
     * Mapeado para o verbo HTTP GET na URL base.
     * @return Uma lista de todos os usuários cadastrados.
     */
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarTodosUsuarios();
    }

    /**
     * Endpoint para buscar um usuário por email.
     * Mapeado para o verbo HTTP GET com um parâmetro de requisição.
     * @param email O email do usuário, vindo de um query parameter na URL.
     * @return O objeto Usuario encontrado e status 200 OK.
     */
    @GetMapping(params = "email")
    public Usuario buscarUsuarioPorEmail(@RequestParam String email) {
        return usuarioService.buscarUsuarioPorEmail(email);
    }

    /**
     * Endpoint para editar um usuário existente.
     * Mapeado para o verbo HTTP PUT com o ID do usuário na URL.
     * @param id O ID do usuário a ser editado, vindo do path da URL.
     * @param usuario O objeto Usuario com os novos dados, vindo do corpo da requisição.
     * @return O objeto Usuario atualizado e status 200 OK.
     */
    @PutMapping("/{id}")
    public Usuario editarUsuario(@PathVariable String id, @RequestBody Usuario usuario) {
        return usuarioService.editarUsuario(id, usuario);
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
