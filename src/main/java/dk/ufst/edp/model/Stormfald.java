package dk.ufst.edp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stormfald {
    @JsonProperty("SNSJournalNummer")
    private String snsJournalNummer;

    private String betalingsdato;

    private String omfang;

    private String ophoersdato;

    private String tilskudstype;
}
