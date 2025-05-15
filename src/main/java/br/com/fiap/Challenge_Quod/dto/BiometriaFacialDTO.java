package br.com.fiap.Challenge_Quod.dto;

import br.com.fiap.Challenge_Quod.model.BiometriaFacial;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class BiometriaFacialDTO {
    @NotNull(message = "Os dados do dispositivo são obrigatórios.")
    @Valid
    private BiometriaFacial.Dispositivo dispositivo;

    @NotNull(message = "Os metadados são obrigatórios.")
    @NotEmpty(message = "Os metadados não podem estar vazios.")
    private Map<String, Object> metadados;
}