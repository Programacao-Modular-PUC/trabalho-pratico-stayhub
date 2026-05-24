package com.puc.stayhub.repository;
import com.puc.stayhub.model.Aluguel;
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
/**
*/
@Query("""
SELECT a FROM Aluguel a
WHERE a.quarto.id = :quartoId
AND a.dataInicio < :dataFim
AND :dataInicio < a.dataFim
* Retorna alugueis do mesmo quarto cujo intervalo se sobrepoe ao informado.
* Dois intervalos [a1,a2] e [b1,b2] se sobrepoem se a1 &lt; b2 AND b1 &lt; a2.
""")
List<Aluguel> findSobreposicoes(
@Param("quartoId") Long quartoId,
@Param("dataInicio") LocalDate dataInicio,
@Param("dataFim") LocalDate dataFim
);
}