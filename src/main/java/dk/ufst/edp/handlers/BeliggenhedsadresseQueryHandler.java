package dk.ufst.edp.handlers;

import dk.ufst.edp.constants.JsonPointerPaths;
import dk.ufst.edp.model.Beliggenhedsadresse;
import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.function.Consumer;

@Component
public class BeliggenhedsadresseQueryHandler {
    public Consumer<InputStream> populateBeliggenhedsadresseFromJsonResponse(Beliggenhedsadresse beliggenhedsadresse) {
        return stream -> {
            try (JsonReader reader = Json.createReader(stream); Jsonb jsonBuilder = JsonbBuilder.create()) {
                //get Beliggenhedsadresse
                JsonValue beliggenhedsadresseFeaturesJson = reader.readObject().getValue(JsonPointerPaths.FEATURE_PATH);

                boolean hasBeliggenhedsadresse = !beliggenhedsadresseFeaturesJson.asJsonArray().isEmpty();

                if (hasBeliggenhedsadresse) {
                    beliggenhedsadresse.setPopulated(true);

                    JsonValue beliggenhedsadresseJson = beliggenhedsadresseFeaturesJson.asJsonArray()
                            .getValue(JsonPointerPaths.FIRST_PROPERTIES_PATH);

                    //map it to BestemtFastEjendom Model
                    beliggenhedsadresse.setBeliggenhedsadresse(jsonBuilder.fromJson(beliggenhedsadresseJson.toString(), Beliggenhedsadresse.class));
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        };
    }
}
