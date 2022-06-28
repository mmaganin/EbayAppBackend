package com.maganini.portfolio;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Certificate {
    String name;
    String issuer;
    String issueDate;
    String expiryDate;
}
