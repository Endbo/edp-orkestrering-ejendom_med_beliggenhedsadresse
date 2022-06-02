package dk.ufst.edp.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
class HttpConfig {
    @Bean
    public HttpClient getHttpClient() {
        return HttpClient.newHttpClient();
    }
}
