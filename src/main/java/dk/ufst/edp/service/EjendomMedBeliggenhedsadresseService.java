package dk.ufst.edp.service;

import dk.ufst.edp.configurations.AdresseServiceConfig;
import dk.ufst.edp.constants.UrlKey;
import dk.ufst.edp.handlers.BeliggenhedsadresseQueryHandler;
import dk.ufst.edp.handlers.BestemtFastEjendomQueryHandler;
import dk.ufst.edp.model.Beliggenhedsadresse;
import dk.ufst.edp.model.BestemtFastEjendom;
import dk.ufst.edp.model.EjendomMedBeliggenhedsadresse;
import dk.ufst.edp.query.QueryParams;
import dk.ufst.edp.web.exceptions.EdpWebServiceException;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class EjendomMedBeliggenhedsadresseService {
    private static final String APP_ID_HEADER = "EPL_APP_ID";
    private static final String APP_PASSWORD_HEADER = "EPL_APP_PASSWORD";
    private final HttpClient client;
    private final BestemtFastEjendomQueryHandler bfeHandler;
    private final BeliggenhedsadresseQueryHandler ebrHandler;
    private final AdresseServiceConfig config;

    public EjendomMedBeliggenhedsadresseService(BestemtFastEjendomQueryHandler bfeHandler,
                                                BeliggenhedsadresseQueryHandler ebrHandler,
                                                AdresseServiceConfig config,
                                                HttpClient client) {
        this.bfeHandler = bfeHandler;
        this.ebrHandler = ebrHandler;
        this.config = config;
        this.client = client;
    }

    public EjendomMedBeliggenhedsadresse getEjendomMedBeliggenhedsadresse(QueryParams query) throws EdpWebServiceException {
        EjendomMedBeliggenhedsadresse ejendomMedBeliggenhedsadresse;
        try {
            ejendomMedBeliggenhedsadresse = new EjendomMedBeliggenhedsadresse(getBestemtFastEjendom(query), getBeliggenhedsadresse(query));
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new EdpWebServiceException(EdpWebServiceException.ExceptionType.Internal,
                    ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString());
        }


        return ejendomMedBeliggenhedsadresse;
    }

    private BestemtFastEjendom getBestemtFastEjendom(QueryParams query) throws ExecutionException, InterruptedException {
        BestemtFastEjendom bestemtFastEjendom = new BestemtFastEjendom();
        client.sendAsync(createRequest(config.getBfePath(), query), HttpResponse.BodyHandlers.ofInputStream())
                .thenAccept(s -> bfeHandler.populateBestemtFastEjendomFromJsonResponse(bestemtFastEjendom).accept(s.body()))
                .get();
        return bestemtFastEjendom;
    }

    private Beliggenhedsadresse getBeliggenhedsadresse(QueryParams query) throws ExecutionException, InterruptedException {
        Beliggenhedsadresse beliggenhedsadresse = new Beliggenhedsadresse();
        client.sendAsync(createRequest(config.getEbrPath(), query), HttpResponse.BodyHandlers.ofInputStream())
                .thenAccept(s -> ebrHandler.populateBeliggenhedsadresseFromJsonResponse(beliggenhedsadresse).accept(s.body()))
                .get();
        return beliggenhedsadresse;
    }

    private HttpRequest createRequest(String path, QueryParams query) throws HttpClientErrorException {
        URIBuilder uriBuilder = new URIBuilder()
                .setParameter(UrlKey.BFE_NUMMER, String.valueOf(query.getBFEnr()))
                .setParameter(UrlKey.VIRKNINGSTID, query.truncatedVirkningstidToString())
                .setParameter(UrlKey.STATUS, path.equals(config.getEbrPath()) ? query.getStatus().toLowerCase() : StringUtils.capitalize(query.getStatus().toLowerCase()));
        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(config.getGateway() + path + uriBuilder))
                .timeout(Duration.of(30, SECONDS))
                .header(APP_ID_HEADER, config.getCredentials().getId())
                .header(APP_PASSWORD_HEADER, config.getCredentials().getPassword())
                .build();
    }
}

