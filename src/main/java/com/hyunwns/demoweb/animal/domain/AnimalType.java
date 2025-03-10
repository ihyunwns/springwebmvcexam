package com.hyunwns.demoweb.animal.domain;

import lombok.Getter;

@Getter
public enum AnimalType {

    DOG("강아지"),
    CAT("고양이"),
    ETC("기타 반려동물");

    private final String keyword;

    AnimalType(String keyword) {
        this.keyword = keyword;
    }

    public static AnimalType fromKeyword(String keyword) {
        for (AnimalType animalType : AnimalType.values()) {
            if (animalType.keyword.equals(keyword)) {
                return animalType;
            }
        }

        throw new IllegalArgumentException(keyword + " is not a valid animal type");
    }

}
