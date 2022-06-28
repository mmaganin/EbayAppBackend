package com.maganini.portfolio;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Project {
    String name;
    String startDate;
    String endDate;
    String institutionAssociation;
    String description;
}
