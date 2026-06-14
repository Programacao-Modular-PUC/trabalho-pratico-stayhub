package com.puc.stayhub.repository;

import com.puc.stayhub.model.Quarto;
import com.puc.stayhub.model.TipoQuarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    List<Quarto> findByResidenciaId(Long residenciaId);

    @Query(value = "SELECT * FROM quartos WHERE tipo = :#{#tipo.name()}", nativeQuery = true)
    List<Quarto> findByTipo(@Param("tipo") TipoQuarto tipo);

    @Query(value = "SELECT * FROM quartos WHERE residencia_id = :residenciaId AND tipo = :#{#tipo.name()}",
           nativeQuery = true)
    List<Quarto> findByResidenciaIdAndTipo(@Param("residenciaId") Long residenciaId,
                                           @Param("tipo") TipoQuarto tipo);
}