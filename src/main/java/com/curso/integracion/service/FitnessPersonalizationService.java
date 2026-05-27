package com.curso.integracion.service;

import com.curso.integracion.dto.EjercicioRecomendado;
import com.curso.integracion.dto.ExerciseApiResponse;
import com.curso.integracion.exception.ApiException;
import java.util.Arrays;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FitnessPersonalizationService {

    public EjercicioRecomendado crearEjercicioRecomendado(ExerciseApiResponse ejercicio) {
        return EjercicioRecomendado.builder()
                .ejercicio(traducirEjercicio(ejercicio.getName()))
                .dificultad(traducirDificultad(ejercicio.getDifficulty()))
                .tipo(traducirTipo(ejercicio.getType()))
                .equipo(traducirEquipo(obtenerEquipo(ejercicio)))
                .instruccionesResumen(generarResumenEnEspanol(ejercicio))
                .recomendacion(recomendacionPersonalizada(ejercicio))
                .build();
    }

    public String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase(Locale.ROOT);
    }

    public String musculoParaApi(String musculo) {
        return switch (normalizar(musculo)) {
            case "legs", "piernas" -> "quadriceps";
            case "espalda" -> "middle_back";
            case "pecho" -> "chest";
            default -> normalizar(musculo);
        };
    }

    public String tipoPorObjetivo(String objetivo) {
        return switch (normalizar(objetivo)) {
            case "fuerza" -> "strength";
            case "resistencia" -> "cardio";
            case "flexibilidad" -> "stretching";
            default -> throw new ApiException("Objetivo no permitido", HttpStatus.BAD_REQUEST);
        };
    }

    public String traducirMusculo(String musculo) {
        return switch (normalizar(musculo)) {
            case "abdominals" -> "Abdominales";
            case "abductors" -> "Abductores";
            case "adductors" -> "Aductores";
            case "biceps" -> "Biceps";
            case "calves" -> "Pantorrillas";
            case "chest" -> "Pecho";
            case "forearms" -> "Antebrazos";
            case "glutes" -> "Gluteos";
            case "hamstrings" -> "Isquiotibiales";
            case "lats" -> "Dorsales";
            case "lower_back" -> "Zona lumbar";
            case "middle_back" -> "Espalda media";
            case "neck" -> "Cuello";
            case "quadriceps" -> "Cuadriceps";
            case "traps" -> "Trapecios";
            case "triceps" -> "Triceps";
            default -> valorOIndefinido(musculo);
        };
    }

    public String traducirTipo(String tipo) {
        return switch (normalizar(tipo)) {
            case "strength" -> "Fuerza";
            case "cardio" -> "Cardiovascular";
            case "stretching" -> "Estiramiento";
            case "plyometrics" -> "Pliometria";
            case "powerlifting" -> "Levantamiento de potencia";
            case "olympic_weightlifting" -> "Levantamiento olimpico";
            default -> valorOIndefinido(tipo);
        };
    }

    public String traducirDificultad(String dificultad) {
        return switch (normalizar(dificultad)) {
            case "beginner" -> "Principiante";
            case "intermediate" -> "Intermedio";
            case "expert" -> "Avanzado";
            default -> "No especificada";
        };
    }

    public String traducirEquipoDeEjercicio(ExerciseApiResponse ejercicio) {
        return traducirEquipo(obtenerEquipo(ejercicio));
    }

    public String intensidadPorDificultad(String dificultad) {
        return switch (normalizar(dificultad)) {
            case "beginner" -> "Baja";
            case "intermediate" -> "Media";
            case "expert" -> "Alta";
            default -> "No clasificada";
        };
    }

    public String mensajePorIntensidad(String intensidad) {
        return switch (intensidad) {
            case "Baja" -> "Ideal para iniciar una rutina de entrenamiento.";
            case "Media" -> "Requiere experiencia moderada.";
            case "Alta" -> "Requiere buena condicion fisica y experiencia previa.";
            default -> "Revisar el ejercicio antes de practicarlo.";
        };
    }

    public String recomendacionSeguridad(String intensidad) {
        return switch (intensidad) {
            case "Baja" -> "Realiza el movimiento con calma y prioriza la tecnica.";
            case "Media" -> "Haz calentamiento previo y controla la carga o velocidad.";
            case "Alta" -> "Se recomienda supervision o experiencia previa antes de intentarlo.";
            default -> "Revisa las instrucciones y adapta el ejercicio a tu condicion fisica.";
        };
    }

    public String categoriaPorTipo(String tipo) {
        return switch (normalizar(tipo)) {
            case "strength" -> "Entrenamiento funcional";
            case "cardio" -> "Resistencia cardiovascular";
            case "stretching" -> "Flexibilidad";
            default -> "Entrenamiento general";
        };
    }

    public String recomendacionPorObjetivo(String objetivo) {
        return switch (normalizar(objetivo)) {
            case "fuerza" -> "Ideal para ganar fuerza muscular.";
            case "resistencia" -> "Ideal para mejorar la capacidad cardiovascular.";
            case "flexibilidad" -> "Ideal para mejorar movilidad y elasticidad.";
            default -> "Objetivo no reconocido.";
        };
    }

    public String generarResumenEnEspanol(ExerciseApiResponse ejercicio) {
        String tipo = descripcionTipo(ejercicio.getType());
        String musculo = traducirMusculo(ejercicio.getMuscle()).toLowerCase(Locale.ROOT);
        String equipo = traducirEquipoDeEjercicio(ejercicio).toLowerCase(Locale.ROOT);
        String dificultad = traducirDificultad(ejercicio.getDifficulty()).toLowerCase(Locale.ROOT);

        if ("no especificado".equals(equipo)) {
            return "Ejercicio " + tipo + " enfocado en " + musculo
                    + ". Nivel " + dificultad + ". Realiza el movimiento con control y prioriza la tecnica.";
        }

        return "Ejercicio " + tipo + " enfocado en " + musculo
                + ". Requiere " + equipo + " y tiene nivel " + dificultad
                + ". Ejecutalo con control, buena postura y respiracion constante.";
    }

    public String traducirEjercicio(String nombre) {
        return switch (normalizar(nombre)) {
            case "dumbbell bench press" -> "Press de banca con mancuernas";
            case "incline dumbbell bench press" -> "Press inclinado con mancuernas";
            case "incline hammer curls", "incline hammer curl" -> "Curl martillo inclinado";
            case "hammer curls", "hammer curl" -> "Curl martillo";
            case "wide grip barbell curl", "wide-grip barbell curl" -> "Curl con barra y agarre amplio";
            case "ez bar curl", "e-z bar curl", "e-z curl bar curl" -> "Curl con barra Z";
            case "close-grip bench press" -> "Press de banca con agarre cerrado";
            case "wide-grip decline barbell bench press",
                    "wide grip decline barbell bench press",
                    "wide-grip declined barbell bench press",
                    "wide grip declined barbell bench press" -> "Press de banca declinado con barra y agarre amplio";
            case "bodyweight flyes", "bodyweight fly", "bodyweight flies" -> "Aperturas con peso corporal";
            case "low-cable cross-over", "low cable cross-over", "low cable crossover" -> "Cruce en polea baja";
            case "dumbbell flyes", "dumbbell fly", "dumbbell flies" -> "Aperturas con mancuernas";
            case "barbell bench press - medium grip" -> "Press de banca con barra";
            case "bench press" -> "Press de banca";
            case "pushups", "push-up", "push up" -> "Flexiones de pecho";
            case "squat", "bodyweight squat" -> "Sentadilla";
            case "barbell squat" -> "Sentadilla con barra";
            case "single leg push-off", "single-leg push-off" -> "Impulso con una pierna";
            case "jumping rope", "jump rope", "rope jumping", "skipping rope" -> "Saltar la cuerda";
            case "running", "run" -> "Correr";
            case "walking", "walk" -> "Caminar";
            case "cycling", "bicycling", "stationary bike" -> "Bicicleta";
            case "burpee", "burpees" -> "Burpees";
            case "mountain climbers", "mountain climber" -> "Escaladores";
            case "box jump", "box jumps" -> "Saltos al cajon";
            case "step-up", "step up", "step-ups", "step ups" -> "Subidas al banco";
            case "treadmill running", "treadmill" -> "Correr en cinta";
            case "dumbbell bicep curl", "dumbbell biceps curl" -> "Curl de biceps con mancuernas";
            default -> nombreTraducidoSeguro(nombre);
        };
    }

    private String recomendacionPersonalizada(ExerciseApiResponse ejercicio) {
        String nombre = normalizar(ejercicio.getName());
        String equipo = normalizar(obtenerEquipo(ejercicio));
        String dificultad = normalizar(ejercicio.getDifficulty());
        String musculo = normalizar(ejercicio.getMuscle());

        if (nombre.contains("close-grip")) {
            return "Buena opcion para trabajar pecho con mayor participacion de triceps.";
        }
        if (nombre.contains("fly")) {
            return "Util para aislar el pecho y mejorar el control del movimiento.";
        }
        if (nombre.contains("cross-over") || nombre.contains("crossover")) {
            return "Recomendado para trabajar el pecho con tension continua en polea.";
        }
        if (nombre.contains("incline") && "chest".equals(musculo)) {
            return "Ideal para dar mayor enfasis a la zona superior del pecho.";
        }
        if (nombre.contains("incline") && "biceps".equals(musculo)) {
            return "Buena opcion para trabajar el biceps con mayor rango de movimiento.";
        }
        if (nombre.contains("hammer")) {
            return "Ayuda a trabajar biceps y antebrazo con agarre neutral.";
        }
        if (nombre.contains("wide grip") || nombre.contains("wide-grip")) {
            return "Permite variar el agarre y estimular el musculo desde otro angulo.";
        }
        if (nombre.contains("ez bar")) {
            return "La barra Z ayuda a trabajar el biceps con una posicion mas comoda para las munecas.";
        }
        if (equipo.contains("dumbbell")) {
            return "Permite trabajar cada lado del cuerpo de forma mas equilibrada.";
        }
        if (equipo.contains("barbell")) {
            return "Recomendado para ejercicios de fuerza con carga progresiva.";
        }
        if ("beginner".equals(dificultad)) {
            return "Ideal para aprender la tecnica antes de aumentar la intensidad.";
        }

        return switch (dificultad) {
            case "intermediate" -> "Recomendado para personas con experiencia moderada.";
            case "expert" -> "Recomendado para personas con experiencia avanzada.";
            default -> "Consulta las instrucciones antes de realizar el ejercicio.";
        };
    }

    private String obtenerEquipo(ExerciseApiResponse ejercicio) {
        if (ejercicio.getEquipments() != null && !ejercicio.getEquipments().isEmpty()) {
            return String.join(", ", ejercicio.getEquipments());
        }
        if (ejercicio.getEquipment() != null && !ejercicio.getEquipment().isBlank()) {
            return ejercicio.getEquipment();
        }

        String nombre = normalizar(ejercicio.getName());
        if (nombre.contains("dumbbell") || nombre.contains("hammer curl")) {
            return "dumbbells";
        }
        if (nombre.contains("barbell") || nombre.contains("bench press")
                || nombre.contains("wide grip barbell curl") || nombre.contains("wide-grip barbell curl")) {
            return "barbell";
        }
        if (nombre.contains("ez bar") || nombre.contains("e-z bar") || nombre.contains("e-z curl")) {
            return "e-z_curl_bar";
        }
        if (nombre.contains("cable")) {
            return "cable";
        }
        if (nombre.contains("band")) {
            return "bands";
        }
        if (nombre.contains("machine")) {
            return "machine";
        }
        if (nombre.contains("kettlebell")) {
            return "kettlebells";
        }
        if (nombre.contains("jumping rope") || nombre.contains("jump rope")
                || nombre.contains("rope jumping") || nombre.contains("skipping rope")) {
            return "jump_rope";
        }
        if (nombre.contains("treadmill")) {
            return "machine";
        }
        if (nombre.contains("cycling") || nombre.contains("bicycling") || nombre.contains("bike")) {
            return "machine";
        }
        if (nombre.contains("box jump") || nombre.contains("step-up") || nombre.contains("step up")) {
            return "bench";
        }
        if (nombre.contains("bodyweight") || nombre.contains("pushup") || nombre.contains("push-up")
                || nombre.contains("single leg push-off") || nombre.contains("single-leg push-off")
                || nombre.contains("burpee") || nombre.contains("mountain climber")
                || nombre.contains("running") || nombre.contains("walking")) {
            return "body_only";
        }

        return "";
    }

    private String traducirEquipo(String equipo) {
        if (equipo != null && equipo.contains(",")) {
            return Arrays.stream(equipo.split(","))
                    .map(String::trim)
                    .filter(valor -> !valor.isBlank())
                    .map(this::traducirEquipo)
                    .distinct()
                    .reduce((primero, segundo) -> primero + ", " + segundo)
                    .orElse("No especificado");
        }

        return switch (normalizar(equipo)) {
            case "body_only" -> "Peso corporal";
            case "machine" -> "Maquina";
            case "other" -> "Otro";
            case "foam_roll" -> "Rodillo de espuma";
            case "kettlebells" -> "Pesas rusas";
            case "dumbbell", "dumbbells" -> "Mancuernas";
            case "cable" -> "Polea";
            case "barbell", "barbells" -> "Barra";
            case "bands" -> "Bandas elasticas";
            case "medicine_ball" -> "Balon medicinal";
            case "exercise_ball" -> "Pelota de ejercicio";
            case "e-z_curl_bar", "e-z curl bar", "ez curl bar", "ez bar", "curl bar" -> "Barra Z";
            case "jump_rope", "jump rope", "rope" -> "Cuerda para saltar";
            case "flat bench", "bench", "incline bench", "decline bench" -> "Banco";
            default -> valorOIndefinido(equipo);
        };
    }

    private String descripcionTipo(String tipo) {
        return switch (normalizar(tipo)) {
            case "strength" -> "de fuerza";
            case "cardio" -> "cardiovascular";
            case "stretching" -> "de estiramiento";
            case "plyometrics" -> "pliometrico";
            case "powerlifting" -> "de levantamiento de potencia";
            case "olympic_weightlifting" -> "de levantamiento olimpico";
            default -> "general";
        };
    }

    private String traducirNombrePorPalabras(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "Ejercicio no especificado";
        }

        return nombre
                .replace("Wide-Grip", "Agarre amplio")
                .replace("Wide Grip", "Agarre amplio")
                .replace("Close-grip", "Agarre cerrado")
                .replace("Close Grip", "Agarre cerrado")
                .replace("Bodyweight", "Peso corporal")
                .replace("Single Leg", "Una pierna")
                .replace("Single-Leg", "Una pierna")
                .replace("Push-off", "Impulso")
                .replace("Push Off", "Impulso")
                .replace("Jumping Rope", "Saltar la cuerda")
                .replace("Jump Rope", "Saltar la cuerda")
                .replace("Rope Jumping", "Saltar la cuerda")
                .replace("Skipping Rope", "Saltar la cuerda")
                .replace("Mountain Climbers", "Escaladores")
                .replace("Mountain Climber", "Escalador")
                .replace("Box Jumps", "Saltos al cajon")
                .replace("Box Jump", "Salto al cajon")
                .replace("Step-ups", "Subidas al banco")
                .replace("Step-Ups", "Subidas al banco")
                .replace("Step-up", "Subida al banco")
                .replace("Step Up", "Subida al banco")
                .replace("Running", "Correr")
                .replace("Walking", "Caminar")
                .replace("Cycling", "Bicicleta")
                .replace("Bicycling", "Bicicleta")
                .replace("Stationary Bike", "Bicicleta estatica")
                .replace("Treadmill", "Cinta de correr")
                .replace("Burpees", "Burpees")
                .replace("Burpee", "Burpee")
                .replace("Hammer Curls", "Curl martillo")
                .replace("Hammer Curl", "Curl martillo")
                .replace("EZ Bar Curl", "Curl con barra Z")
                .replace("E-Z Bar Curl", "Curl con barra Z")
                .replace("Bar Curl", "Curl con barra")
                .replace("Low-cable", "Polea baja")
                .replace("Low Cable", "Polea baja")
                .replace("Cross-over", "Cruce")
                .replace("Crossover", "Cruce")
                .replace("Flyes", "Aperturas")
                .replace("Fly", "Apertura")
                .replace("Dumbbells", "Mancuernas")
                .replace("Dumbbell", "Mancuerna")
                .replace("Barbell", "Barra")
                .replace("Incline", "Inclinado")
                .replace("Declined", "Declinado")
                .replace("Bench Press", "Press de banca")
                .replace("Bench", "Banca")
                .replace("Squat", "Sentadilla")
                .replace("Push", "Empuje")
                .replace("Pull", "Jalon")
                .replace("Raise", "Elevacion")
                .replace("Extension", "Extension")
                .replace("Stretch", "Estiramiento")
                .replace("Seated", "Sentado")
                .replace("Standing", "De pie")
                .replace("Decline", "Declinado")
                .replace("-", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String nombreTraducidoSeguro(String nombre) {
        String traducido = traducirNombrePorPalabras(nombre);

        if (contieneInglesComun(traducido)) {
            return "Ejercicio traducido de " + clasificacionNombre(nombre);
        }

        return traducido;
    }

    private boolean contieneInglesComun(String valor) {
        String texto = normalizar(valor);
        return texto.matches(".*\\b(press|bench|jump|rope|running|walking|curl|raise|pull|push|step|fly|row|kick|extension|machine|barbell|dumbbell)\\b.*");
    }

    private String clasificacionNombre(String nombre) {
        String texto = normalizar(nombre);
        if (texto.contains("curl")) {
            return "biceps";
        }
        if (texto.contains("jump") || texto.contains("running") || texto.contains("walking")
                || texto.contains("rope") || texto.contains("burpee")) {
            return "cardio";
        }
        if (texto.contains("stretch")) {
            return "estiramiento";
        }
        if (texto.contains("press") || texto.contains("bench")) {
            return "fuerza";
        }
        return "entrenamiento";
    }

    private String valorOIndefinido(String valor) {
        return valor == null || valor.isBlank() ? "No especificado" : valor;
    }
}
