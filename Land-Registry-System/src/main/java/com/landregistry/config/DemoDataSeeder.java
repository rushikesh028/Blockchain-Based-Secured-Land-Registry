package com.landregistry.config;

import com.landregistry.dto.LandTransactionDTO;
import com.landregistry.entity.LandParcel;
import com.landregistry.repository.LandParcelRepository;
import com.landregistry.service.LandRegistryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true")
public class DemoDataSeeder implements CommandLineRunner {

    private final LandRegistryService landRegistryService;
    private final LandParcelRepository parcelRepository;

    @Override
    public void run(String... args) {
        if (parcelRepository.count() > 0) {
            log.info(">>> Demo land data already present. Skipping seeding.");
            return;
        }

        LandParcel andheri = register(
                "MH-MUM-ANH-001",
                "OWNER-001",
                "Rajesh Kumar",
                "Mumbai",
                "Andheri",
                "Maharashtra",
                "1200.00",
                "5000000.00",
                "RESIDENTIAL",
                "Ground floor flat, north-facing residential parcel",
                19.1136,
                72.8697,
                "First registration for residential property"
        );

        register(
                "MH-PUN-HNJ-014",
                "OWNER-002",
                "Priya Singh",
                "Pune",
                "Hinjewadi",
                "Maharashtra",
                "2400.00",
                "9200000.00",
                "COMMERCIAL",
                "Commercial office plot near IT park access road",
                18.5913,
                73.7389,
                "Commercial registration for business use"
        );

        LandParcel nashik = register(
                "MH-NSK-SNR-022",
                "OWNER-003",
                "Amit Patil",
                "Nashik",
                "Sinnar",
                "Maharashtra",
                "43560.00",
                "1800000.00",
                "AGRICULTURAL",
                "Agricultural land parcel with irrigation access",
                19.8457,
                74.0006,
                "Agricultural land registration"
        );

        LandParcel nagpur = register(
                "MH-NGP-BTI-031",
                "OWNER-004",
                "Sneha Deshmukh",
                "Nagpur",
                "Butibori",
                "Maharashtra",
                "10000.00",
                "7500000.00",
                "INDUSTRIAL",
                "Industrial plot in logistics and manufacturing zone",
                20.9276,
                79.0038,
                "Industrial parcel registration"
        );

        LandParcel thane = register(
                "MH-THN-KLW-009",
                "OWNER-005",
                "Meera Shah",
                "Thane",
                "Kalwa",
                "Maharashtra",
                "860.00",
                "4200000.00",
                "RESIDENTIAL",
                "Residential apartment parcel near railway station",
                19.1955,
                72.9995,
                "Residential registration for demo dashboard"
        );

        transfer(andheri, "OWNER-001", "Rajesh Kumar", "OWNER-006", "Neha Sharma", "5500000.00");
        mutate(thane, "Meera R. Shah", "Residential apartment parcel with corrected owner name");
        encumber(nagpur, "Mortgage recorded against industrial plot - State Bank of India");
        dispute(nashik, "Boundary dispute filed by adjacent land holder");

        log.info(">>> Seeded demo land registry data for dashboard and blockchain screens.");
    }

    private LandParcel register(String surveyNumber,
                                String ownerId,
                                String ownerName,
                                String district,
                                String village,
                                String state,
                                String areaSqFt,
                                String marketValue,
                                String landType,
                                String description,
                                Double latitude,
                                Double longitude,
                                String notes) {
        LandTransactionDTO dto = LandTransactionDTO.builder()
                .parcelId("PENDING")
                .surveyNumber(surveyNumber)
                .transactionType("REGISTRATION")
                .toOwnerId(ownerId)
                .toOwnerName(ownerName)
                .district(district)
                .village(village)
                .state(state)
                .areaSqFt(new BigDecimal(areaSqFt))
                .marketValue(new BigDecimal(marketValue))
                .landType(landType)
                .description(description)
                .latitude(latitude)
                .longitude(longitude)
                .initiatedBy("registrar")
                .notes(notes)
                .build();

        return landRegistryService.registerLand(dto);
    }

    private void transfer(LandParcel parcel,
                          String fromOwnerId,
                          String fromOwnerName,
                          String toOwnerId,
                          String toOwnerName,
                          String marketValue) {
        LandTransactionDTO dto = LandTransactionDTO.builder()
                .fromOwnerId(fromOwnerId)
                .fromOwnerName(fromOwnerName)
                .toOwnerId(toOwnerId)
                .toOwnerName(toOwnerName)
                .marketValue(new BigDecimal(marketValue))
                .witnessId("WIT-DEMO-001")
                .initiatedBy("registrar")
                .notes("Sale deed executed and ownership transferred")
                .build();

        landRegistryService.transferOwnership(parcel.getParcelId(), dto);
    }

    private void mutate(LandParcel parcel, String correctedOwnerName, String description) {
        LandTransactionDTO dto = LandTransactionDTO.builder()
                .toOwnerName(correctedOwnerName)
                .description(description)
                .initiatedBy("registrar")
                .notes("Name correction approved through mutation entry")
                .build();

        landRegistryService.mutateLandRecord(parcel.getParcelId(), dto);
    }

    private void encumber(LandParcel parcel, String notes) {
        LandTransactionDTO dto = LandTransactionDTO.builder()
                .initiatedBy("registrar")
                .notes(notes)
                .build();

        landRegistryService.encumberLand(parcel.getParcelId(), dto);
    }

    private void dispute(LandParcel parcel, String notes) {
        LandTransactionDTO dto = LandTransactionDTO.builder()
                .initiatedBy("officer")
                .notes(notes)
                .build();

        landRegistryService.fileDispute(parcel.getParcelId(), dto);
    }
}
