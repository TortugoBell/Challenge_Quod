package br.com.fiap.Challenge_Quod.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Document(collection = "biometrias_facial")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BiometriaFacial {

    @Id
    private String id;

    private UUID transacaoId;
    private String tipoBiometria;
    private boolean fraudeDetectada;
    private String tipoFraude;

    private Instant dataCaptura;

    private Dispositivo dispositivo;
    private Map<String, Object> metadados;

    private byte[] imagem;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Dispositivo {
        @NotBlank(message = "Fabricante do dispositivo é obrigatório.")
        private String fabricante;

        @NotBlank(message = "Modelo do dispositivo é obrigatório.")
        private String modelo;

        @NotBlank(message = "Sistema operacional do dispositivo é obrigatório.")
        private String sistemaOperacional;
    }
}