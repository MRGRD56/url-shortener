package ru.mrgrd56.go.model.shortenedurl;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.NonNull;
import ru.mrgrd56.go.model.BaseEntity;

import javax.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "shortened_url", indexes = {
        @Index(name = "shortened_url_url_idx", columnList = "url")
})
public class ShortenedUrl extends BaseEntity {
    @Id
    @Column(name = "short_url", unique = true)
    private String shortUrl;

    @Column(name = "url", columnDefinition = "varchar(10240)")
    private String url;

    @Column(name = "custom")
    private Boolean isCustom;

    public ShortenedUrl() {
    }

    public ShortenedUrl(String shortUrl, String url) {
        this.shortUrl = shortUrl;
        this.url = url;
    }

    public ShortenedUrl(String shortUrl, String url, Boolean isCustom) {
        this.shortUrl = shortUrl;
        this.url = url;
        this.isCustom = isCustom;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @NonNull
    public boolean isCustom() {
        return Boolean.TRUE.equals(isCustom);
    }

    public void setCustom(Boolean custom) {
        isCustom = custom;
    }
}
