package dk.ufst.edp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Beliggenhedsadresse {
    @JsonProperty("Ejendomstype")
    public String Ejendomstype;
    @JsonIgnore
    private boolean isPopulated = false;
    private String id_lokalId, bestemtFastEjendomBFENr, id_namespace, virkningFra, virkningTil, registreringFra, registreringTil, status;
    private List<Adresse> adresse;

    public void setBeliggenhedsadresse(Beliggenhedsadresse beliggenhedsadresse) {
        this.id_lokalId = beliggenhedsadresse.getId_lokalId();
        this.id_namespace = beliggenhedsadresse.getId_namespace();
        this.bestemtFastEjendomBFENr = beliggenhedsadresse.getBestemtFastEjendomBFENr();
        this.Ejendomstype = beliggenhedsadresse.getEjendomstype();
        this.virkningFra = beliggenhedsadresse.getVirkningFra();
        this.virkningTil = beliggenhedsadresse.getVirkningTil();
        this.registreringFra = beliggenhedsadresse.getRegistreringFra();
        this.registreringTil = beliggenhedsadresse.getRegistreringTil();
        this.status = beliggenhedsadresse.getStatus();
        this.adresse = beliggenhedsadresse.getAdresse();
    }
}
