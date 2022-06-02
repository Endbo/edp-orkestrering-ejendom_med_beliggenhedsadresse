package dk.ufst.edp.query;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class QueryParams {
    @NotBlank(message = "BFEnr: er påkrævet")
    @Pattern(regexp = "^(0|[1-9][0-9]*)$", message = "BFEnr: Skal kun indeholde tal.")
    private String BFEnr;

    @Pattern(regexp = "Gældende|Historisk|Foreløbig|Ikke gennemført",
            flags = {Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.UNICODE_CASE},
            message = "Status: Kan kun være 'Gældende', 'Historisk', 'Foreløbig' eller 'Ikke gennemført ")
    private String status = "Gældende";

    private Instant virkningstid = Instant.now();

    public String truncatedVirkningstidToString() {
        return virkningstid.truncatedTo(ChronoUnit.MICROS).toString();
    }
}