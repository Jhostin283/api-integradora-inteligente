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
public class ExerciseApiResponse {

    private String name;
    private String type;
    private String muscle;
    private String equipment;
    private List<String> equipments;
    private String difficulty;
    private String instructions;
}
