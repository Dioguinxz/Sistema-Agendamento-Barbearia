package com.example.SistemaBarbearia.service;

import com.example.SistemaBarbearia.entity.Usuario;
import com.example.SistemaBarbearia.exceptions.UsuarioNaoEncontradoEmailException;
import com.example.SistemaBarbearia.exceptions.UsuarioRegistradoException;
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
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(novoUsuario.getEmail()); //Optional é responsável por evitar erro de Null Pointer Exception, que ocorre ao tentar usar um objeto que é null

        if (usuarioExistente.isPresent()) {
            throw new UsuarioRegistradoException(novoUsuario.getEmail());
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
        Usuario usuarioExistente = usuarioRepository.findById(id).orElseThrow(() -> new IllegalStateException("Usuário não encontrado"));

        if (usuarioComNovosDados.getEmail() != null && !usuarioComNovosDados.getEmail().isBlank() && !usuarioComNovosDados.getEmail().equals(usuarioExistente.getEmail())) {
            if (usuarioRepository.findByEmail(usuarioComNovosDados.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email já registrado");
            }
            usuarioExistente.setEmail(usuarioComNovosDados.getEmail());
        }

        Optional.ofNullable(usuarioComNovosDados.getNome()).ifPresent(usuarioExistente::setNome);
        Optional.ofNullable(usuarioComNovosDados.getTelefone()).ifPresent(usuarioExistente::setTelefone);

        if (usuarioComNovosDados.getSenha() != null && !usuarioComNovosDados.getSenha().isBlank()) {
            usuarioExistente.setSenha(usuarioComNovosDados.getSenha());
        }
        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public void excluirUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Usuário não encontrado para exclusão!"));
        usuarioRepository.delete(usuario);
    }
}