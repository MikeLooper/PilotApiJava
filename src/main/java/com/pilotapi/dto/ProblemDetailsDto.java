package com.pilotapi.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProblemDetailsDto {

    private String type;
    private String title;
    private Integer status;
    private String detail;
    private String instance;
    private final Map<String, Object> extensions = new LinkedHashMap<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    /**
     * Additional members serialized alongside the standard fields (RFC 9457 extension members).
     */
    @JsonAnyGetter
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @JsonAnySetter
    public void addExtension(String name, Object value) {
        extensions.put(name, value);
    }
}
