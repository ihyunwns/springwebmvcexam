package com.hyunwns.demoweb.animal.service;

import com.google.gson.*;
import com.hyunwns.demoweb.animal.config.KakaoMapConfig;
import com.hyunwns.demoweb.animal.domain.LocationInfo;
import com.hyunwns.demoweb.animal.exception.KakaoException;
import org.openqa.selenium.json.Json;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class KakaoMapService {

    public LocationInfo getLocationInfo(String address) throws MalformedURLException {

        LocationInfo location = searchByAddress(address);
        if (location == null) {
            List<String> str = new ArrayList<>(List.of(address.split("")));

            // TODO: 검색이 안되는 주소를 입력하는 사람이 있는 것 같음 그래서 MalformedURL 에러가 잡힘
            // 이때 에러 처리 고민 해봐야 함
            // 이때는 address_name 속성을 비워두고 매니징 사이트에서 관리 가능하도록 시스템 추가 하면 될 듯?
            while (!str.isEmpty()) {
                try {
                    String keyword = String.join("", str);

                    location = searchByKeyword(keyword);
                } catch (KakaoException e) {
                    str.remove(str.size() - 1);
                    continue;
                }
                break;
            }
        }

        return location;
    }

    private LocationInfo searchByAddress(String address) throws MalformedURLException {
        String query = UriComponentsBuilder.fromHttpUrl(KakaoMapConfig.getKakaoApiAddress()).queryParam("query", address).toUriString();
        URL url = new URL(query);

        try {
            JsonObject json = connectAPI(url);

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

    private LocationInfo searchByKeyword(String keyword) throws MalformedURLException, KakaoException {

        String query = UriComponentsBuilder.fromHttpUrl(KakaoMapConfig.getKakaoApiKeyword()).queryParam("query", keyword).toUriString();
        URL url = new URL(query);

        try {
            JsonObject json = connectAPI(url);

            if (getTotalCount(json) > 0) {
                JsonArray documents = json.getAsJsonArray("documents");

                JsonElement jsonElement = documents.get(0);
                JsonObject asJsonObject = jsonElement.getAsJsonObject();

                return new LocationInfo(asJsonObject.get("x").getAsFloat(), asJsonObject.get("y").getAsFloat(), asJsonObject.get("address_name").getAsString());
            } else {
                throw new KakaoException("Kakao Keyword Not Found");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private JsonObject connectAPI(URL url) throws IOException {
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

        return JsonParser.parseString(response.toString()).getAsJsonObject();
    }

    private int getTotalCount(JsonObject json) {
        String total = JsonParser.parseString(json.get("meta").toString()).getAsJsonObject().get("total_count").toString();

        return Integer.parseInt(total);

    }
}
