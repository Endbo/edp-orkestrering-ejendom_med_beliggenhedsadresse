package dk.ufst.edp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Adresse {
    private String adressebetegnelse;
    private String dørbetegnelse;
    private String etagebetegnelse;

    private Husnummer husnummer;
}
