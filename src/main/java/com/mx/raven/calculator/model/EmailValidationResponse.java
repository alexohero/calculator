package com.mx.raven.calculator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailValidationResponse {
    private String email;
    
    @JsonProperty("did_you_mean")
    private String didYouMean;
    
    private String user;
    private String domain;
    
    @JsonProperty("format_valid")
    private boolean formatValid;
    
    @JsonProperty("mx_found")
    private boolean mxFound;
    
    @JsonProperty("smtp_check")
    private boolean smtpCheck;
    
    @JsonProperty("catch_all")
    private Boolean catchAll;
    
    private boolean role;
    private boolean disposable;
    private boolean free;
    private double score;
}