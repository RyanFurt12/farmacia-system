package br.com.farmacia.service;

import br.com.farmacia.cpf.CpfValidator;
import br.com.farmacia.dto.ClientRequest;
import br.com.farmacia.dto.ClientResponse;
import br.com.farmacia.exception.InvalidCpfException;
import br.com.farmacia.model.Client;
import br.com.farmacia.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final CpfValidator cpfValidator;

    public ClientService(ClientRepository clientRepository, CpfValidator cpfValidator) {
        this.clientRepository = clientRepository;
        this.cpfValidator = cpfValidator;
    }

    @Transactional
    public ClientResponse register(ClientRequest request) {
        if (!cpfValidator.isValid(request.getCpf())) {
            throw new InvalidCpfException(request.getCpf());
        }

        String cleanCpf = cpfValidator.unformat(request.getCpf());
        
        Optional<Client> existing = clientRepository.findByCpf(cleanCpf);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Cliente já cadastrado com o CPF informado");
        }

        Client client = Client.builder()
                .cpf(cleanCpf)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .hasInsurance(request.getHasInsurance())
                .build();

        client = clientRepository.save(client);
        return toResponse(client);
    }

    @Transactional
    public ClientResponse update(String cpf, ClientRequest request) {
        String cleanCpf = cpfValidator.unformat(cpf);
        Client client = clientRepository.findByCpf(cleanCpf)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com CPF: " + cpf));

        if (request.getName() != null) client.setName(request.getName());
        if (request.getEmail() != null) client.setEmail(request.getEmail());
        if (request.getPhone() != null) client.setPhone(request.getPhone());
        if (request.getBirthDate() != null) client.setBirthDate(request.getBirthDate());
        if (request.getHasInsurance() != null) client.setHasInsurance(request.getHasInsurance());

        client = clientRepository.save(client);
        return toResponse(client);
    }

    @Transactional
    public Client findOrCreateByCpf(String cpf) {
        if (!cpfValidator.isValid(cpf)) {
            throw new InvalidCpfException(cpf);
        }
        String cleanCpf = cpfValidator.unformat(cpf);

        Optional<Client> existing = clientRepository.findByCpf(cleanCpf);
        if (existing.isPresent()) {
            return existing.get();
        }

        Client newClient = Client.builder().cpf(cleanCpf).build();
        return clientRepository.save(newClient);
    }

    public List<ClientResponse> findAll() {
        return clientRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ClientResponse findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com id: " + id));
        return toResponse(client);
    }

    public ClientResponse findByCpf(String cpf) {
        String cleanCpf = cpfValidator.unformat(cpf);
        Client client = clientRepository.findByCpf(cleanCpf)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com CPF: " + cpf));
        return toResponse(client);
    }

    private ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getCpf(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getBirthDate(),
                client.getHasInsurance(),
                client.getRegistrationDate()
        );
    }
}
