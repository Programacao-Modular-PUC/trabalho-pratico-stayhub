package com.puc.stayhub.controller;
import com.puc.stayhub.dto.QuartoRequestDTO;
import com.puc.stayhub.model.Quarto;
import com.puc.stayhub.model.TipoQuarto;
import com.puc.stayhub.service.QuartoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/quartos")
@CrossOrigin(origins = "*")
public class QuartoController {
private final QuartoService quartoService;
public QuartoController(QuartoService quartoService) {
this.quartoService = quartoService;
}
@GetMapping
public List<Quarto> findAll(@RequestParam(required = false) Long residenciaId,
@RequestParam(required = false) TipoQuarto tipo) {
if (residenciaId != null && tipo != null) {
return quartoService.findByResidenciaAndTipo(residenciaId, tipo);
}
if (residenciaId != null) return quartoService.findByResidencia(residenciaId);
if (tipo != null) return quartoService.findByTipo(tipo);
return quartoService.findAll();
}
@GetMapping("/{id}")
public Quarto findById(@PathVariable Long id) {
return quartoService.findById(id);
}
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public Quarto create(@RequestBody @Valid QuartoRequestDTO dto) {
return quartoService.criar(dto);
}
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(@PathVariable Long id) {
quartoService.delete(id);
}
}