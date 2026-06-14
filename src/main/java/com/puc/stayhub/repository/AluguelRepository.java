package com.puc.stayhub.repository;
import com.puc.stayhub.model.Aluguel;
import com.puc.stayhub.model.StatusAluguel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
@Repository
public interface AluguelRepository extends JpaRepository<Aluguel, Long> {
List<Aluguel> findByClienteId(Long clienteId);
List<Aluguel> findByQuartoId(Long quartoId);
List<Aluguel> findByClienteIdOrderByDataInicioDesc(Long clienteId);
List<Aluguel> findByClienteIdAndStatus(Long clienteId, StatusAluguel status);
@Query("""
SELECT a FROM Aluguel a
WHERE a.quarto.id = :quartoId
AND a.status = com.puc.stayhub.model.StatusAluguel.ATIVO
AND a.dataInicio < :dataFim
AND :dataInicio < a.dataFim
""")
List<Aluguel> findSobreposicoes(
@Param("quartoId") Long quartoId,
@Param("dataInicio") LocalDate dataInicio,
@Param("dataFim") LocalDate dataFim
);
}