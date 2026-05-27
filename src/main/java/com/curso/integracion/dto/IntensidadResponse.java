package com.curso.integracion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntensidadResponse {

    private String musculo;
    private String ejercicio;
    private String tipo;
    private String equipo;
    private String dificultad;
    private String nivelIntensidad;
    private String mensaje;
    private String recomendacionSeguridad;
}
