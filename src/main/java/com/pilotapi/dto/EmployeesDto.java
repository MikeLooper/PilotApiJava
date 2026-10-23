package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

import io.micrometer.common.lang.Nullable;

public class EmployeesDto {

    @Schema(types = {"null", "string"})
    private String address;

    @Schema(types = {"null", "string"}, format = "date-time")
    private LocalDateTime birthDate;

    @Schema(types = {"null", "string"})
    private String city;

    @Schema(types = {"null", "string"})
    private String country;

    @Schema(types = {"null", "integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN)
    @Nullable
    private Integer employeeId;

    @Schema(types = {"null", "string"})
    private String extension;

    @Schema(types = {"null", "string"})
    @NotBlank
    private String firstName;

    @Schema(types = {"null", "string"}, format = "date-time")
    private LocalDateTime hireDate;

    @Schema(types = {"null", "string"})
    private String homePhone;

    @Schema(types = {"null", "string"})
    @NotBlank
    private String lastName;

    @Schema(types = {"null", "string"})
    private String notes;

    @Schema(types = {"null", "string"}, format = "byte")
    private String photo;

    @Schema(types = {"null", "string"})
    private String photoPath;

    @Schema(types = {"null", "string"})
    private String postalCode;

    @Schema(types = {"null", "string"})
    private String region;

    @Schema(types = {"null", "integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN)
    private Integer reportsTo;

    @Schema(types = {"null", "string"})
    private String title;

    @Schema(types = {"null", "string"})
    private String titleOfCourtesy;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDateTime getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDateTime hireDate) {
        this.hireDate = hireDate;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getReportsTo() {
        return reportsTo;
    }

    public void setReportsTo(Integer reportsTo) {
        this.reportsTo = reportsTo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleOfCourtesy() {
        return titleOfCourtesy;
    }

    public void setTitleOfCourtesy(String titleOfCourtesy) {
        this.titleOfCourtesy = titleOfCourtesy;
    }
}
