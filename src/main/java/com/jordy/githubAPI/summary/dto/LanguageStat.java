package com.jordy.githubAPI.summary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LanguageStat {
    private String language;
    private double percentage;
}