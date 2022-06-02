package dk.ufst.edp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BygningPaaFremmedGrund {
    @JsonProperty("BFEnummer")
    private int BFEnummer;

    private String id_lokalId;

    private String id_namespace;

    private String samletFastEjendomBFEnummer;

    private boolean opdeltIEjerlejligheder;

    private String virkningFra;

    private String virkningTil;

    private String registreringFra;

    private String registreringTil;

    private String status;

    private boolean paaHavet;

    @JsonProperty("ejerlejlighedListe")
    private List<SlimEjerlejlighed> ejerlejlighed;
}
