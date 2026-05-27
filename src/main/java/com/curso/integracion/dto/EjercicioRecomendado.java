package com.curso.integracion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EjercicioRecomendado {

    private String ejercicio;
    private String dificultad;
    private String tipo;
    private String equipo;
    private String instruccionesResumen;
    private String recomendacion;
}
