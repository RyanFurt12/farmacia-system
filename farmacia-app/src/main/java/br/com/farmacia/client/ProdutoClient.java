package br.com.farmacia.client;

import br.com.farmacia.dto.ProdutoDTO;

public interface ProdutoClient {
    ProdutoDTO buscarProduto(Long id);
}
