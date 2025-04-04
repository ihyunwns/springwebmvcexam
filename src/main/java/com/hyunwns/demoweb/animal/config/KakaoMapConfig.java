package com.hyunwns.demoweb.animal.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 https://apis.map.kakao.com/web/guide/
*/

public class KakaoMapConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = KakaoMapConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties 파일을 찾을 수 없습니다.");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("설정 파일 로드 중 오류 발생", ex);
        }
    }

    public static String getKakaoJsApiKey() {
        return properties.getProperty("kakao.js.api.key");
    }

    public static String getKakaoRestApi() {
        return properties.getProperty("kakao.rest.api.key");
    }

    public static String getKakaoApiAddress() {
        return properties.getProperty("kakao.api.address");
    }

    public static String getKakaoApiKeyword() {
        return properties.getProperty("kakao.api.keyword");
    }
}
