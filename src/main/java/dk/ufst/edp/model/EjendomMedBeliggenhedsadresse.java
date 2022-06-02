package dk.ufst.edp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EjendomMedBeliggenhedsadresse {
    @JsonProperty("BestemtFastEjendom")
    private BestemtFastEjendom bestemtFastEjendom;

    @JsonProperty("Beliggenhedsadresse")
    private Beliggenhedsadresse beliggenhedsadresse;

    @JsonIgnore
    public boolean isPopulated() {
        return beliggenhedsadresse.isPopulated() || bestemtFastEjendom.isPopulated();
    }
}
