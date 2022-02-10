package dev.isnow.fox.util.vpn;

import dev.isnow.fox.util.vpn.json.JSONException;
import dev.isnow.fox.util.vpn.json.JSONObject;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class VPNResponse {
    private String asn, ip, countryName, countryCode, city, timeZone, method, isp, failureReason = "N/A";
    private boolean proxy, cached;
    private final boolean success;
    private double latitude, longitude;
    private long lastAccess;
    private long queriesLeft;

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("ip", ip);
        json.put("countryName", countryName);
        json.put("countryCode", countryCode);
        json.put("city", city);
        json.put("method", method);
        json.put("isp", isp);
        json.put("proxy", proxy);
        json.put("success", success);
        json.put("timeZone", timeZone);
        json.put("success", true);
        json.put("queriesLeft", queriesLeft);
        json.put("cached", cached);

        return json;
    }

    public static VPNResponse fromJson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        if(jsonObject.getBoolean("success")) {
            return new VPNResponse(jsonObject.getString("asn"), jsonObject.getString("ip"),
                    jsonObject.getString("countryName"), jsonObject.getString("countryCode"),
                    jsonObject.getString("city"), jsonObject.getString("timeZone"),
                    jsonObject.has("method") ? jsonObject.getString("method") : "N/A",
                    jsonObject.getString("isp"), "N/A", jsonObject.getBoolean("proxy"),
                    jsonObject.getBoolean("cached"), jsonObject.getBoolean("success"),
                    jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"),
                    jsonObject.getLong("lastAccess"), jsonObject.getInt("queriesLeft"));
        } else {
            VPNResponse response = new VPNResponse(false);

            response.failureReason = jsonObject.getString("failureReason");

            return response;
        }
    }

    public static VPNResponse fromJson(JSONObject jsonObject) throws JSONException {
        if(jsonObject.getBoolean("success")) {
            return new VPNResponse(jsonObject.getString("asn"), jsonObject.getString("ip"),
                    jsonObject.getString("countryName"), jsonObject.getString("countryCode"),
                    jsonObject.getString("city"), jsonObject.getString("timeZone"),
                    jsonObject.has("method") ? jsonObject.getString("method") : "N/A",
                    jsonObject.getString("isp"), "N/A", jsonObject.getBoolean("proxy"),
                    jsonObject.getBoolean("cached"), jsonObject.getBoolean("success"),
                    jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"),
                    jsonObject.getLong("lastAccess"), jsonObject.getInt("queriesLeft"));
        } else {
            VPNResponse response = new VPNResponse(false);

            response.failureReason = jsonObject.getString("failureReason");

            return response;
        }
    }
}
