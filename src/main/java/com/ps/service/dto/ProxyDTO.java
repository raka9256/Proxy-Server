package com.ps.service.dto;


import org.springframework.http.MediaType;

import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Proxy entity.
 */
public class ProxyDTO implements Serializable {

    private String id;

    @NotNull
    private String api;

    private String contentType;

    private String header;

    private LocalDate dateModified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public LocalDate getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProxyDTO proxyDTO = (ProxyDTO) o;
        if(proxyDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), proxyDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ProxyDTO{" +
            "id=" + getId() +
            ", api='" + getApi() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", header='" + getHeader() + "'" +
            ", dateModified='" + getDateModified() + "'" +
            "}";
    }
}
