package dk.ufst.edp.configurations;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@ConfigurationProperties(prefix = "edp")
@Configuration
@NoArgsConstructor
@Setter
@Getter
public class AdresseServiceConfig {
    private String gateway;
    private String bfePath;
    private String ebrPath;
    private AppCredentials credentials;

    private static void throwOnEmptyArg(final String arg, final String message) {
        if (arg == null || arg.isEmpty()) throw new IllegalArgumentException(message);
    }

    @PostConstruct
    protected void postConstruct() {
        throwOnEmptyArg(gateway, "Gateway argument cannot be null or blank.");
        throwOnEmptyArg(bfePath, "BfePath argument cannot be null or blank.");
        throwOnEmptyArg(ebrPath, "EbrPath argument cannot be null or blank.");
    }
}