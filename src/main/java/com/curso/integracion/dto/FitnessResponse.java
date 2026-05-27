package com.curso.integracion.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FitnessResponse {

    private String musculo;
    private String musculoTrabajado;
    private Integer cantidad;
    private String mensaje;
    private List<EjercicioRecomendado> ejercicios;
}
