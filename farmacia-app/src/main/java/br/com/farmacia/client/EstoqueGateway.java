package br.com.farmacia.client;

/**
 * Interface para o comando de baixa de estoque, possue 2 versões:
 *  - V1: implementação REST síncrona ({@link EstoqueGatewayRest})
 *  - V2: implementação por mensageria ({@link EstoqueGatewayRabbit})
 */
public interface EstoqueGateway {
    void baixarEstoque(Long productId, Integer quantity);
}
