package dk.ufst.edp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BestemtFastEjendom {
    @JsonIgnore
    private boolean isPopulated = false;

    @JsonProperty("SamletFastEjendom")
    private SamletFastEjendom samletFastEjendom;

    @JsonProperty("Ejerlejlighed")
    private Ejerlejlighed ejerlejlighed;

    @JsonProperty("BygningPaaFremmedGrund")
    private BygningPaaFremmedGrund bygningPaaFremmedGrund;
}
