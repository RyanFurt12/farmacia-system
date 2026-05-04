# Sistema de Farmácias — Monorepo

Exemplo de sistema de farmácias com integração a sistemas legados, realizado para estudo em Desenvolvimento de componentes (BRADEPO) no IFSP.

## Estrutura

```
farmacias-system/
├── common-dtos/                ← DTOs compartilhados (Maven jar)
├── cpf-validator/              ← Biblioteca de validação de CPF (Maven jar)
├── farmacia-app/               ← Sistema principal (Spring Boot, porta 8080)
├── fornecedor-a-service/       ← Fornecedor A — COBOL (porta 8083)
├── fornecedor-b-service/       ← Fornecedor B — SOAP/Delphi (porta 8084)
├── docker-compose.yml
└── README.md
```

## Build e Execução
Como é preciso realizar build dos componentes comum-dtos e cpf-validator, além de subir vários serviços, montei algumas imagens docker para facilitar a execução. No entanto, é possível executar os serviços localmente e manualmente se preferir.

### Docker Compose
```bash
docker-compose up --build
```

### Execução local
```bash
mvn install -f common-dtos/pom.xml
mvn install -f cpf-validator/pom.xml

cd fornecedor-a-service && mvn spring-boot:run

cd fornecedor-b-service && mvn spring-boot:run

cd farmacia-app && mvn spring-boot:run
```

## API Endpoints

### Clientes — `/api/clients`
| Método | Endpoint | Descrição |
|--------|----------|-------------|
| POST | `/api/clients` | Registrar Cliente |
| PUT | `/api/clients/cpf/{cpf}` | Atualizar dados de cliente |
| GET | `/api/clients` | Listar Clientes |
| GET | `/api/clients/{id}` | Buscar Cliente por ID |
| GET | `/api/clients/cpf/{cpf}` | Buscar Cliente por CPF |

### Produtos — `/api/products`
| Método | Endpoint | Descrição |
|--------|----------|-------------|
| POST | `/api/products` | Registrar Produto |
| GET | `/api/products` | Listar Produtos |
| GET | `/api/products/{id}` | Buscar Produto por ID |
| POST | `/api/products/{id}/order?quantity=5` | Repor Estoque de um Produto |

### Vendas — `/api/sales`
| Método | Endpoint | Descrição |
|--------|----------|-------------|
| POST | `/api/sales` | Registrar Venda |
| GET | `/api/sales` | Listar Vendas |
| GET | `/api/sales/{id}` | Buscar Venda por ID |

## Exemplos de uso

### Registrar e Atualizar Cliente
```bash
# Register
curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{"cpf": "52998224725", "name": "Teste Silva", "email": "teste@email.com"}'

# Update
curl -X PUT http://localhost:8080/api/clients/cpf/52998224725 \
  -H "Content-Type: application/json" \
  -d '{"name": "Teste Update", "email": "teste.update@email.com"}'
```

### Registrar Venda
```bash
curl -X POST http://localhost:8080/api/sales \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "52998224725",
    "items": [
      {"productId": 1, "quantity": 2},
      {"productId": 2, "quantity": 1}
    ],
    "prescriptionId": "REC-12345"
  }'
```

### Repor Estoque de produto
```bash
curl -X POST "http://localhost:8080/api/products/1/order?quantity=10"
```
