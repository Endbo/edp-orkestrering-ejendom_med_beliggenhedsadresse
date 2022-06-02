package dk.ufst.edp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Husnummer {
    private String adgangsadressebetegnelse;
    private String husnummertekst;

    private Kommuneinddeling kommuneinddeling;
    private NavngivenVej navngivenVej;
    private Postnummer postnummer;
}
