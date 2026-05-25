package com.puc.stayhub.service;

import com.puc.stayhub.model.Cliente;
import com.puc.stayhub.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Cliente findById(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Cliente nao encontrado: " + id));
    }

    public Cliente save(Cliente cliente) {
        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "CPF ja cadastrado: " + cliente.getCpf());
        }
        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, Cliente dados) {
        Cliente existente = findById(id);
        existente.setNome(dados.getNome());
        existente.setEmail(dados.getEmail());
        existente.setTelefone(dados.getTelefone());
        return clienteRepository.save(existente);
    }

    public void delete(Long id) {
        findById(id);
        clienteRepository.deleteById(id);
    }
}