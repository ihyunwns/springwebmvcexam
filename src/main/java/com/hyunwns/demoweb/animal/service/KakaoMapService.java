package com.hyunwns.demoweb.animal.service;

import com.google.gson.*;
import com.hyunwns.demoweb.animal.config.KakaoMapConfig;
import com.hyunwns.demoweb.animal.domain.LocationInfo;
import org.openqa.selenium.json.Json;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class KakaoMapService {

    public LocationInfo getLocationInfo(String address) throws MalformedURLException {
        LocationInfo location = searchByAddress(address);
        if (location == null) {
            location = searchByKeyword(address);
        }

        return location;
    }

    private LocationInfo searchByAddress(String address) throws MalformedURLException {
        String query = UriComponentsBuilder.fromHttpUrl(KakaoMapConfig.getKakaoApiAddress()).queryParam("query", address).toUriString();
        URL url = new URL(query);

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            String authKey = "KakaoAK " + KakaoMapConfig.getKakaoRestApi();
            connection.setRequestProperty("Authorization", authKey);

            BufferedReader br;
            if( connection.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                throw new MalformedURLException(connection.getResponseMessage());
            }

            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();

            if (getTotalCount(json) > 0) {
                JsonArray documents = json.getAsJsonArray("documents");

                JsonElement jsonElement = documents.get(0);
                JsonObject asJsonObject = jsonElement.getAsJsonObject();

                return new LocationInfo(asJsonObject.get("x").getAsFloat(), asJsonObject.get("y").getAsFloat(), asJsonObject.get("address_name").getAsString());
            } else {
                return null;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private LocationInfo searchByKeyword(String keyword) throws MalformedURLException {

        return null;

    }

    private int getTotalCount(JsonObject json) {
        String total = JsonParser.parseString(json.get("meta").toString()).getAsJsonObject().get("total_count").toString();

        return Integer.parseInt(total);

    }
}
