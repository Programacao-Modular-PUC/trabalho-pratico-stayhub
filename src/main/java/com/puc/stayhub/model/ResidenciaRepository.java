package com.puc.stayhub.repository;
import com.puc.stayhub.model.Residencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ResidenciaRepository extends JpaRepository<Residencia, Long> {
}