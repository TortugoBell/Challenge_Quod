package br.com.fiap.Challenge_Quod.controller;

import br.com.fiap.Challenge_Quod.model.BiometriaFacial;
import br.com.fiap.Challenge_Quod.repository.BiometriaFacialRepository;
import br.com.fiap.Challenge_Quod.service.BiometriaFacialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/validar")
@RequiredArgsConstructor
public class BiometriaFacialController {

    private final BiometriaFacialService service;
    private final BiometriaFacialRepository repository;

    @PostMapping("/biometria-facial")
    public ResponseEntity<?> validarBiometriaFacial(
            @RequestParam("imagem") MultipartFile imagem,
            @RequestPart("dados") br.com.fiap.Challenge_Quod.dto.BiometriaFacialDTO request
    ) {
        try {
            BiometriaFacial resultado = service.validarBiometriaFacial(
                    imagem,
                    request.getMetadados(),
                    request.getDispositivo()
            );
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Erro ao processar a imagem");
        }
    }

    @GetMapping("/transacao/{id}")
    public ResponseEntity<?> consultarPorTransacaoId(@PathVariable("id") String id) {
        try {
            UUID transacaoId = UUID.fromString(id);
            Optional<BiometriaFacial> biometria = repository.findByTransacaoId(transacaoId);

            return biometria
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("ID de transação inválido");
        }
    }
}