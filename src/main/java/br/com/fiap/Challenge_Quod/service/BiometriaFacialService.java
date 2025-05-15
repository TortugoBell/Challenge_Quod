package br.com.fiap.Challenge_Quod.service;

import br.com.fiap.Challenge_Quod.model.BiometriaFacial;
import br.com.fiap.Challenge_Quod.repository.BiometriaFacialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BiometriaFacialService {

    private final BiometriaFacialRepository repository;

    public BiometriaFacial validarBiometriaFacial(MultipartFile imagem, Map<String, Object> metadados, BiometriaFacial.Dispositivo dispositivo) throws IOException {
        UUID transacaoId = UUID.randomUUID();

        validarImagem(imagem);

        boolean fraudeDetectada = simularFraude();
        String tipoFraude = fraudeDetectada ? sortearTipoFraude() : null;

        BiometriaFacial registro = BiometriaFacial.builder()
                .transacaoId(transacaoId)
                .tipoBiometria("facial")
                .fraudeDetectada(fraudeDetectada)
                .tipoFraude(tipoFraude)
                .dataCaptura(Instant.now())
                .dispositivo(dispositivo)
                .metadados(metadados)
                .imagem(imagem.getBytes())
                .build();

        return repository.save(registro);
    }

    private void validarImagem(MultipartFile imagem) {
        if (imagem == null || imagem.isEmpty()) {
            throw new IllegalArgumentException("Imagem é obrigatória.");
        }

        String contentType = imagem.getContentType();
        if (!Objects.requireNonNull(contentType).startsWith("image/")) {
            throw new IllegalArgumentException("O arquivo enviado não é uma imagem válida.");
        }

        if (imagem.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("A imagem não pode exceder 5MB.");
        }
    }

    private boolean simularFraude() {
        // Simula fraude em 30% dos casos
        return Math.random() < 0.3;
    }

    private String sortearTipoFraude() {
        List<String> tipos = Arrays.asList("deepfake", "máscara", "foto de foto");
        return tipos.get(new Random().nextInt(tipos.size()));
    }
}