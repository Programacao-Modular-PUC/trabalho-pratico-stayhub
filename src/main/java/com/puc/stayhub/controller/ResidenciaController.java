package com.puc.stayhub.controller;

import com.puc.stayhub.model.Quarto;
import com.puc.stayhub.model.Residencia;
import com.puc.stayhub.service.QuartoService;
import com.puc.stayhub.service.ResidenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/residencias")
@CrossOrigin(origins = "*")
public class ResidenciaController {

    private final ResidenciaService residenciaService;
    private final QuartoService quartoService;

    public ResidenciaController(ResidenciaService residenciaService, QuartoService quartoService) {
        this.residenciaService = residenciaService;
        this.quartoService = quartoService;
    }

    @GetMapping
    public List<Residencia> findAll() {
        return residenciaService.findAll();
    }

    @GetMapping("/{id}")
    public Residencia findById(@PathVariable Long id) {
        return residenciaService.findById(id);
    }

    @GetMapping("/{id}/quartos")
    public List<Quarto> findQuartos(@PathVariable Long id) {
        residenciaService.findById(id);
        return quartoService.findByResidencia(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Residencia create(@RequestBody @Valid Residencia residencia) {
        return residenciaService.save(residencia);
    }

    @PutMapping("/{id}")
    public Residencia update(@PathVariable Long id, @RequestBody @Valid Residencia residencia) {
        return residenciaService.update(id, residencia);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        residenciaService.delete(id);
    }
}