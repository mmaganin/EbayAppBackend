package com.maganini.portfolio;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndustryExperience {
    private String jobTitle;
    private String company;
    private boolean isFullTime;
    private String startDate;
    private String endDate;
}
