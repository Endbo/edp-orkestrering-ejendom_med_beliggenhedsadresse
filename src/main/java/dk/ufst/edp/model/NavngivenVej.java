package dk.ufst.edp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NavngivenVej {
    private String udtaltVejnavn;
    private String vejadresseringsnavn;
    private String vejnavn;

    @JsonProperty("navngivenVejKommunedelListe")
    private List<NavngivenVejKommunedelList> navngivenVejKommunedelList;

}
