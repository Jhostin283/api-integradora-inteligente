package com.curso.integracion.client;

import com.curso.integracion.config.ApiProperties;
import com.curso.integracion.dto.ExerciseApiResponse;
import com.curso.integracion.exception.ApiException;
import java.net.URI;
import java.util.List;
import java.util.function.Function;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;

@Component
public class ExerciseApiClient {

    private static final String API_KEY_HEADER = "X-Api-Key";

    private final RestClient restClient;

    public ExerciseApiClient(RestClient.Builder restClientBuilder, ApiProperties apiProperties) {
        this.restClient = restClientBuilder
                .baseUrl(apiProperties.baseUrl())
                .defaultHeader(API_KEY_HEADER, apiProperties.key())
                .build();
    }

    public List<ExerciseApiResponse> buscarPorMusculo(String musculo) {
        return consultar(uriBuilder -> uriBuilder
                .queryParam("muscle", musculo)
                .build());
    }

    public List<ExerciseApiResponse> buscarPorMusculoYDificultad(String musculo, String dificultad) {
        return consultar(uriBuilder -> uriBuilder
                .queryParam("muscle", musculo)
                .queryParam("difficulty", dificultad)
                .build());
    }

    public List<ExerciseApiResponse> buscarPorTipo(String tipo) {
        return consultar(uriBuilder -> uriBuilder
                .queryParam("type", tipo)
                .build());
    }

    private List<ExerciseApiResponse> consultar(Function<UriBuilder, URI> uriFunction) {
        try {
            return restClient.get()
                    .uri(uriFunction)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED
                                || response.getStatusCode() == HttpStatus.FORBIDDEN) {
                            throw new ApiException("API Key invalida o no autorizada", HttpStatus.UNAUTHORIZED);
                        }

                        throw new ApiException("Solicitud invalida hacia la API externa", HttpStatus.BAD_REQUEST);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new ApiException("Error de la API externa", HttpStatus.INTERNAL_SERVER_ERROR);
                    })
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (ApiException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new ApiException("No se pudo consumir la API externa", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
