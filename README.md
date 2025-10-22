# ChatBot Easy - Squad 10

## Descrição
Este projeto é um chatbot para WhatsApp utilizando a API da Meta e Spring Boot.  
Ele recebe mensagens via webhook, processa o conteúdo e responde automaticamente.

---

## Estrutura do projeto

- **controller**: Recebe os webhooks da Meta (`WebhookController`)  
- **dto**: Contém os objetos que representam o payload do webhook (`IncomingMessage`)  
- **service**
  - `ChatLogic`: Processa as mensagens recebidas e decide a resposta  
  - `SendMessage`: Envia mensagens para a API do WhatsApp  

---

## Branches

- **main**: Branch principal, sempre com código estável e pronto para produção  
- **develop**: Branch de desenvolvimento, para testar novas funcionalidades  
- **feature/nome-da-funcionalidade**: Branches de funcionalidades específicas  


