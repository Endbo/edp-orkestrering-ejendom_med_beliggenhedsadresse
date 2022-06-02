package dk.ufst.edp.handlers;

import dk.ufst.edp.model.BestemtFastEjendom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

@ExtendWith(SpringExtension.class)
class BestemtFastEjendomQueryHandlerTest {

    private final ClassLoader classLoader = getClass().getClassLoader();

    @Test
    void populateSamletFastEjendomFromJsonResponse() throws IOException {
        //Arrange
        InputStream inputStream = classLoader.getResourceAsStream("bfeMedSamletFastEjendomSomHarJordstykkeResponse.json");
        BestemtFastEjendomQueryHandler sut = new BestemtFastEjendomQueryHandler();
        BestemtFastEjendom bestemtFastEjendom = new BestemtFastEjendom();

        var metode = sut.populateBestemtFastEjendomFromJsonResponse(bestemtFastEjendom);

        //Act
        metode.accept(inputStream);
        inputStream.close();

        //Assert
        assertThat(bestemtFastEjendom.getSamletFastEjendom().getBFEnummer()).isEqualTo(9135983);
        assertThat(bestemtFastEjendom.getSamletFastEjendom().getJordstykke().get(0).getStatus()).isEqualTo("Gældende");
        assertThat(bestemtFastEjendom.getSamletFastEjendom().getJordstykke().get(0).getJordrente().getOmfang()).isNull();
    }

    @Test
    void populateEjerlejlighedFromJsonResponse() throws IOException {
        //Arrange
        InputStream inputStream = classLoader.getResourceAsStream("bfeMedEjerlejlighedResponse.json");
        BestemtFastEjendomQueryHandler sut = new BestemtFastEjendomQueryHandler();
        BestemtFastEjendom bestemtFastEjendom = new BestemtFastEjendom();

        var metode = sut.populateBestemtFastEjendomFromJsonResponse(bestemtFastEjendom);

        //Act
        metode.accept(inputStream);
        inputStream.close();

        //Assert
        assertThat(bestemtFastEjendom.getEjerlejlighed().getBFEnummer()).isEqualTo(366311);
        assertThat(bestemtFastEjendom.getEjerlejlighed().getStatus()).isEqualTo("Gældende");
    }

    @Test
    void populateBygningPaaFremmedGrundFromJsonResponse() throws IOException {
        //Arrange
        InputStream inputStream = classLoader.getResourceAsStream("bfeMedBygningPaaFremmedGrundResponse.json");
        BestemtFastEjendomQueryHandler sut = new BestemtFastEjendomQueryHandler();
        BestemtFastEjendom bestemtFastEjendom = new BestemtFastEjendom();

        var metode = sut.populateBestemtFastEjendomFromJsonResponse(bestemtFastEjendom);

        //Act
        metode.accept(inputStream);
        inputStream.close();

        //Assert
        assertThat(bestemtFastEjendom.getBygningPaaFremmedGrund().getSamletFastEjendomBFEnummer()).isEqualTo("100095834");
    }

    @Test
    void populateBestemtFastEjendomFromJsonResponseIsNull() throws IOException {
        //Arrange
        InputStream inputStream = classLoader.getResourceAsStream("bestemtFastEjendomNoResponse.json");
        BestemtFastEjendomQueryHandler sut = new BestemtFastEjendomQueryHandler();
        BestemtFastEjendom bestemtFastEjendom = new BestemtFastEjendom();

        var metode = sut.populateBestemtFastEjendomFromJsonResponse(bestemtFastEjendom);

        //Act
        metode.accept(inputStream);
        inputStream.close();

        //Assert
        assertNull(bestemtFastEjendom.getSamletFastEjendom());
        assertNull(bestemtFastEjendom.getEjerlejlighed());
        assertNull(bestemtFastEjendom.getBygningPaaFremmedGrund());
    }
}
