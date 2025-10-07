package com.example.SistemaBarbearia.service;

import com.example.SistemaBarbearia.dto.UsuarioResponseDTO;
import com.example.SistemaBarbearia.dto.UsuarioUpdateDTO;
import com.example.SistemaBarbearia.entity.Usuario;
import com.example.SistemaBarbearia.exceptions.*;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.SistemaBarbearia.repository.AgendamentoRepository;
import com.example.SistemaBarbearia.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final PasswordEncoder passwordEncoder;

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

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::new) // Converte cada Usuario em um UsuarioResponseDTO
                .collect(Collectors.toList());
    }


    public UsuarioResponseDTO buscarUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoEmailException(email));
        return new UsuarioResponseDTO(usuario);
    }

    /**
     * Atualiza o perfil do próprio usuário logado.
     */
    public UsuarioResponseDTO atualizarMeuPerfil(UsuarioUpdateDTO dto, Usuario usuarioLogado) {
        // Validação de telefone duplicado, apenas se um novo telefone for enviado
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()) {
            usuarioRepository.findByTelefone(dto.getTelefone()).ifPresent(usuarioEncontrado -> {
                if (!usuarioEncontrado.getId().equals(usuarioLogado.getId())) {
                    throw new UsuarioComTelefoneRegistradoException(dto.getTelefone());
                }
            });
            usuarioLogado.setTelefone(dto.getTelefone());
        }

        // Atualiza o nome, apenas se um novo nome for enviado
        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            usuarioLogado.setNome(dto.getNome());
        }

        // Atualiza a senha, apenas se uma nova senha for enviada
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            String novaSenhaCriptografada = passwordEncoder.encode(dto.getSenha());
            usuarioLogado.setSenha(novaSenhaCriptografada);
        }

        Usuario usuarioSalvo = usuarioRepository.save(usuarioLogado);
        return new UsuarioResponseDTO(usuarioSalvo);
    }

    /**
     * Atualiza qualquer usuário pelo ID (ação de um Barbeiro).
     */
    public UsuarioResponseDTO atualizarUsuarioPeloBarbeiro(String id, UsuarioUpdateDTO dto) {
        Usuario usuarioParaAtualizar = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoIdException(id));

        // Validação de telefone duplicado, apenas se um novo telefone for enviado
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()) {
            usuarioRepository.findByTelefone(dto.getTelefone()).ifPresent(usuarioEncontrado -> {
                if (!usuarioEncontrado.getId().equals(id)) {
                    throw new UsuarioComTelefoneRegistradoException(dto.getTelefone());
                }
            });
            usuarioParaAtualizar.setTelefone(dto.getTelefone());
        }

        // Atualiza o nome, apenas se um novo nome for enviado
        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            usuarioParaAtualizar.setNome(dto.getNome());
        }

        // Atualiza a senha, apenas se uma nova senha for enviada
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            String novaSenhaCriptografada = passwordEncoder.encode(dto.getSenha());
            usuarioParaAtualizar.setSenha(novaSenhaCriptografada);
        }

        Usuario usuarioSalvo = usuarioRepository.save(usuarioParaAtualizar);
        return new UsuarioResponseDTO(usuarioSalvo);
    }


    @Transactional
    public void excluirUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoEmailException(email));
        usuarioRepository.delete(usuario);
    }
}