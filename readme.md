# Autorizador de Cartões
## Visão Geral
Este projeto é um sistema de autorização de transações para cartões, desenvolvido com Spring Boot, Java 24 e MySQL. A aplicação permite criar cartões, consultar saldos e autorizar transações financeiras, simulando o comportamento de um autorizador de cartões de débito ou pré-pago.
## Funcionalidades
- **Cadastro de Cartões**: Cria novos cartões com número, senha e saldo inicial de R$ 500,00
- **Consulta de Saldo**: Verifica o saldo disponível em um cartão
- **Autorização de Transações**: Valida e autoriza transações com base em regras de negócio

## Tecnologias Utilizadas
- **Backend**: Spring Boot 3.5.3, Java 24
- **Persistência**: Spring Data JPA, MySQL 5.7
- **Containerização**: Docker e Docker Compose
- **Testes**: JUnit, Mockito

## Estrutura do Projeto
``` 
├── src/
│   ├── main/
│   │   ├── java/com/bank/authorizer/
│   │   │   ├── api/
│   │   │   │   ├── controller/    # Controladores REST
│   │   │   │   ├── dto/           # Objetos de transferência de dados
│   │   │   │   ├── exception/     # Tratamento de exceções da API
│   │   │   ├── config/            # Configurações da aplicação
│   │   │   ├── domain/
│   │   │   │   ├── enums/         # Enumerações de domínio
│   │   │   │   ├── model/         # Entidades do domínio
│   │   │   │   ├── repository/    # Repositórios JPA
│   │   │   │   ├── service/       # Lógica de negócio
│   │   │   ├── AuthorizerApplication.java
│   │   ├── resources/
│   │   │   ├── application.properties  # Configurações da aplicação
│   ├── test/                      # Testes unitários e de integração
├── docker-compose.yml             # Configuração do ambiente Docker
├── .env                           # Variáveis de ambiente
├── pom.xml                        # Dependências Maven
└── README.md                      # Este arquivo
```
## Modelo de Dados
### Entidade Cartao
- : Identificador único (UUID) **id**
- : Número do cartão (16 dígitos) **numeroCartao**
- **senha**: Senha do cartão
- **saldo**: Saldo disponível (padrão: R$ 500,00)

### Entidade Transacao
- : Identificador único (UUID) **id**
- : Número do cartão **numeroCartao**
- **senha**: Senha usada na transação
- : Valor da transação **valor**

## Regras de Negócio
1. **Criação de Cartão**:
    - Cartões são criados com saldo inicial de R$ 500,00
    - Não é permitido criar cartões com números duplicados (mesmo número)

2. **Autorização de Transação**:
    - A transação será negada se o cartão não existir (CARTAO_INEXISTENTE)
    - A transação será negada se a senha estiver incorreta (SENHA_INVALIDA)
    - A transação será negada se o saldo for insuficiente (SALDO_INSUFICIENTE)
    - Em caso de sucesso, o valor da transação é debitado do saldo do cartão (OK)

## API REST
### Endpoints
#### Cartões
- **POST /cartoes** - Cria um novo cartão
    - Corpo da requisição:
``` json
    {
        "numeroCartao": "6549873025634501",
        "senha": "1234"
    }
```
- Respostas:
    - 201 Created: Cartão criado com sucesso
``` json
      {
         "numeroCartao": "6549873025634501",
         "senha": "1234"
      } 
```
- 422 Unprocessable Entity: Cartão já existe
``` json
      {
         "numeroCartao": "6549873025634501",
         "senha": "1234"
      } 
```
- **GET /cartoes/{numeroCartao}** - Consulta saldo do cartão
    - Respostas:
        - 200 OK: Retorna o saldo do cartão (ex: `495.15`)
        - 404 Not Found: Cartão não encontrado

#### Transações
- **POST /transacoes** - Autoriza uma transação
    - Corpo da requisição:
``` json
    {
        "numeroCartao": "6549873025634501",
        "senhaCartao": "1234",
        "valor": 10.00
    }
```
- Respostas:
    - 201 Created: Transação autorizada com corpo da resposta `OK`
    - 422 Unprocessable Entity: Transação não autorizada com corpo da resposta podendo ser , ou (dependendo da regra que impediu a autorização) `SALDO_INSUFICIENTE``SENHA_INVALIDA``CARTAO_INEXISTENTE`

## Configuração e Execução
### Pré-requisitos
- Java 24 ou superior
- Docker e Docker Compose
- Maven

### Configuração do Ambiente
1. **Clone o repositório**:
``` bash
   git clone https://github.com/seu-usuario/authorizer.git
   cd authorizer
```
1. **Configure as variáveis de ambiente** (arquivo ): `.env`
``` 
   # Configurações do Banco de Dados
   DB_HOST=mysql
   DB_PORT=3306
   DB_NAME=miniautorizador
   DB_USERNAME=root
   DB_PASSWORD=miniautorizador

   # Configurações do JPA
   JPA_DDL_AUTO=update
   JPA_SHOW_SQL=true
   JPA_FORMAT_SQL=true

   # Configurações de logging
   HIBERNATE_LOG_LEVEL=DEBUG
   HIBERNATE_TYPE_LOG_LEVEL=TRACE

   # Configuração de servidor
   SERVER_PORT=8080

   # Variáveis para o Docker Compose MySQL
   MYSQL_DATABASE=miniautorizador
   MYSQL_ROOT_PASSWORD=miniautorizador
   MYSQL_ALLOW_EMPTY_PASSWORD=yes
```
1. **Inicie o banco de dados**:
``` bash
   docker-compose up -d
```
1. **Execute a aplicação**:
``` bash
   # Com Maven
   mvn spring-boot:run

   # Ou usando o JAR
   mvn clean package
   java -jar target/authorizer-0.0.1-SNAPSHOT.jar
```
## Exemplos de Uso
### Criar um Cartão
``` bash
curl -X POST http://localhost:8080/cartoes \
  -H "Content-Type: application/json" \
  -d '{"numeroCartao": "6549873025634501", "senha": "1234"}'
```
### Consultar Saldo
``` bash
curl -X GET http://localhost:8080/cartoes/6549873025634501
```
### Autorizar uma Transação
``` bash
curl -X POST http://localhost:8080/transacoes \
  -H "Content-Type: application/json" \
  -d '{"numeroCartao": "6549873025634501", "senhaCartao": "1234", "valor": 10.00}'
```
## Monitoramento e Logs
A aplicação utiliza o framework de logging do Spring Boot. Os logs são gerados no console e podem ser configurados no arquivo : `application.properties`
``` properties
# Configurações de logging
logging.level.org.hibernate.SQL=${HIBERNATE_LOG_LEVEL:DEBUG}
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=${HIBERNATE_TYPE_LOG_LEVEL:TRACE}
```
## Desenvolvimento
### Executando Testes
``` bash
# Executar todos os testes
mvn test

# Executar testes de uma classe específica
mvn test -Dtest=CartaoServiceTest
```
### Construindo a Aplicação
``` bash
mvn clean package
```
## Contratos da API
Os contratos da API seguem estritamente os padrões definidos abaixo:
### Criar novo cartão
``` 
Method: POST
URL: http://localhost:8080/cartoes
Body (json):
{
    "numeroCartao": "6549873025634501",
    "senha": "1234"
}
```
#### Possíveis respostas:
``` 
Criação com sucesso:
   Status Code: 201
   Body (json):
   {
      "senha": "1234",
      "numeroCartao": "6549873025634501"
   } 
-----------------------------------------
Caso o cartão já exista:
   Status Code: 422
   Body (json):
   {
      "senha": "1234",
      "numeroCartao": "6549873025634501"
   } 
```
### Obter saldo do Cartão
``` 
Method: GET
URL: http://localhost:8080/cartoes/{numeroCartao}
```
#### Possíveis respostas:
``` 
Obtenção com sucesso:
   Status Code: 200
   Body: 495.15 
-----------------------------------------
Caso o cartão não exista:
   Status Code: 404 
   Sem Body
```
### Realizar uma Transação
``` 
Method: POST
URL: http://localhost:8080/transacoes
Body (json):
{
    "numeroCartao": "6549873025634501",
    "senhaCartao": "1234",
    "valor": 10.00
}
```
#### Possíveis respostas:
``` 
Transação realizada com sucesso:
   Status Code: 201
   Body: OK 
-----------------------------------------
Caso alguma regra de autorização tenha barrado a mesma:
   Status Code: 422 
   Body: SALDO_INSUFICIENTE|SENHA_INVALIDA|CARTAO_INEXISTENTE
```
## Finalização
Desenvolvido com ❤️ para o sistema de autorização de cartões.
