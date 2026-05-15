package com.landregistry.repository;

import com.landregistry.entity.LandParcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LandParcelRepository extends JpaRepository<LandParcel, String> {

    Optional<LandParcel> findBySurveyNumber(String surveyNumber);

    List<LandParcel> findByCurrentOwnerId(String ownerId);

    List<LandParcel> findByDistrictAndVillage(String district, String village);

    List<LandParcel> findByStatus(LandParcel.LandStatus status);

    List<LandParcel> findByDistrict(String district);

    boolean existsBySurveyNumber(String surveyNumber);

    @Query("SELECT lp FROM LandParcel lp WHERE lp.district = :district AND lp.state = :state")
    List<LandParcel> findByDistrictAndState(@Param("district") String district,
                                            @Param("state") String state);

    @Query("SELECT lp FROM LandParcel lp WHERE lp.currentOwnerId = :ownerId AND lp.status = :status")
    List<LandParcel> findByOwnerIdAndStatus(@Param("ownerId") String ownerId,
                                            @Param("status") LandParcel.LandStatus status);

    @Query("SELECT COUNT(lp) FROM LandParcel lp WHERE lp.district = :district")
    long countByDistrict(@Param("district") String district);
}
