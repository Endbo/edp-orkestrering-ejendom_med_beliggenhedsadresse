package dk.ufst.edp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ejerlejlighed {
    @JsonProperty("BFEnummer")
    private int BFEnummer;
    private String id_lokalId;
    private String id_namespace;
    private String samletFastEjendomBFEnummer;
    private String bygningPaaFremmedGrundBFEnummer;
    private String ejerlejlighedsnummer;
    private int fordelingstalNaevner;
    private int fordelingstalTaeller;
    private String samletAreal;
    private String virkningFra;
    private String virkningTil;
    private String registreringFra;
    private String registreringTil;
    private String status;
}
