package com.puc.stayhub.service;

import com.puc.stayhub.model.Residencia;
import com.puc.stayhub.repository.ResidenciaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ResidenciaService {

    private final ResidenciaRepository residenciaRepository;

    public ResidenciaService(ResidenciaRepository residenciaRepository) {
        this.residenciaRepository = residenciaRepository;
    }

    public List<Residencia> findAll() {
        return residenciaRepository.findAll();
    }

    public Residencia findById(Long id) {
        return residenciaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Residencia nao encontrada: " + id));
    }

    public Residencia save(Residencia residencia) {
        return residenciaRepository.save(residencia);
    }

    public Residencia update(Long id, Residencia dados) {
        Residencia existente = findById(id);
        existente.setNome(dados.getNome());
        existente.setEndereco(dados.getEndereco());
        existente.setCidade(dados.getCidade());
        existente.setUf(dados.getUf());
        return residenciaRepository.save(existente);
    }

    public void delete(Long id) {
        findById(id);
        residenciaRepository.deleteById(id);
    }
}