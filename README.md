# Plataforma BAAS (Banking as a Service)
## Visão Geral
A Plataforma BAAS é uma solução completa de Banking as a Service que oferece funcionalidades bancárias essenciais através de uma arquitetura modular e extensível. Esta plataforma permite que empresas de tecnologia financeira (fintechs) e outras instituições ofereçam serviços financeiros sem precisar desenvolver toda a infraestrutura bancária desde o início.
## Funcionalidades Principais
- **Gerenciamento de Usuários**: Cadastro, autenticação e gerenciamento de perfis de usuários
- **Contas Bancárias**: Criação e gerenciamento de contas com senha protegida
- **Transferências**: Transações seguras entre usuários do sistema
- **Validação de Operações**: Verificação de saldo, autenticação de senha e outras medidas de segurança

## Arquitetura
A plataforma é construída seguindo princípios de Clean Architecture e Domain-Driven Design, organizados nas seguintes camadas:
### Domínio (Domain)
- **Modelos**: Entidades centrais do negócio (User, Account, Transaction)
- **Repositórios**: Interfaces para acesso a dados
- **Exceções**: Tipos de erros específicos do domínio

### Aplicação (Application)
- **Casos de Uso**: Implementação da lógica de negócio
- **Serviços**: Orquestração de operações complexas

### Infraestrutura (Infrastructure)
- **Persistência**: Implementação dos repositórios com Spring Data JPA
- **Segurança**: Mecanismos de autenticação e autorização

### Apresentação (Presentation)
- **Controladores REST**: Endpoints da API
- **DTOs**: Objetos de transferência de dados

## Tecnologias Utilizadas
- **Java 24**: Linguagem de programação base
- **Spring Boot**: Framework para desenvolvimento de aplicações
- **Spring Data JPA**: Camada de persistência simplificada
- **Jakarta EE**: Especificações para desenvolvimento de aplicações empresariais
- **JUnit 5**: Framework de testes
- : Framework para criação de mocks em testes **Mockito**

## Exemplos de Uso
### Transferência entre Usuários
A plataforma permite transferências entre usuários através de e-mail ou CPF:
``` java
// Exemplo de requisição para transferência
UserTransactionRequest request = new UserTransactionRequest(
    "sender@example.com",     // E-mail do remetente
    "receiver@example.com",   // E-mail do destinatário
    new BigDecimal("100.00"), // Valor da transferência
    "senha123",               // Senha do remetente
    false                     // Identificação por e-mail (false) ou CPF (true)
);

// Processamento da transação
Transaction transaction = userTransactionUseCase.execute(request);
```
### Validações Implementadas
O sistema realiza diversas validações durante as transações:
- Existência dos usuários (remetente e destinatário)
- Existência de contas associadas aos usuários
- Validação de senha
- Verificação de saldo suficiente
- Validação de valor positivo para transferência

## Desenvolvimento e Testes
### Configuração do Ambiente
1. Clone o repositório
2. Configure o Java 24
3. Importe o projeto como Maven/Gradle
4. Execute os testes unitários para verificar a configuração

### Execução de Testes
O projeto utiliza JUnit 5 e Mockito para testes unitários:
``` bash
# Executar todos os testes
./mvnw test

# Executar uma classe de teste específica
./mvnw test -Dtest=UserTransactionUseCaseTest
```
### Boas Práticas de Teste
- Todos os testes devem ser independentes
- Use mocks para isolar componentes
- Verifique tanto casos de sucesso quanto casos de erro
- Mantenha os stubs limpos, evitando configurações desnecessárias

## Contribuição
1. Fork o repositório
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## Documentação com Swagger

Acesse http://localhost:6789/swagger-ui/index.html

## Licença
Este projeto está licenciado sob a Licença MIT - veja o arquivo LICENSE para detalhes.
## Contato
Para mais informações, entre em contato comigo atraves do email vinicius.devjvm@gmail.com
