package br.com.farmacia.controller;

import br.com.farmacia.dto.ClientRequest;
import br.com.farmacia.dto.ClientResponse;
import br.com.farmacia.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientResponse> register(@RequestBody ClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.register(request));
    }

    @PutMapping("/cpf/{cpf}")
    public ResponseEntity<ClientResponse> update(@PathVariable String cpf, @RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.update(cpf, request));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> findAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClientResponse> findByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(clientService.findByCpf(cpf));
    }
}
