package com.example.SistemaBarbearia.dto;

/**
 * DTO genérico para padronizar as respostas da API, funcionando como um "envelope".
 *
 * @param <T> O tipo do objeto de dados que será retornado no campo 'dados'.
 */
public record ApiResponseDTO<T>(
        String mensagem,
        T dados
) {
}