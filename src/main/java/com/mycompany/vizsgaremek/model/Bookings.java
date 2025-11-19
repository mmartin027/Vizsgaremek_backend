/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
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
@Table(name = "bookings")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bookings.findAll", query = "SELECT b FROM Bookings b"),
    @NamedQuery(name = "Bookings.findById", query = "SELECT b FROM Bookings b WHERE b.id = :id"),
    @NamedQuery(name = "Bookings.findByStartTime", query = "SELECT b FROM Bookings b WHERE b.startTime = :startTime"),
    @NamedQuery(name = "Bookings.findByEndTime", query = "SELECT b FROM Bookings b WHERE b.endTime = :endTime"),
    @NamedQuery(name = "Bookings.findByHours", query = "SELECT b FROM Bookings b WHERE b.hours = :hours"),
    @NamedQuery(name = "Bookings.findByTotalPrice", query = "SELECT b FROM Bookings b WHERE b.totalPrice = :totalPrice"),
    @NamedQuery(name = "Bookings.findByLicensePlate", query = "SELECT b FROM Bookings b WHERE b.licensePlate = :licensePlate"),
    @NamedQuery(name = "Bookings.findByCarBrand", query = "SELECT b FROM Bookings b WHERE b.carBrand = :carBrand"),
    @NamedQuery(name = "Bookings.findByCarModel", query = "SELECT b FROM Bookings b WHERE b.carModel = :carModel"),
    @NamedQuery(name = "Bookings.findByCarColor", query = "SELECT b FROM Bookings b WHERE b.carColor = :carColor"),
    @NamedQuery(name = "Bookings.findByStatus", query = "SELECT b FROM Bookings b WHERE b.status = :status"),
    @NamedQuery(name = "Bookings.findByAccessCode", query = "SELECT b FROM Bookings b WHERE b.accessCode = :accessCode"),
    @NamedQuery(name = "Bookings.findByIsExtended", query = "SELECT b FROM Bookings b WHERE b.isExtended = :isExtended"),
    @NamedQuery(name = "Bookings.findByCreatedAt", query = "SELECT b FROM Bookings b WHERE b.createdAt = :createdAt"),
    @NamedQuery(name = "Bookings.findByUpdatedAt", query = "SELECT b FROM Bookings b WHERE b.updatedAt = :updatedAt"),
    @NamedQuery(name = "Bookings.findByCancelledAt", query = "SELECT b FROM Bookings b WHERE b.cancelledAt = :cancelledAt")})
public class Bookings implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    @Column(name = "hours")
    private Integer hours;
    @Column(name = "total_price")
    private Integer totalPrice;
    @Size(max = 20)
    @Column(name = "license_plate")
    private String licensePlate;
    @Size(max = 255)
    @Column(name = "car_brand")
    private String carBrand;
    @Size(max = 255)
    @Column(name = "car_model")
    private String carModel;
    @Size(max = 50)
    @Column(name = "car_color")
    private String carColor;
    @Size(max = 9)
    @Column(name = "status")
    private String status;
    @Lob
    @Size(max = 65535)
    @Column(name = "qr_code")
    private String qrCode;
    @Size(max = 10)
    @Column(name = "access_code")
    private String accessCode;
    @Lob
    @Size(max = 65535)
    @Column(name = "note")
    private String note;
    @Lob
    @Size(max = 65535)
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    @Column(name = "is_extended")
    private Boolean isExtended;
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
    @Column(name = "cancelled_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cancelledAt;
    @OneToMany(mappedBy = "bookingId")
    private Collection<Payments> paymentsCollection;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User userId;
    @JoinColumn(name = "parking_space_id", referencedColumnName = "id")
    @ManyToOne
    private ParkingSpaces parkingSpaceId;

    public Bookings() {
    }

    public Bookings(Integer id) {
        this.id = id;
    }

    public Bookings(Integer id, Date startTime, Date endTime, Date createdAt, Date updatedAt) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public Boolean getIsExtended() {
        return isExtended;
    }

    public void setIsExtended(Boolean isExtended) {
        this.isExtended = isExtended;
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

    public Date getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Date cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    @XmlTransient
    public Collection<Payments> getPaymentsCollection() {
        return paymentsCollection;
    }

    public void setPaymentsCollection(Collection<Payments> paymentsCollection) {
        this.paymentsCollection = paymentsCollection;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public ParkingSpaces getParkingSpaceId() {
        return parkingSpaceId;
    }

    public void setParkingSpaceId(ParkingSpaces parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
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
        if (!(object instanceof Bookings)) {
            return false;
        }
        Bookings other = (Bookings) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Bookings[ id=" + id + " ]";
    }
    
}
