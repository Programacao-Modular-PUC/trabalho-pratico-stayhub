package com.puc.stayhub.service;

import com.puc.stayhub.dto.AluguelRequestDTO;
import com.puc.stayhub.model.Aluguel;
import com.puc.stayhub.model.Cliente;
import com.puc.stayhub.model.Quarto;
import com.puc.stayhub.repository.AluguelRepository;
import com.puc.stayhub.repository.ClienteRepository;
import com.puc.stayhub.repository.QuartoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final ClienteRepository clienteRepository;
    private final QuartoRepository quartoRepository;

    public AluguelService(AluguelRepository aluguelRepository,
                          ClienteRepository clienteRepository,
                          QuartoRepository quartoRepository) {
        this.aluguelRepository = aluguelRepository;
        this.clienteRepository = clienteRepository;
        this.quartoRepository = quartoRepository;
    }

    public List<Aluguel> findAll() {
        return aluguelRepository.findAll();
    }

    public Aluguel findById(Long id) {
        return aluguelRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Aluguel nao encontrado: " + id));
    }

    public List<Aluguel> findByCliente(Long clienteId) {
        return aluguelRepository.findByClienteId(clienteId);
    }

    public List<Aluguel> findByQuarto(Long quartoId) {
        return aluguelRepository.findByQuartoId(quartoId);
    }

    public Aluguel criar(AluguelRequestDTO dto) {
        validarDatas(dto.getDataInicio(), dto.getDataFim());

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Cliente nao encontrado: " + dto.getClienteId()));

        Quarto quarto = quartoRepository.findById(dto.getQuartoId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Quarto nao encontrado: " + dto.getQuartoId()));

        if (dto.getNumHospedes() > quarto.getCapacidadeMaxima()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Numero de hospedes (" + dto.getNumHospedes() + ") excede a capacidade do quarto ("
                + quarto.getCapacidadeMaxima() + ")");
        }

        List<Aluguel> sobreposicoes = aluguelRepository.findSobreposicoes(
            dto.getQuartoId(), dto.getDataInicio(), dto.getDataFim());
        if (!sobreposicoes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Quarto indisponivel no periodo solicitado");
        }

        Aluguel aluguel = new Aluguel();
        aluguel.setCliente(cliente);
        aluguel.setQuarto(quarto);
        aluguel.setDataInicio(dto.getDataInicio());
        aluguel.setDataFim(dto.getDataFim());
        aluguel.setNumHospedes(dto.getNumHospedes());
        aluguel.setSolicitouBerco(dto.isSolicitouBerco());
        aluguel.recalcularValores();

        return aluguelRepository.save(aluguel);
    }

    public void delete(Long id) {
        findById(id);
        aluguelRepository.deleteById(id);
    }

    private void validarDatas(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Datas de inicio e fim sao obrigatorias");
        }
        if (!fim.isAfter(inicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Data fim deve ser posterior a data inicio");
        }
    }
}