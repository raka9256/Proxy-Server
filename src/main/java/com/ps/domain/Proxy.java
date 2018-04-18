package com.ps.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.http.HttpHeaders;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Proxy.
 */
@Document(collection = "proxy")
public class Proxy implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @NotNull
    @Field("api")
    private String api;

    @Field("content_type")
    private String contentType;

    @Field("header")
    private String header;

    @Field("date_modified")
    private LocalDate dateModified;

    // jhipster-needle-entity-add-field - Jhipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApi() {
        return api;
    }

    public Proxy api(String api) {
        this.api = api;
        return this;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getContentType() {
        return contentType;
    }

    public Proxy contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getHeader() {
        return header;
    }

    public Proxy header(String header) {
        this.header = header;
        return this;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public LocalDate getDateModified() {
        return dateModified;
    }

    public Proxy dateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
        return this;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }
    // jhipster-needle-entity-add-getters-setters - Jhipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Proxy proxy = (Proxy) o;
        if (proxy.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), proxy.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Proxy{" +
            "id=" + getId() +
            ", api='" + getApi() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", header='" + getHeader() + "'" +
            ", dateModified='" + getDateModified() + "'" +
            "}";
    }
}
