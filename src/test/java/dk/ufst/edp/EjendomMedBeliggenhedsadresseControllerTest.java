package dk.ufst.edp;


import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ufst.edp.model.EjendomMedBeliggenhedsadresse;
import dk.ufst.edp.service.EjendomMedBeliggenhedsadresseService;
import dk.ufst.edp.web.exceptions.EdpWebServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.InputStream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "dev")
class EjendomMedBeliggenhedsadresseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EjendomMedBeliggenhedsadresseService service;

    @Test
    void callingEjendomMedBeliggenhedsadresseReturnsOk() throws Exception {
        //Arrange
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("EjendomMedBeliggenhedsadresseResponse.json");

        ObjectMapper objectMapper = new ObjectMapper();
        EjendomMedBeliggenhedsadresse ejendomMedBeliggenhedsadresse = objectMapper.readValue(inputStream, EjendomMedBeliggenhedsadresse.class);
        ejendomMedBeliggenhedsadresse.getBeliggenhedsadresse().setPopulated(true);
        ejendomMedBeliggenhedsadresse.getBestemtFastEjendom().setPopulated(true);

        Mockito.when(service.getEjendomMedBeliggenhedsadresse(any())).thenReturn(ejendomMedBeliggenhedsadresse);

        //Act
        mockMvc.perform(MockMvcRequestBuilders.get("/api/EjendomMedBeliggenhedsadresse").param("BFEnr", "123"))
                //Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.BestemtFastEjendom", notNullValue()))
                .andExpect(jsonPath("$.Beliggenhedsadresse", notNullValue()))
                .andDo(print());
    }

    @Test
    void returnInternalServerErrorOnFailure() throws Exception {
        //Arrange
        when(service.getEjendomMedBeliggenhedsadresse(any())).thenThrow(new EdpWebServiceException(EdpWebServiceException.ExceptionType.Internal));

        //Act
        mockMvc.perform(get("/api/EjendomMedBeliggenhedsadresse").param("BFEnr", "123123"))
                //Assert
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", is("Kontakt venligst support med oplysningen fejl 500")))
                .andDo(print());

    }

    @Test
    void returnBadRequest() throws Exception {
        //Arrange
        when(service.getEjendomMedBeliggenhedsadresse(any())).thenThrow(new EdpWebServiceException(EdpWebServiceException.ExceptionType.Internal));

        //Act
        mockMvc.perform(get("/api/EjendomMedBeliggenhedsadresse"))
                //Assert
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("[\"BFEnr: er påkrævet\"]")))
                .andDo(print());
    }

    @Test
    void callingEjendomMedBeliggenhedsadresseReturnsNoContent() throws Exception {
        //Arrange
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("EjendomMedBeliggenhedsadresseResponse.json");

        ObjectMapper objectMapper = new ObjectMapper();
        EjendomMedBeliggenhedsadresse ejendomMedBeliggenhedsadresse = objectMapper.readValue(inputStream, EjendomMedBeliggenhedsadresse.class);

        Mockito.when(service.getEjendomMedBeliggenhedsadresse(any())).thenReturn(ejendomMedBeliggenhedsadresse);

        //Act
        mockMvc.perform(MockMvcRequestBuilders.get("/api/EjendomMedBeliggenhedsadresse").param("BFEnr", "123123"))
                //Assert
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
