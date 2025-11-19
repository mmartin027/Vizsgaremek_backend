/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author szilv
 */
@Entity
@Table(name = "parking_spot_features")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ParkingSpotFeatures.findAll", query = "SELECT p FROM ParkingSpotFeatures p"),
    @NamedQuery(name = "ParkingSpotFeatures.findByParkingSpotId", query = "SELECT p FROM ParkingSpotFeatures p WHERE p.parkingSpotFeaturesPK.parkingSpotId = :parkingSpotId"),
    @NamedQuery(name = "ParkingSpotFeatures.findByFeatureId", query = "SELECT p FROM ParkingSpotFeatures p WHERE p.parkingSpotFeaturesPK.featureId = :featureId")})
public class ParkingSpotFeatures implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ParkingSpotFeaturesPK parkingSpotFeaturesPK;
    @JoinColumn(name = "feature_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Features features;
    @JoinColumn(name = "parking_spot_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ParkingSpots parkingSpots;

    public ParkingSpotFeatures() {
    }

    public ParkingSpotFeatures(ParkingSpotFeaturesPK parkingSpotFeaturesPK) {
        this.parkingSpotFeaturesPK = parkingSpotFeaturesPK;
    }

    public ParkingSpotFeatures(int parkingSpotId, int featureId) {
        this.parkingSpotFeaturesPK = new ParkingSpotFeaturesPK(parkingSpotId, featureId);
    }

    public ParkingSpotFeaturesPK getParkingSpotFeaturesPK() {
        return parkingSpotFeaturesPK;
    }

    public void setParkingSpotFeaturesPK(ParkingSpotFeaturesPK parkingSpotFeaturesPK) {
        this.parkingSpotFeaturesPK = parkingSpotFeaturesPK;
    }

    public Features getFeatures() {
        return features;
    }

    public void setFeatures(Features features) {
        this.features = features;
    }

    public ParkingSpots getParkingSpots() {
        return parkingSpots;
    }

    public void setParkingSpots(ParkingSpots parkingSpots) {
        this.parkingSpots = parkingSpots;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (parkingSpotFeaturesPK != null ? parkingSpotFeaturesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ParkingSpotFeatures)) {
            return false;
        }
        ParkingSpotFeatures other = (ParkingSpotFeatures) object;
        if ((this.parkingSpotFeaturesPK == null && other.parkingSpotFeaturesPK != null) || (this.parkingSpotFeaturesPK != null && !this.parkingSpotFeaturesPK.equals(other.parkingSpotFeaturesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.ParkingSpotFeatures[ parkingSpotFeaturesPK=" + parkingSpotFeaturesPK + " ]";
    }
    
}
