package dk.ufst.edp.handlers;

import dk.ufst.edp.constants.JsonPointerPaths;
import dk.ufst.edp.model.*;
import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Consumer;

@Component
public class BestemtFastEjendomQueryHandler {

    public Consumer<InputStream> populateBestemtFastEjendomFromJsonResponse(BestemtFastEjendom bestemtFastEjendom) {
        return stream -> {
            try (JsonReader reader = Json.createReader(stream);
                 Jsonb jsonBuilder = JsonbBuilder.create()) {

                JsonValue bfeJson = reader.readObject().getValue("");

                extractSamletFastEjendom(bestemtFastEjendom, jsonBuilder, bfeJson);
                extractEjerlejlighed(bestemtFastEjendom, jsonBuilder, bfeJson);
                extractBygningPaaFremmedGrund(bestemtFastEjendom, jsonBuilder, bfeJson);

            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        };
    }

    private void extractSamletFastEjendom(BestemtFastEjendom bestemtFastEjendom, Jsonb jsonBuilder, JsonValue bfeJson) {
        //get SamletFastEjendom
        JsonValue samletFastEjendomJson = bfeJson.asJsonObject()
                .getValue(JsonPointerPaths.SAMLETFASTEJENDOM_PATH + JsonPointerPaths.FEATURE_PATH);

        boolean hasSamletFastEjendom = !samletFastEjendomJson
                .asJsonArray()
                .isEmpty();

        if (hasSamletFastEjendom) {
            bestemtFastEjendom.setPopulated(true);

            ArrayList<Jordstykke> jordstykkeJsonList = new ArrayList<>();

            samletFastEjendomJson = samletFastEjendomJson.asJsonArray()
                    .getValue(JsonPointerPaths.FIRST_PROPERTIES_PATH);

            SamletFastEjendom samletFastEjendom = jsonBuilder.fromJson(samletFastEjendomJson.toString(), SamletFastEjendom.class);

            //map it to BestemtFastEjendom Model
            bestemtFastEjendom.setSamletFastEjendom(samletFastEjendom);

            //get List of Jordstykke
            JsonValue jordstykkeJsonArray = samletFastEjendomJson.asJsonObject()
                    .getValue(JsonPointerPaths.JORDSTYKKE_PATH);


            for (JsonValue jordstykkeReader : jordstykkeJsonArray.asJsonArray()) {
                JsonValue jordstykkeJson = jordstykkeReader.asJsonObject()
                        .getValue(JsonPointerPaths.PROPERTIES_PATH);

                Jordstykke jordstykke = jsonBuilder.fromJson(jordstykkeJson.toString(), Jordstykke.class);
                jordstykkeJsonList.add(jordstykke);
            }

            //map it to BestemtFastEjendom Model
            bestemtFastEjendom.getSamletFastEjendom().setJordstykke(jordstykkeJsonList);
        }
    }

    private void extractEjerlejlighed(BestemtFastEjendom bestemtFastEjendom, Jsonb jsonBuilder, JsonValue bfeJson) {
        //get Ejerlejlighed
        JsonValue ejerlejlighedJson = bfeJson.asJsonObject()
                .getValue(JsonPointerPaths.EJERLEJLIGHED_PATH + JsonPointerPaths.FEATURE_PATH);

        boolean hasEjerlejlighed = !ejerlejlighedJson
                .asJsonArray()
                .isEmpty();

        if (hasEjerlejlighed) {
            bestemtFastEjendom.setPopulated(true);

            ejerlejlighedJson = ejerlejlighedJson.asJsonArray()
                    .getValue(JsonPointerPaths.FIRST_PROPERTIES_PATH);

            Ejerlejlighed ejerlejlighed = jsonBuilder.fromJson(ejerlejlighedJson.toString(), Ejerlejlighed.class);

            //map it to BestemtFastEjendom Model
            bestemtFastEjendom.setEjerlejlighed(ejerlejlighed);
        }
    }

    private void extractBygningPaaFremmedGrund(BestemtFastEjendom bestemtFastEjendom, Jsonb jsonBuilder, JsonValue bfeJson) {
        //get BygningPaaFremmedGrund
        JsonValue bygningPaaFremmedGrundJson = bfeJson.asJsonObject()
                .getValue(JsonPointerPaths.BYGNINGPAAFREMMEDGRUND_PATH + JsonPointerPaths.FEATURE_PATH);

        boolean hasBygningPaaFremmedGrund = !bygningPaaFremmedGrundJson
                .asJsonArray()
                .isEmpty();

        if (hasBygningPaaFremmedGrund) {
            bestemtFastEjendom.setPopulated(true);

            bygningPaaFremmedGrundJson = bygningPaaFremmedGrundJson.asJsonArray().getValue(JsonPointerPaths.FIRST_PROPERTIES_PATH);
            BygningPaaFremmedGrund bygningPaaFremmedGrundd = jsonBuilder.fromJson(bygningPaaFremmedGrundJson.toString(), BygningPaaFremmedGrund.class);

            //map it to BestemtFastEjendom Model
            bestemtFastEjendom.setBygningPaaFremmedGrund(bygningPaaFremmedGrundd);
        }
    }
}
