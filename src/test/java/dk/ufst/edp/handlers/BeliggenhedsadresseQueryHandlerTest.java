package dk.ufst.edp.handlers;

import dk.ufst.edp.model.Beliggenhedsadresse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

@ExtendWith(SpringExtension.class)
class BeliggenhedsadresseQueryHandlerTest {
    private final ClassLoader classLoader = getClass().getClassLoader();

    @Test
    void populateBeliggenhedsadresseFromJsonResponse() throws IOException {
        //Arrange
        InputStream inputStream = classLoader.getResourceAsStream("beliggenhedsadresseRresponse.json");

        BeliggenhedsadresseQueryHandler sut = new BeliggenhedsadresseQueryHandler();
        Beliggenhedsadresse beliggenhedsadresse = new Beliggenhedsadresse();

        var metode = sut.populateBeliggenhedsadresseFromJsonResponse(beliggenhedsadresse);

        //Act
        metode.accept(inputStream);
        inputStream.close();

        //Assert
        assertThat(beliggenhedsadresse.getBestemtFastEjendomBFENr()).isEqualTo("501031");
        assertThat(beliggenhedsadresse.getAdresse().get(0).getAdressebetegnelse()).isEqualTo("Nivå Center 60, 2990 Nivå");
        assertThat(beliggenhedsadresse.getAdresse().get(0).getHusnummer().getPostnummer().getPostnr()).isEqualTo("2990");
    }

    @Test
    void populateBeliggenhedsadresseFromJsonResponseIsNull() throws IOException {
        //Arrange
        InputStream inputStream = classLoader.getResourceAsStream("beliggenhedsadresseNoResponse.json");

        BeliggenhedsadresseQueryHandler sut = new BeliggenhedsadresseQueryHandler();
        Beliggenhedsadresse beliggenhedsadresse = new Beliggenhedsadresse();

        var metode = sut.populateBeliggenhedsadresseFromJsonResponse(beliggenhedsadresse);

        //Act
        metode.accept(inputStream);
        inputStream.close();

        //Assert
        assertNull(beliggenhedsadresse.getBestemtFastEjendomBFENr());
    }
}
