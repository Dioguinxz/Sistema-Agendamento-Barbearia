package com.example.SistemaBarbearia.controller;

import com.example.SistemaBarbearia.dto.AuthenticationDTO;
import com.example.SistemaBarbearia.dto.EmailResponseDTO;
import com.example.SistemaBarbearia.dto.RegisterDTO;
import com.example.SistemaBarbearia.entity.Usuario;
import com.example.SistemaBarbearia.exceptions.UsuarioComEmailRegistradoException;
import com.example.SistemaBarbearia.exceptions.UsuarioComTelefoneRegistradoException;
import com.example.SistemaBarbearia.repository.UsuarioRepository;
import com.example.SistemaBarbearia.security.TokenService;
import com.example.SistemaBarbearia.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder; // 1. Importe o PasswordEncoder
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.gerarToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new EmailResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Validated RegisterDTO data) {

        if (this.usuarioRepository.findByEmail(data.email()).isPresent()) {
            throw new UsuarioComEmailRegistradoException(data.email());
        }

        if (this.usuarioRepository.findByTelefone(data.telefone()).isPresent()) {
            throw new UsuarioComTelefoneRegistradoException(data.telefone());

        }

        String senhaCriptografada = passwordEncoder.encode(data.senha());
        Usuario novoUsuario = new Usuario(data.nome(), data.email(), data.telefone(), senhaCriptografada, data.tipoUsuario());

        this.usuarioRepository.save(novoUsuario);
        emailService.enviarEmailBoasVindas(novoUsuario);
        return ResponseEntity.ok().build();
    }
}