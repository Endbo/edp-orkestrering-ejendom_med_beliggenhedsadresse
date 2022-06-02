package dk.ufst.edp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * This class is named Slim, becasue its a slim version of Ejerlejlighed
 */

@Getter
@Setter
public class SlimEjerlejlighed {
    @JsonProperty("BFEnummer")
    private int BFEnummer;

    private String status;
}
