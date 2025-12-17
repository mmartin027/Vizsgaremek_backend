package com.vizsgaremek.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ParkingSpotFeatureId implements Serializable {
    private static final long serialVersionUID = -6517210430238451568L;
    @Column(name = "parking_spot_id", nullable = false)
    private Integer parkingSpotId;

    @Column(name = "feature_id", nullable = false)
    private Integer featureId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ParkingSpotFeatureId entity = (ParkingSpotFeatureId) o;
        return Objects.equals(this.parkingSpotId, entity.parkingSpotId) &&
                Objects.equals(this.featureId, entity.featureId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parkingSpotId, featureId);
    }

}