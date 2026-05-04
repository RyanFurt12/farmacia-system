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

### Clients — `/api/clients`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/clients` | Register client |
| PUT | `/api/clients/cpf/{cpf}` | Update client info |
| GET | `/api/clients` | List all |
| GET | `/api/clients/{id}` | Find by ID |
| GET | `/api/clients/cpf/{cpf}` | Find by CPF |

### Products & Supplier Orders — `/api/products`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/products` | Register product |
| GET | `/api/products` | List all |
| GET | `/api/products/{id}` | Find by ID |
| POST | `/api/products/{id}/order?quantity=5` | Order product from a supplier |

### Sales — `/api/sales`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/sales` | Register sale |
| GET | `/api/sales` | List all |
| GET | `/api/sales/{id}` | Find by ID |

## Exemplos de uso

### Register or Update Client
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

### Order from Supplier
```bash
curl -X POST "http://localhost:8080/api/products/1/order?quantity=10"
```

### Register Sale
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