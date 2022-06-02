package dk.ufst.edp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SamletFastEjendom {

    @JsonProperty("BFEnummer")
    private int BFEnummer;

    @JsonProperty("id_lokalId")
    private String id_lokalId;

    @JsonProperty("id_namespace")
    private String id_namespace;

    private boolean hovedejendomOpdeltIEjerlejligheder;

    private String landbrugsnotering;

    private boolean arbejderbolig;

    private boolean erFaelleslod;

    private boolean udskiltVej;

    private String virkningFra;

    private String virkningTil;

    private String registreringFra;

    private String registreringTil;

    private String status;

    @JsonProperty("bygningPaaFremmedGrundListe")
    private List<SfeBygningPaaFremmedGrund> bygningPaaFremmedGrund;

    @JsonProperty("ejerlejlighedListe")
    private List<SlimEjerlejlighed> ejerlejlighed;

    @JsonProperty("jordstykkeListe")
    private List<Jordstykke> jordstykke;
}
