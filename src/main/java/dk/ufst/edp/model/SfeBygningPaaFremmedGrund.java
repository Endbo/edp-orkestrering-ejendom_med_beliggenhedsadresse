package dk.ufst.edp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SfeBygningPaaFremmedGrund {
    @JsonProperty("BFEnummer")
    private int BFEnummer;

    private String status;

    @JsonProperty("ejerlejlighedListe")
    private List<SlimEjerlejlighed> ejerlejlighed;
}
