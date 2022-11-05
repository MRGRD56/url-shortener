package ru.mrgrd56.go.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mrgrd56.go.exception.StatusCodeException;
import ru.mrgrd56.go.model.shortenedurl.ShortenedUrl;
import ru.mrgrd56.go.model.shortenedurl.ShortenedUrlRepository;
import ru.mrgrd56.go.model.urlvisit.UrlVisit;
import ru.mrgrd56.go.model.urlvisit.UrlVisitRepository;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class UrlService {

    private final Cache<String, ShortenedUrl> cache = Caffeine.newBuilder()
            .maximumSize(2048)
            .expireAfterWrite(3, TimeUnit.DAYS)
            .build();

    private final ShortenedUrlRepository shortenedUrlRepository;
    private final UrlVisitRepository urlVisitRepository;

    public UrlService(
            ShortenedUrlRepository shortenedUrlRepository,
            UrlVisitRepository urlVisitRepository) {
        this.shortenedUrlRepository = shortenedUrlRepository;
        this.urlVisitRepository = urlVisitRepository;
    }

    public ShortenedUrl shortenUrl(@NonNull String url, @NonNull String newShortUrl) {
        var existingById = shortenedUrlRepository.findById(newShortUrl).orElse(null);
        if (existingById != null) {
            if (Objects.equals(existingById.getUrl(), url)) {
                return existingById;
            } else {
                throw new StatusCodeException(HttpStatus.BAD_REQUEST, "This short URL already exists");
            }
        }

        var byUrl = shortenedUrlRepository.findByUrl(url);
        if (byUrl.isPresent()) {
            return byUrl.get();
        }

        var newShortenedUrl = new ShortenedUrl(newShortUrl, url);
        return shortenedUrlRepository.save(newShortenedUrl);
    }

    public ShortenedUrl shortenUrl(@NonNull String url) {
        return shortenUrl(url, shortenedUrlRepository.generateShortUrl());
    }

    @Nullable
    public ShortenedUrl getUrlByShort(String shortUrl) {
        return cache.get(shortUrl, k -> {
            return shortenedUrlRepository.findById(shortUrl).orElse(null);
        });
    }

    public void visitUrl(@NonNull ShortenedUrl url, @NonNull HttpServletRequest request) {
        var visit = new UrlVisit(url, request.getRemoteAddr(), request.getHeader(HttpHeaders.USER_AGENT));
        urlVisitRepository.save(visit);
    }

    public boolean isValidUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }

        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    @Transactional
    public boolean removeShortenedUrl(String shortUrl) {
        return shortenedUrlRepository.findById(shortUrl)
                .map(url -> {
                    urlVisitRepository.deleteAllByShortenedUrl(url);
                    shortenedUrlRepository.delete(url);
                    return true;
                })
                .orElse(false);
    }
}
