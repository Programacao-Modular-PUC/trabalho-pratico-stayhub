import com.puc.stayhub.dto.AluguelRequestDTO;
import com.puc.stayhub.model.Aluguel;
import com.puc.stayhub.service.AluguelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/alugueis")
@CrossOrigin(origins = "*")
public class AluguelController {
private final AluguelService aluguelService;
public AluguelController(AluguelService aluguelService) {
this.aluguelService = aluguelService;
}
@GetMapping
public List<Aluguel> findAll() {
return aluguelService.findAll();
}
@GetMapping("/{id}")
public Aluguel findById(@PathVariable Long id) {
return aluguelService.findById(id);
}
@GetMapping("/cliente/{clienteId}")
public List<Aluguel> findByCliente(@PathVariable Long clienteId) {
return aluguelService.findByCliente(clienteId);
}
@GetMapping("/cliente/{clienteId}/historico")
public List<Aluguel> historicoPorCliente(@PathVariable Long clienteId) {
return aluguelService.historicoPorCliente(clienteId);
}
@GetMapping("/quarto/{quartoId}")
public List<Aluguel> findByQuarto(@PathVariable Long quartoId) {
return aluguelService.findByQuarto(quartoId);
}
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public Aluguel create(@RequestBody @Valid AluguelRequestDTO dto) {
return aluguelService.criar(dto);
}
@PatchMapping("/{id}/cancelar")
public Aluguel cancelar(@PathVariable Long id) {
return aluguelService.cancelar(id);
}
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(@PathVariable Long id) {
aluguelService.delete(id);
}
}