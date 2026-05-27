package com.curso.integracion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjetivoResponse {

    private String objetivo;
    private String ejercicio;
    private String tipo;
    private String musculoTrabajado;
    private String equipo;
    private String dificultad;
    private String categoria;
    private String instruccionesResumen;
    private String recomendacion;
}
