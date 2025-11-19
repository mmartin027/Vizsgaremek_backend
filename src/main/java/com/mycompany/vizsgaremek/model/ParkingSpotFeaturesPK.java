/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author szilv
 */
@Embeddable
public class ParkingSpotFeaturesPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "parking_spot_id")
    private int parkingSpotId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "feature_id")
    private int featureId;

    public ParkingSpotFeaturesPK() {
    }

    public ParkingSpotFeaturesPK(int parkingSpotId, int featureId) {
        this.parkingSpotId = parkingSpotId;
        this.featureId = featureId;
    }

    public int getParkingSpotId() {
        return parkingSpotId;
    }

    public void setParkingSpotId(int parkingSpotId) {
        this.parkingSpotId = parkingSpotId;
    }

    public int getFeatureId() {
        return featureId;
    }

    public void setFeatureId(int featureId) {
        this.featureId = featureId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) parkingSpotId;
        hash += (int) featureId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ParkingSpotFeaturesPK)) {
            return false;
        }
        ParkingSpotFeaturesPK other = (ParkingSpotFeaturesPK) object;
        if (this.parkingSpotId != other.parkingSpotId) {
            return false;
        }
        if (this.featureId != other.featureId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.ParkingSpotFeaturesPK[ parkingSpotId=" + parkingSpotId + ", featureId=" + featureId + " ]";
    }
    
}
