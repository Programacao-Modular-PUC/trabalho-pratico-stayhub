package com.puc.stayhub.service;

import com.puc.stayhub.dto.AluguelRequestDTO;
import com.puc.stayhub.exception.DataInvalidaException;
import com.puc.stayhub.exception.QuartoIndisponivelException;
import com.puc.stayhub.exception.CapacidadeExcedidaException;
import com.puc.stayhub.log.LogService;
import com.puc.stayhub.model.Aluguel;
import com.puc.stayhub.model.Cliente;
import com.puc.stayhub.model.Quarto;
import com.puc.stayhub.model.StatusAluguel;
import com.puc.stayhub.notificacao.CentralNotificacoes;
import com.puc.stayhub.notificacao.EventoReserva;
import com.puc.stayhub.notificacao.TipoEventoReserva;
import com.puc.stayhub.repository.AluguelRepository;
import com.puc.stayhub.repository.ClienteRepository;
import com.puc.stayhub.repository.QuartoRepository;
import com.puc.stayhub.tarifa.ContextoTarifacao;
import com.puc.stayhub.tarifa.GerenciadorTarifas;
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

    public List<Aluguel> historicoPorCliente(Long clienteId) {
        clienteRepository.findById(clienteId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado: " + clienteId));
        return aluguelRepository.findByClienteIdOrderByDataInicioDesc(clienteId);
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
            throw new CapacidadeExcedidaException(dto.getNumHospedes(), quarto.getCapacidadeMaxima());
        }

        List<Aluguel> sobreposicoes = aluguelRepository.findSobreposicoes(
            dto.getQuartoId(), dto.getDataInicio(), dto.getDataFim());
        if (!sobreposicoes.isEmpty()) {
            throw new QuartoIndisponivelException(dto.getQuartoId());
        }

        Aluguel aluguel = new Aluguel();
        aluguel.setCliente(cliente);
        aluguel.setQuarto(quarto);
        aluguel.setDataInicio(dto.getDataInicio());
        aluguel.setDataFim(dto.getDataFim());
        aluguel.setNumHospedes(dto.getNumHospedes());
        aluguel.setSolicitouBerco(dto.isSolicitouBerco());
        aluguel.setStatus(StatusAluguel.ATIVO);
        aluguel.recalcularValores();

        double valorBase = aluguel.getValorDiaria();
        int qtdReservasAnteriores = aluguelRepository.findByClienteId(cliente.getId()).size();
        ContextoTarifacao ctx = new ContextoTarifacao(
            valorBase, dto.getDataInicio(), dto.getDataFim(),
            cliente, qtdReservasAnteriores);
        double valorAjustado = GerenciadorTarifas.getInstancia().calcular(ctx);
        aluguel.setValorDiaria(valorAjustado);
        aluguel.setValorTotal(valorAjustado * aluguel.calcularQuantidadeDiarias());

        Aluguel salvo = aluguelRepository.save(aluguel);

        LogService.getInstancia().info("AluguelService",
            String.format("Reserva #%d criada para cliente #%d (valor diaria=%.2f, total=%.2f)",
                salvo.getId(), cliente.getId(), valorAjustado, salvo.getValorTotal()));

        CentralNotificacoes.getInstancia().publicar(new EventoReserva(
            TipoEventoReserva.RESERVA_CRIADA, salvo,
            "Sua reserva foi criada com sucesso."));

        return salvo;
    }

    public Aluguel cancelar(Long id) {
        Aluguel aluguel = findById(id);
        if (aluguel.getStatus() == StatusAluguel.CANCELADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Aluguel ja esta cancelado: " + id);
        }
        if (aluguel.getDataInicio().isBefore(LocalDate.now())) {
            throw new DataInvalidaException(
                "Nao e possivel cancelar um aluguel que ja iniciou");
        }
        aluguel.cancelar();
        Aluguel salvo = aluguelRepository.save(aluguel);

        LogService.getInstancia().info("AluguelService",
            String.format("Reserva #%d cancelada", salvo.getId()));

        CentralNotificacoes.getInstancia().publicar(new EventoReserva(
            TipoEventoReserva.RESERVA_CANCELADA, salvo,
            "Sua reserva foi cancelada."));

        return salvo;
    }

    public void delete(Long id) {
        findById(id);
        aluguelRepository.deleteById(id);
    }

    private void validarDatas(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new DataInvalidaException("Datas de inicio e fim sao obrigatorias");
        }
        if (!fim.isAfter(inicio)) {
            throw new DataInvalidaException("Data fim deve ser posterior a data inicio");
        }
        if (inicio.isBefore(LocalDate.now())) {
            throw new DataInvalidaException("Data de inicio nao pode estar no passado");
        }
    }
}