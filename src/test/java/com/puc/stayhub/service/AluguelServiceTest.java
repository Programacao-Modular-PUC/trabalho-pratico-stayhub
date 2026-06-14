package com.puc.stayhub.service;

import com.puc.stayhub.dto.AluguelRequestDTO;
import com.puc.stayhub.exception.CapacidadeExcedidaException;
import com.puc.stayhub.exception.DataInvalidaException;
import com.puc.stayhub.exception.QuartoIndisponivelException;
import com.puc.stayhub.model.*;
import com.puc.stayhub.repository.AluguelRepository;
import com.puc.stayhub.repository.ClienteRepository;
import com.puc.stayhub.repository.QuartoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AluguelService - regras de criacao, cancelamento e historico")
class AluguelServiceTest {

    @Mock AluguelRepository aluguelRepository;
    @Mock ClienteRepository clienteRepository;
    @Mock QuartoRepository quartoRepository;

    @InjectMocks AluguelService service;

    private Cliente cliente;
    private QuartoIndividual quarto;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("Caua", "12345678901", "c@email.com", "31999999999");
        cliente.setId(1L);

        quarto = new QuartoIndividual(2);
        quarto.setValorBase(100.0);
        quarto.setId(10L);
    }

    private AluguelRequestDTO dto(int hospedes, LocalDate inicio, LocalDate fim) {
        AluguelRequestDTO d = new AluguelRequestDTO();
        d.setClienteId(1L);
        d.setQuartoId(10L);
        d.setNumHospedes(hospedes);
        d.setDataInicio(inicio);
        d.setDataFim(fim);
        d.setSolicitouBerco(false);
        return d;
    }

    @Test
    @DisplayName("Datas nulas lancam DataInvalidaException")
    void datasNulas() {
        assertThrows(DataInvalidaException.class,
            () -> service.criar(dto(1, null, LocalDate.now().plusDays(1))));
    }

    @Test
    @DisplayName("Data fim anterior a data inicio lanca DataInvalidaException")
    void datasInvertidas() {
        LocalDate inicio = LocalDate.now().plusDays(5);
        LocalDate fim    = LocalDate.now().plusDays(2);
        assertThrows(DataInvalidaException.class, () -> service.criar(dto(1, inicio, fim)));
    }

    @Test
    @DisplayName("Numero de hospedes acima da capacidade lanca CapacidadeExcedidaException")
    void capacidadeExcedida() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(quartoRepository.findById(10L)).thenReturn(Optional.of(quarto));
        AluguelRequestDTO d = dto(5, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        assertThrows(CapacidadeExcedidaException.class, () -> service.criar(d));
    }

    @Test
    @DisplayName("Sobreposicao de datas lanca QuartoIndisponivelException")
    void quartoIndisponivel() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(quartoRepository.findById(10L)).thenReturn(Optional.of(quarto));
        when(aluguelRepository.findSobreposicoes(eq(10L), any(), any()))
            .thenReturn(List.of(new Aluguel()));
        AluguelRequestDTO d = dto(1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        assertThrows(QuartoIndisponivelException.class, () -> service.criar(d));
    }

    @Test
    @DisplayName("Criacao valida salva e retorna o aluguel com status ATIVO")
    void criacaoValida() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(quartoRepository.findById(10L)).thenReturn(Optional.of(quarto));
        when(aluguelRepository.findSobreposicoes(eq(10L), any(), any())).thenReturn(List.of());
        when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(inv -> inv.getArgument(0));

        Aluguel salvo = service.criar(dto(2, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)));

        assertEquals(StatusAluguel.ATIVO, salvo.getStatus());
        assertEquals(quarto, salvo.getQuarto());
        assertEquals(cliente, salvo.getCliente());
        assertTrue(salvo.getValorTotal() > 0);
        verify(aluguelRepository).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Cancelar aluguel marca o status como CANCELADO")
    void cancelarAluguel() {
        Aluguel a = new Aluguel();
        a.setId(99L);
        a.setCliente(cliente);
        a.setQuarto(quarto);
        a.setDataInicio(LocalDate.now().plusDays(5));
        a.setDataFim(LocalDate.now().plusDays(7));
        a.setNumHospedes(1);
        a.setStatus(StatusAluguel.ATIVO);

        when(aluguelRepository.findById(99L)).thenReturn(Optional.of(a));
        when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(inv -> inv.getArgument(0));

        Aluguel cancelado = service.cancelar(99L);
        assertEquals(StatusAluguel.CANCELADO, cancelado.getStatus());
    }

    @Test
    @DisplayName("Cancelar aluguel ja cancelado lanca conflito")
    void cancelarDuplicado() {
        Aluguel a = new Aluguel();
        a.setId(99L);
        a.setDataInicio(LocalDate.now().plusDays(5));
        a.setStatus(StatusAluguel.CANCELADO);
        when(aluguelRepository.findById(99L)).thenReturn(Optional.of(a));
        assertThrows(ResponseStatusException.class, () -> service.cancelar(99L));
    }

    @Test
    @DisplayName("Historico por cliente delega ao repository ordenado")
    void historico() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(aluguelRepository.findByClienteIdOrderByDataInicioDesc(1L))
            .thenReturn(List.of(new Aluguel(), new Aluguel()));
        List<Aluguel> historico = service.historicoPorCliente(1L);
        assertEquals(2, historico.size());
        verify(aluguelRepository).findByClienteIdOrderByDataInicioDesc(1L);
    }
}
