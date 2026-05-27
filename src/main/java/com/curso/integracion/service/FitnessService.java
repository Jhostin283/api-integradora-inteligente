package com.curso.integracion.service;

import com.curso.integracion.client.ExerciseApiClient;
import com.curso.integracion.dto.EjercicioRecomendado;
import com.curso.integracion.dto.ExerciseApiResponse;
import com.curso.integracion.dto.FitnessResponse;
import com.curso.integracion.dto.IntensidadResponse;
import com.curso.integracion.dto.ObjetivoResponse;
import com.curso.integracion.exception.ApiException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FitnessService {

    private static final int LIMITE_RECOMENDACIONES = 5;

    private final ExerciseApiClient exerciseApiClient;
    private final FitnessPersonalizationService personalizationService;

    public FitnessService(
            ExerciseApiClient exerciseApiClient,
            FitnessPersonalizationService personalizationService
    ) {
        this.exerciseApiClient = exerciseApiClient;
        this.personalizationService = personalizationService;
    }

    public FitnessResponse recomendarPorMusculo(String musculo) {
        List<ExerciseApiResponse> ejerciciosApi = buscarEjerciciosVariadosPorMusculo(musculo);
        String musculoTraducido = personalizationService.traducirMusculo(ejerciciosApi.get(0).getMuscle());

        List<EjercicioRecomendado> ejercicios = ejerciciosApi.stream()
                .limit(LIMITE_RECOMENDACIONES)
                .map(personalizationService::crearEjercicioRecomendado)
                .toList();

        return FitnessResponse.builder()
                .musculo(musculoTraducido)
                .musculoTrabajado(musculoTraducido)
                .cantidad(ejercicios.size())
                .mensaje("Se encontraron ejercicios recomendados para trabajar "
                        + musculoTraducido.toLowerCase(Locale.ROOT) + ".")
                .ejercicios(ejercicios)
                .build();
    }

    public IntensidadResponse clasificarIntensidad(String musculo) {
        ExerciseApiResponse ejercicio = buscarPrimerEjercicioPorMusculo(musculo);
        String intensidad = personalizationService.intensidadPorDificultad(ejercicio.getDifficulty());

        return IntensidadResponse.builder()
                .musculo(personalizationService.traducirMusculo(ejercicio.getMuscle()))
                .ejercicio(personalizationService.traducirEjercicio(ejercicio.getName()))
                .tipo(personalizationService.traducirTipo(ejercicio.getType()))
                .equipo(personalizationService.traducirEquipoDeEjercicio(ejercicio))
                .dificultad(personalizationService.traducirDificultad(ejercicio.getDifficulty()))
                .nivelIntensidad(intensidad)
                .mensaje(personalizationService.mensajePorIntensidad(intensidad))
                .recomendacionSeguridad(personalizationService.recomendacionSeguridad(intensidad))
                .build();
    }

    public ObjetivoResponse recomendarPorObjetivo(String objetivo) {
        String objetivoNormalizado = personalizationService.normalizar(objetivo);
        String tipo = personalizationService.tipoPorObjetivo(objetivoNormalizado);
        ExerciseApiResponse ejercicio = buscarPrimerEjercicioPorTipo(tipo, objetivoNormalizado);

        return ObjetivoResponse.builder()
                .objetivo(objetivoNormalizado)
                .ejercicio(personalizationService.traducirEjercicio(ejercicio.getName()))
                .tipo(personalizationService.traducirTipo(ejercicio.getType()))
                .musculoTrabajado(personalizationService.traducirMusculo(ejercicio.getMuscle()))
                .equipo(personalizationService.traducirEquipoDeEjercicio(ejercicio))
                .dificultad(personalizationService.traducirDificultad(ejercicio.getDifficulty()))
                .categoria(personalizationService.categoriaPorTipo(ejercicio.getType()))
                .instruccionesResumen(personalizationService.generarResumenEnEspanol(ejercicio))
                .recomendacion(personalizationService.recomendacionPorObjetivo(objetivoNormalizado))
                .build();
    }

    private ExerciseApiResponse buscarPrimerEjercicioPorMusculo(String musculo) {
        return buscarEjerciciosVariadosPorMusculo(musculo).get(0);
    }

    private List<ExerciseApiResponse> buscarEjerciciosVariadosPorMusculo(String musculo) {
        String musculoApi = personalizationService.musculoParaApi(musculo);
        List<ExerciseApiResponse> ejercicios = consultarPorDificultades(musculoApi);

        if (ejercicios.isEmpty()) {
            ejercicios = exerciseApiClient.buscarPorMusculo(musculoApi);
        }

        if (ejercicios == null || ejercicios.isEmpty()) {
            throw new ApiException("Musculo no encontrado", HttpStatus.NOT_FOUND);
        }

        return ejercicios;
    }

    private List<ExerciseApiResponse> consultarPorDificultades(String musculoApi) {
        List<ExerciseApiResponse> ejercicios = new ArrayList<>();

        agregarPrimerosPorDificultad(ejercicios, musculoApi, "beginner", 2);
        agregarPrimerosPorDificultad(ejercicios, musculoApi, "intermediate", 2);
        agregarPrimerosPorDificultad(ejercicios, musculoApi, "expert", 1);

        return ejercicios;
    }

    private void agregarPrimerosPorDificultad(
            List<ExerciseApiResponse> destino,
            String musculoApi,
            String dificultad,
            int limite
    ) {
        List<ExerciseApiResponse> encontrados = exerciseApiClient.buscarPorMusculoYDificultad(musculoApi, dificultad);

        if (encontrados == null || encontrados.isEmpty()) {
            return;
        }

        encontrados.stream()
                .filter(ejercicio -> ejercicio.getName() != null && !ejercicio.getName().isBlank())
                .filter(ejercicio -> destino.stream().noneMatch(actual -> mismoEjercicio(actual, ejercicio)))
                .limit(limite)
                .forEach(destino::add);
    }

    private boolean mismoEjercicio(ExerciseApiResponse actual, ExerciseApiResponse candidato) {
        return personalizationService.normalizar(actual.getName())
                .equals(personalizationService.normalizar(candidato.getName()));
    }

    private ExerciseApiResponse buscarPrimerEjercicioPorTipo(String tipo, String objetivo) {
        List<ExerciseApiResponse> ejercicios = exerciseApiClient.buscarPorTipo(tipo);

        if (ejercicios == null || ejercicios.isEmpty()) {
            throw new ApiException("No se encontraron ejercicios para el objetivo: " + objetivo,
                    HttpStatus.NOT_FOUND);
        }

        if ("resistencia".equals(objetivo)) {
            return ejercicios.stream()
                    .filter(this::esEjercicioDeCuerda)
                    .findFirst()
                    .orElseGet(() -> buscarEjercicioConEquipo(ejercicios));
        }

        return buscarEjercicioConEquipo(ejercicios);
    }

    private ExerciseApiResponse buscarEjercicioConEquipo(List<ExerciseApiResponse> ejercicios) {
        return ejercicios.stream()
                .filter(ejercicio -> !"No especificado"
                        .equals(personalizationService.traducirEquipoDeEjercicio(ejercicio)))
                .findFirst()
                .orElse(ejercicios.get(0));
    }

    private boolean esEjercicioDeCuerda(ExerciseApiResponse ejercicio) {
        String nombre = personalizationService.normalizar(ejercicio.getName());
        return nombre.contains("jumping rope")
                || nombre.contains("jump rope")
                || nombre.contains("rope jumping")
                || nombre.contains("skipping rope");
    }
}
