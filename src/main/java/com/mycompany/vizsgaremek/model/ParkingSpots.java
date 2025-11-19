/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author hokal
 */
@Entity
@Table(name = "parking_spots")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ParkingSpots.findAll", query = "SELECT p FROM ParkingSpots p"),
    @NamedQuery(name = "ParkingSpots.findById", query = "SELECT p FROM ParkingSpots p WHERE p.id = :id"),
    @NamedQuery(name = "ParkingSpots.findByName", query = "SELECT p FROM ParkingSpots p WHERE p.name = :name"),
    @NamedQuery(name = "ParkingSpots.findByAddress", query = "SELECT p FROM ParkingSpots p WHERE p.address = :address"),
    @NamedQuery(name = "ParkingSpots.findByLatitude", query = "SELECT p FROM ParkingSpots p WHERE p.latitude = :latitude"),
    @NamedQuery(name = "ParkingSpots.findByLongitude", query = "SELECT p FROM ParkingSpots p WHERE p.longitude = :longitude"),
    @NamedQuery(name = "ParkingSpots.findByHourlyRate", query = "SELECT p FROM ParkingSpots p WHERE p.hourlyRate = :hourlyRate"),
    @NamedQuery(name = "ParkingSpots.findByDailyRate", query = "SELECT p FROM ParkingSpots p WHERE p.dailyRate = :dailyRate"),
    @NamedQuery(name = "ParkingSpots.findByMonthlyRate", query = "SELECT p FROM ParkingSpots p WHERE p.monthlyRate = :monthlyRate"),
    @NamedQuery(name = "ParkingSpots.findByDistanceFromCenter", query = "SELECT p FROM ParkingSpots p WHERE p.distanceFromCenter = :distanceFromCenter"),
    @NamedQuery(name = "ParkingSpots.findByCapacity", query = "SELECT p FROM ParkingSpots p WHERE p.capacity = :capacity"),
    @NamedQuery(name = "ParkingSpots.findByOccupiedSpaces", query = "SELECT p FROM ParkingSpots p WHERE p.occupiedSpaces = :occupiedSpaces"),
    @NamedQuery(name = "ParkingSpots.findByRating", query = "SELECT p FROM ParkingSpots p WHERE p.rating = :rating"),
    @NamedQuery(name = "ParkingSpots.findByRatingCount", query = "SELECT p FROM ParkingSpots p WHERE p.ratingCount = :ratingCount"),
    @NamedQuery(name = "ParkingSpots.findByIsActive", query = "SELECT p FROM ParkingSpots p WHERE p.isActive = :isActive"),
    @NamedQuery(name = "ParkingSpots.findByCreatedAt", query = "SELECT p FROM ParkingSpots p WHERE p.createdAt = :createdAt"),
    @NamedQuery(name = "ParkingSpots.findByUpdatedAt", query = "SELECT p FROM ParkingSpots p WHERE p.updatedAt = :updatedAt")})
public class ParkingSpots implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 200)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "address")
    private String address;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "latitude")
    private BigDecimal latitude;
    @Column(name = "longitude")
    private BigDecimal longitude;
    @Column(name = "hourly_rate")
    private Integer hourlyRate;
    @Column(name = "daily_rate")
    private Integer dailyRate;
    @Column(name = "monthly_rate")
    private Integer monthlyRate;
    @Column(name = "distance_from_center")
    private Integer distanceFromCenter;
    @Lob
    @Size(max = 65535)
    @Column(name = "types")
    private String types;
    @JoinTable(name = "parking_spot_features", joinColumns = {
    @JoinColumn(name = "parking_spot_id", referencedColumnName = "id")}, inverseJoinColumns = {
    @JoinColumn(name = "feature_id", referencedColumnName = "id")})
    @ManyToMany
    private Collection<Features> featuresCollection;

    @Column(name = "capacity")
    private Integer capacity;
    @Column(name = "occupied_spaces")
    private Integer occupiedSpaces;
    @Lob
    @Size(max = 65535)
    @Column(name = "main_image_url")
    private String mainImageUrl;
    @Lob
    @Size(max = 65535)
    @Column(name = "image_gallery")
    private String imageGallery;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Column(name = "rating")
    private BigDecimal rating;
    @Column(name = "rating_count")
    private Integer ratingCount;
    @Column(name = "is_active")
    private Boolean isActive;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @JoinTable(name = "parking_spot_features", joinColumns = {
        @JoinColumn(name = "parking_spot_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "feature_id", referencedColumnName = "id")})
    @ManyToMany
    private Collection<Features> featuresCollection;
    @OneToMany(mappedBy = "parkingSpotId")
    private Collection<Favorites> favoritesCollection;
    @OneToMany(mappedBy = "parkingSpotId")
    private Collection<ParkingSpaces> parkingSpacesCollection;
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    @ManyToOne
    private Cities cityId;

    public ParkingSpots() {
    }

    public ParkingSpots(Integer id) {
        this.id = id;
    }

    public ParkingSpots(Integer id, Date createdAt, Date updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Integer getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Integer hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Integer getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(Integer dailyRate) {
        this.dailyRate = dailyRate;
    }

    public Integer getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(Integer monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public Integer getDistanceFromCenter() {
        return distanceFromCenter;
    }

    public void setDistanceFromCenter(Integer distanceFromCenter) {
        this.distanceFromCenter = distanceFromCenter;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getOccupiedSpaces() {
        return occupiedSpaces;
    }

    public void setOccupiedSpaces(Integer occupiedSpaces) {
        this.occupiedSpaces = occupiedSpaces;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public String getImageGallery() {
        return imageGallery;
    }

    public void setImageGallery(String imageGallery) {
        this.imageGallery = imageGallery;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @XmlTransient
    public Collection<Features> getFeaturesCollection() {
        return featuresCollection;
    }

    public void setFeaturesCollection(Collection<Features> featuresCollection) {
        this.featuresCollection = featuresCollection;
    }

    @XmlTransient
    public Collection<Favorites> getFavoritesCollection() {
        return favoritesCollection;
    }

    public void setFavoritesCollection(Collection<Favorites> favoritesCollection) {
        this.favoritesCollection = favoritesCollection;
    }

    @XmlTransient
    public Collection<ParkingSpaces> getParkingSpacesCollection() {
        return parkingSpacesCollection;
    }

    public void setParkingSpacesCollection(Collection<ParkingSpaces> parkingSpacesCollection) {
        this.parkingSpacesCollection = parkingSpacesCollection;
    }

    public Cities getCityId() {
        return cityId;
    }

    public void setCityId(Cities cityId) {
        this.cityId = cityId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ParkingSpots)) {
            return false;
        }
        ParkingSpots other = (ParkingSpots) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.ParkingSpots[ id=" + id + " ]";
    }
    
}
