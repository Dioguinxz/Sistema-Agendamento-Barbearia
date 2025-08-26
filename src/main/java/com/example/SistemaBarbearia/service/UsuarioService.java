package com.example.SistemaBarbearia.service;

import com.example.SistemaBarbearia.entity.Usuario;
import com.example.SistemaBarbearia.exceptions.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.SistemaBarbearia.repository.AgendamentoRepository;
import com.example.SistemaBarbearia.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AgendamentoRepository agendamentoRepository;

    public Usuario criarUsuario(Usuario novoUsuario) {
        Optional<Usuario> usuarioComEmailExistente = usuarioRepository.findByEmail(novoUsuario.getEmail()); //Optional é responsável por evitar erro de Null Pointer Exception, que ocorre ao tentar usar um objeto que é null
        Optional<Usuario> usuarioComTelefoneExistente = usuarioRepository.findByTelefone(novoUsuario.getTelefone());

        if (usuarioComEmailExistente.isPresent()) {
            throw new UsuarioComEmailRegistradoException(novoUsuario.getEmail());
        }

        if (usuarioComTelefoneExistente.isPresent()) {
            throw new UsuarioComTelefoneRegistradoException(novoUsuario.getTelefone());
        }

        return usuarioRepository.save(novoUsuario);
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new UsuarioNaoEncontradoEmailException(email));
    }

    public Usuario editarUsuario(String id, Usuario usuarioComNovosDados) {
        Usuario usuarioExistente = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontradoIdException(id));

        if (usuarioComNovosDados.getTipo() != null && !usuarioComNovosDados.getTipo().equals(usuarioExistente.getTipo())) {
            throw new UsuarioNaoAlteraTipoException();
        }

        if (usuarioComNovosDados.getEmail() != null && !usuarioComNovosDados.getEmail().isBlank() && !usuarioComNovosDados.getEmail().equals(usuarioExistente.getEmail())) {
            if (usuarioRepository.findByEmail(usuarioComNovosDados.getEmail()).isPresent()) {
                throw new UsuarioComEmailRegistradoException(usuarioComNovosDados.getEmail());
            }
            usuarioExistente.setEmail(usuarioComNovosDados.getEmail());
        }

        if (usuarioComNovosDados.getTelefone() != null && !usuarioComNovosDados.getTelefone().isBlank() && !usuarioComNovosDados.getTelefone().equals(usuarioExistente.getTelefone())) {
            if (usuarioRepository.findByTelefone(usuarioComNovosDados.getTelefone()).isPresent()) {
                throw new UsuarioComTelefoneRegistradoException(usuarioComNovosDados.getTelefone());
            }
        }

        Optional.ofNullable(usuarioComNovosDados.getNome()).ifPresent(usuarioExistente::setNome);

        if (usuarioComNovosDados.getSenha() != null && !usuarioComNovosDados.getSenha().isBlank()) {
            usuarioExistente.setSenha(usuarioComNovosDados.getSenha());
        }
        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public void excluirUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new UsuarioNaoEncontradoEmailException(email));
        usuarioRepository.delete(usuario);
    }
}