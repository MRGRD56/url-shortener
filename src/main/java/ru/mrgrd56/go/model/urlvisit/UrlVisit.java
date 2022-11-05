package ru.mrgrd56.go.model.urlvisit;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.mrgrd56.go.model.BaseEntity;
import ru.mrgrd56.go.model.shortenedurl.ShortenedUrl;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "url_visit")
@EntityListeners(AuditingEntityListener.class)
public class UrlVisit extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type = "pg-uuid")
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "shortened_url")
    private ShortenedUrl shortenedUrl;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    public UrlVisit() {
    }

    public UrlVisit(ShortenedUrl shortenedUrl, String ipAddress, String userAgent) {
        this.shortenedUrl = shortenedUrl;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ShortenedUrl getShortenedUrl() {
        return shortenedUrl;
    }

    public void setShortenedUrl(ShortenedUrl shortenedUrl) {
        this.shortenedUrl = shortenedUrl;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
