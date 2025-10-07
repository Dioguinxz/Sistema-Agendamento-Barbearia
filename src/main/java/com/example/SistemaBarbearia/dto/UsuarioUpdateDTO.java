package com.example.SistemaBarbearia.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioUpdateDTO {

    // Os campos não são mais obrigatórios individualmente
    private String nome;
    private String telefone;

    @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres")
    private String senha;

    /**
     * Método de validação customizado.
     * O Spring irá executar este método e, se ele retornar 'false', a validação falha.
     */
    @AssertTrue(message = "Pelo menos um campo (nome, telefone ou senha) deve ser preenchido para atualização.")
    @JsonIgnore // Garante que este método não apareça no JSON de resposta
    public boolean isAtLeastOneFieldPresent() {
        // Retorna true se pelo menos um dos campos foi preenchido
        return (nome != null && !nome.isBlank()) ||
                (telefone != null && !telefone.isBlank()) ||
                (senha != null && !senha.isBlank());
    }
}