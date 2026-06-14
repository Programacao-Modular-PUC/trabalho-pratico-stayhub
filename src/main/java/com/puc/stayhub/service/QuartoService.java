package com.puc.stayhub.service;

import com.puc.stayhub.dto.QuartoRequestDTO;
import com.puc.stayhub.model.*;
import com.puc.stayhub.repository.QuartoRepository;
import com.puc.stayhub.repository.ResidenciaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class QuartoService {

    private final QuartoRepository quartoRepository;
    private final ResidenciaRepository residenciaRepository;

    public QuartoService(QuartoRepository quartoRepository, ResidenciaRepository residenciaRepository) {
        this.quartoRepository = quartoRepository;
        this.residenciaRepository = residenciaRepository;
    }

    public List<Quarto> findAll() {
        return quartoRepository.findAll();
    }

    public List<Quarto> findByResidencia(Long residenciaId) {
        return quartoRepository.findByResidenciaId(residenciaId);
    }

    public List<Quarto> findByTipo(TipoQuarto tipo) {
        return quartoRepository.findByTipo(tipo);
    }

    public List<Quarto> findByResidenciaAndTipo(Long residenciaId, TipoQuarto tipo) {
        return quartoRepository.findByResidenciaIdAndTipo(residenciaId, tipo);
    }

    public Quarto findById(Long id) {
        return quartoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Quarto nao encontrado: " + id));
    }

    public Quarto criar(QuartoRequestDTO dto) {
        Residencia residencia = residenciaRepository.findById(dto.getResidenciaId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Residencia nao encontrada: " + dto.getResidenciaId()));

        Quarto quarto = fabricarQuarto(dto);
        quarto.setValorBase(dto.getValorBase());
        quarto.setPossuiAR(dto.isPossuiAR());
        quarto.setPossuiHidro(dto.isPossuiHidro());
        quarto.setResidencia(residencia);

        return quartoRepository.save(quarto);
    }

    private Quarto fabricarQuarto(QuartoRequestDTO dto) {
        return switch (dto.getTipo()) {
            case INDIVIDUAL -> {
                int camas = dto.getCamasSolteiro() != null ? dto.getCamasSolteiro() : 1;
                yield new QuartoIndividual(camas);
            }
            case DUPLO -> {
                TipoCamaCasal tipoCama = dto.getTipoCamaCasal() != null
                    ? dto.getTipoCamaCasal() : TipoCamaCasal.CASAL;
                boolean berco = Boolean.TRUE.equals(dto.getPossuiBercoDisponivel());
                yield new QuartoDuplo(tipoCama, berco);
            }
            case FAMILIA -> {
                int solteiro = dto.getCamasSolteiro() != null ? dto.getCamasSolteiro() : 0;
                int casal = dto.getCamasCasal() != null ? dto.getCamasCasal() : 0;
                int queenKing = dto.getCamasQueenKing() != null ? dto.getCamasQueenKing() : 0;
                int ambientes = dto.getQuantidadeAmbientes() != null ? dto.getQuantidadeAmbientes() : 1;
                if (solteiro + casal + queenKing == 0) {
                    throw new IllegalArgumentException("Quarto Familia deve ter ao menos uma cama");
                }
                yield new QuartoFamilia(solteiro, casal, queenKing, ambientes);
            }
        };
    }

    public void delete(Long id) {
        findById(id);
        quartoRepository.deleteById(id);
    }
}