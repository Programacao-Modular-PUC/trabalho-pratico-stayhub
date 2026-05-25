package com.puc.stayhub.repository;

import com.puc.stayhub.model.Quarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuartoRepository extends JpaRepository<Quarto, Long> {
    List<Quarto> findByResidenciaId(Long residenciaId);
}