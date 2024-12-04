package br.com.mkcf.cepapi.controller;

import br.com.mkcf.cepapi.model.CepResponse;
import br.com.mkcf.cepapi.service.CepService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ceps")
public class CepController {
    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<CepResponse> consultarCep(@PathVariable String cep) {
        CepResponse response = cepService.consultarCep(cep);
        return ResponseEntity.ok(response);
    }
}