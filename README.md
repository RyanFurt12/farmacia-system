# Sistema de Farmácias — Monorepo

Sistema de farmácias em **microsserviços** (Spring Boot + MySQL), com integração a
sistemas legados, feito para estudo em Desenvolvimento de Componentes (BRADEPO) no IFSP.

Entrega em **duas versões**, selecionáveis por configuração e **reutilizando os mesmos
componentes**:
- **V1** — baixa de estoque por chamada **REST** síncrona entre serviços (`ESTOQUE_MODE=rest`, padrão);
- **V2** — baixa de estoque por **mensageria (RabbitMQ)** (`ESTOQUE_MODE=messaging`).

## Estrutura

```
farmacias-system/
├── common-dtos/                ← DTOs/contratos compartilhados (jar): ProdutoDTO, EstoqueBaixaEvent, etc.
├── cpf-validator/              ← Biblioteca de validação de CPF (jar)
├── farmacia-app/               ← Interface unificada: clientes, vendas, NF, receita, relatórios (porta 8080)
├── produtos-estoque-service/   ← Microsserviço de produtos, estoque e compras (porta 8082)
├── fornecedor-a-service/       ← Fornecedor A — legado COBOL (porta 8083)
├── fornecedor-b-service/       ← Fornecedor B — legado SOAP/Delphi (porta 8084)
├── docker-compose.yml          ← MySQL + RabbitMQ + serviços
└── README.md
```

### Arquitetura

- `farmacia-app` é a **interface unificada**. Para vender, ele **lê** produtos do
  `produtos-estoque-service` (`ProdutoClient`, sempre REST) e **dá baixa** no estoque
  através do `EstoqueGateway` — cujo adapter muda entre V1 (REST) e V2 (RabbitMQ) sem
  alterar a regra de negócio.
- Não há FK entre serviços: `SaleItem` guarda um *snapshot* do produto e cada serviço
  tem seu próprio banco (`farmaciadb` / `produtosdb`).
- Integrações externas via ports/adapters: **SEFAZ** (NF-e), **ANS** (receitas
  controladas) e **Fornecedores A/B**.

### Regras de negócio principais

- Cadastro de **medicamentos** e **produtos de higiene** (xampu, creme facial, …).
- Dados do cliente são coletados/cadastrados **somente** em medicamentos controlados.
- Cliente pode pedir **NF com CPF sem ser cadastrado** (vai só na nota).
- **Bonificação**: desconto progressivo por nº de compras do cliente cadastrado +
  bônus de convênio para idoso com plano de saúde.
- Emissão de **NF-e (SEFAZ)** em toda venda; envio de **receita à ANS** em controlados.
- **Relatórios**: vendas por período, mais vendidos e controle de estoque.

## Build e Execução

### Docker Compose (recomendado)

```bash
# V1 (REST) — padrão
docker-compose up --build

# V2 (mensageria RabbitMQ)
ESTOQUE_MODE=messaging docker-compose up --build
```

Sobe MySQL, RabbitMQ (painel em http://localhost:15672, guest/guest) e todos os serviços.

### Execução local

```bash
mvn install -f common-dtos/pom.xml
mvn install -f cpf-validator/pom.xml

# infra mínima
docker compose up -d mysql rabbitmq

# em terminais separados (defina ESTOQUE_MODE=messaging para a V2)
cd fornecedor-a-service && mvn spring-boot:run
cd fornecedor-b-service && mvn spring-boot:run
cd produtos-estoque-service && mvn spring-boot:run
cd farmacia-app && mvn spring-boot:run
```

## API Endpoints

### farmacia-app (8080)

**Clientes — `/api/clients`**
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/clients` | Registrar cliente (cpf, name, email, phone, birthDate, hasInsurance) |
| PUT | `/api/clients/cpf/{cpf}` | Atualizar cliente |
| GET | `/api/clients` · `/api/clients/{id}` · `/api/clients/cpf/{cpf}` | Consultas |

**Vendas — `/api/sales`**
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/sales` | Registrar venda |
| GET | `/api/sales` · `/api/sales/{id}` | Consultas |

**Relatórios — `/api/reports`**
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/reports/sales?start=&end=` | Vendas por período (ISO date-time) |
| GET | `/api/reports/top-products` | Produtos mais vendidos |

### produtos-estoque-service (8082)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST/GET | `/api/products` | Registrar / listar produtos |
| GET | `/api/products/{id}` | Buscar produto |
| GET | `/api/products/stock` | Relatório de estoque |
| POST | `/api/products/{id}/order?quantity=` | Reposição (cria intenção de compra) |
| POST | `/api/products/{id}/baixa?quantity=` | Baixa de estoque (usado na V1) |
| ... | `/api/purchase-intentions/...` | Aprovar/reprovar/compra em lote |

## Exemplos de uso

```bash
# Venda não controlada SEM CPF (anônima)
curl -X POST http://localhost:8080/api/sales -H "Content-Type: application/json" \
  -d '{"items":[{"productId":1,"quantity":2}]}'

# NF com CPF (cliente não é cadastrado)
curl -X POST http://localhost:8080/api/sales -H "Content-Type: application/json" \
  -d '{"cpf":"16899535009","items":[{"productId":3,"quantity":1}]}'

# Venda de medicamento controlado (exige CPF) + receita
curl -X POST http://localhost:8080/api/sales -H "Content-Type: application/json" \
  -d '{"cpf":"52998224725","items":[{"productId":2,"quantity":1}],"prescriptionId":"REC-12345"}'

# Relatório de vendas por período
curl "http://localhost:8080/api/reports/sales?start=2020-01-01T00:00:00&end=2030-01-01T00:00:00"
```
