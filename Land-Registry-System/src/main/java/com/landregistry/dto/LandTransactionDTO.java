package com.landregistry.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for land transactions.
 * This is the data payload that gets stored in each blockchain block.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandTransactionDTO {

    private String transactionId;

    @NotBlank(message = "Parcel ID is required")
    private String parcelId;

    @NotBlank(message = "Survey number is required")
    private String surveyNumber;

    @NotBlank(message = "Transaction type is required")
    private String transactionType;

    private String fromOwnerId;
    private String fromOwnerName;

    @NotBlank(message = "To owner ID is required")
    private String toOwnerId;

    @NotBlank(message = "To owner name is required")
    private String toOwnerName;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Village is required")
    private String village;

    @NotBlank(message = "State is required")
    private String state;

    @NotNull(message = "Area is required")
    @DecimalMin(value = "0.01", message = "Area must be greater than 0")
    private BigDecimal areaSqFt;

    @NotNull(message = "Market value is required")
    @DecimalMin(value = "0.01", message = "Market value must be greater than 0")
    private BigDecimal marketValue;

    private BigDecimal stampDuty;
    private BigDecimal registrationFee;

    @NotBlank(message = "Land type is required")
    private String landType;

    private String description;
    private Double latitude;
    private Double longitude;
    private String witnessId;
    private String notes;
    private String initiatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime transactionDate;
}
