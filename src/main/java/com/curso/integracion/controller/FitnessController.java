package com.curso.integracion.controller;

import com.curso.integracion.dto.FitnessResponse;
import com.curso.integracion.dto.IntensidadResponse;
import com.curso.integracion.dto.ObjetivoResponse;
import com.curso.integracion.service.FitnessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/fitness")
@Tag(name = "Fitness", description = "Endpoints para consultar ejercicios y recomendaciones fitness")
public class FitnessController {

    private final FitnessService fitnessService;

    public FitnessController(FitnessService fitnessService) {
        this.fitnessService = fitnessService;
    }

    @GetMapping("/recomendacion")
    @Operation(summary = "Recomienda un ejercicio segun el musculo indicado")
    public FitnessResponse recomendarPorMusculo(
            @Parameter(description = "Musculo que se desea entrenar. Ejemplo: chest, biceps, legs")
            @RequestParam @NotBlank String musculo) {
        return fitnessService.recomendarPorMusculo(musculo);
    }

    @GetMapping("/intensidad")
    @Operation(summary = "Clasifica la intensidad de un ejercicio segun su dificultad")
    public IntensidadResponse clasificarIntensidad(
            @Parameter(description = "Musculo usado para buscar ejercicios. Ejemplo: legs")
            @RequestParam @NotBlank String musculo) {
        return fitnessService.clasificarIntensidad(musculo);
    }

    @GetMapping("/objetivo")
    @Operation(summary = "Recomienda un ejercicio segun el objetivo fisico")
    public ObjetivoResponse recomendarPorObjetivo(
            @Parameter(description = "Objetivo fisico. Valores permitidos: fuerza, resistencia, flexibilidad")
            @RequestParam @NotBlank String objetivo) {
        return fitnessService.recomendarPorObjetivo(objetivo);
    }
}
