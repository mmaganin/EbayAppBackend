package com.maganini.portfolio;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Education {
    String name;
    String degree;
    String major;
    String startDate;
    String endDate;
    String description;
}
