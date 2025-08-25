package com.crediya.api.user;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public Mono<Boolean> existsByDni(Long dniClient) {
        return webClient.get()
                .uri("/users/{dni}", dniClient)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just(true);
                    } else if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.just(false);
                    } else {
                        return Mono.error(new RuntimeException("Error checking user existence"));
                    }
                })
                .onErrorReturn(false);
    }
}
