package dk.ufst.edp.controller;

import dk.ufst.edp.model.EjendomMedBeliggenhedsadresse;
import dk.ufst.edp.query.QueryParams;
import dk.ufst.edp.service.EjendomMedBeliggenhedsadresseService;
import dk.ufst.edp.web.exceptions.EdpWebServiceException;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Api(tags = "EjendomMedBeliggenhedsadresse")
@Validated
@RestController
@RequestMapping("api/EjendomMedBeliggenhedsadresse")
public class EjendomMedBeliggenhedsadresseController {
    private final EjendomMedBeliggenhedsadresseService service;

    public EjendomMedBeliggenhedsadresseController(EjendomMedBeliggenhedsadresseService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<EjendomMedBeliggenhedsadresse> getEjendomMedBeliggenhedsadresse(@Valid QueryParams query) throws EdpWebServiceException {
        EjendomMedBeliggenhedsadresse ejendomMedBeliggenhedsadresse = service.getEjendomMedBeliggenhedsadresse(query);
        if (!ejendomMedBeliggenhedsadresse.isPopulated()) {
            throw new EdpWebServiceException(EdpWebServiceException.ExceptionType.NoContent);
        }

        return ResponseEntity.ok().body(ejendomMedBeliggenhedsadresse);
    }
}