package service;

import entity.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AgendamentoRepository;
import repository.UsuarioRepository;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor // Gera um construtor com todos os campos final
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AgendamentoRepository agendamentoRepository;

    public Usuario criarUsuario(Usuario novoUsuario) {

        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(novoUsuario.getEmail());

        if (usuarioExistente.isPresent()) {
            throw new IllegalArgumentException("Email já registrado");
        }
        return usuarioRepository.save(novoUsuario);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new IllegalStateException("Usuário não encontrado!")
        );
    }

    @Transactional
    public void excluirUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new IllegalStateException("Usuário não encontrado para exclusão!")
        );
        usuarioRepository.delete(usuario);
    }

    public Usuario editarUsuario(String id, Usuario usuarioComNovosDados) {
        Usuario usuarioExistente = usuarioRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Usuário não encontrado"));

        if (usuarioComNovosDados.getEmail() != null && !usuarioComNovosDados.getEmail().isBlank()
                && !usuarioComNovosDados.getEmail().equals(usuarioExistente.getEmail())) {

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
}