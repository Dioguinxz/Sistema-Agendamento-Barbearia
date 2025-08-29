# 💈 - Sistema de Agendamento para Barbearia


API RESTful em desenvolvimento com **Java e Spring Boot** para gerenciar agendamentos em uma barbearia.

---
## Funcionalidades

### Cliente
* Cadastro e Login (com JWT)
* Visualizar horários disponíveis
* Agendar, editar e cancelar seus próprios horários

### Barbeiro / Admin
* Login com acesso admin
* Visualizar e filtrar **todos** os agendamentos
* Gerenciar o status dos agendamentos (concluído, cancelado)
* Gerenciar a disponibilidade da agenda

---
## Tecnologias

* **Java** & **Spring Boot**
* **Spring Security** (Autenticação com JWT)
* **MongoDB** (Banco de Dados NoSQL)
* **Maven**

---

## Status do Desenvolvimento

- [x] Estrutura do projeto com Spring Boot.
- [x] CRUD de Usuários e sistema de autenticação/autorização com JWT.
- [x] Tratamento de exceções global.
- [ ] **Em Andamento:** Implementação da lógica de Agendamentos.
- [ ] **Próximos Passos:** Notificações e frontend.
