# ChatBot Easy - Squad 10

## Descrição
Chatbot para WhatsApp usando a API da Meta e Spring Boot.  
Recebe mensagens via webhook, extrai informações relevantes, processa a lógica e responde automaticamente.  
Inclui integração com a API da Omie e endpoints para gerenciar números cadastrados.

---

## Tecnologias Utilizadas

- **Java 17+**
- **Spring Boot**
- **Spring Web**
- **Spring Data JPA**
- **MySQL** como banco de dados
- **WhatsApp Cloud API (Meta)**
- **Integração com API Omie**

---

## Estrutura do projeto

- **controller**
  - `WebhookController`: Recebe e valida os webhooks enviados pela Meta.

- **dto**
  - `IncomingMessage`: Representação do payload recebido da Meta.
  - `CriarNumeroRequest`: DTO para criação de números cadastrados.
  - `AtualizarNumeroRequest`: DTO para atualização de números cadastrados.
  - `NumCadastradoGet`: DTO de retorno de números cadastrados.

- **service**
  - `MessageExtractor`: Extrai dados essenciais do payload (conteúdo, tipo, remetente, etc.).
  - `ChatLogic`: Define a resposta com base nas informações extraídas.
  - `SendMessage`: Envia mensagens para a API do WhatsApp.
  - `OmieDataService`: Consome a API da Omie.
  - Serviços relacionados ao CRUD de números cadastrados.

---

## Como Rodar

1. **Configurar o arquivo de propriedades**
   - Renomeie `application.properties.template` para `application.properties`.
   - Preencha os campos obrigatórios (tokens da Meta, URL base, credenciais da Omie, configurações do MySQL etc.).

2. **Configurar o Webhook no WhatsApp Cloud API**
   - Acesse o painel da Meta Developers.
   - Cadastre o endpoint do projeto como webhook.
   - Ative o evento de “messages”.

3. **Testar o bot**
   - Envie uma mensagem de um número válido para o número configurado no WhatsApp Cloud API.
   - O bot deve receber o webhook, processar e responder.

---

## Branches

- **main**: Código estável, pronto para produção.
- **develop**: Desenvolvimento contínuo.
- **feature/nome-da-funcionalidade**: Novas funcionalidades.
