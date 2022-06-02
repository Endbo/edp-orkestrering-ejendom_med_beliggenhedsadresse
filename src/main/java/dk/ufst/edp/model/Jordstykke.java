package dk.ufst.edp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Jordstykke {
    @JsonProperty("id_lokalId")
    private String id_lokalId;

    @JsonProperty("id_namespace")
    private String id_namespace;

    private String delnummer;

    private int ejerlavskode;

    private String ejerlavsnavn;

    private String matrikelnummer;

    private String kommuneLokalId;

    private String regionLokalId;

    private String sognLokalId;

    private String registreringFra;

    private String registreringTil;

    private String virkningFra;

    private String virkningTil;

    private String status;

    private int registreretAreal;

    private boolean brugsretsareal;

    private String vandarealinkludering;

    private String vejareal;

    private Fredskov fredskov;

    private Jordrente jordrente;

    private Klitfredning klitfredning;

    private Majoratskov majoratskov;

    private List<Stormfald> stormfald;

    private Strandbeskyttelse strandbeskyttelse;
}
