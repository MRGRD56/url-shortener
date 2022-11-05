package ru.mrgrd56.go.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import ru.mrgrd56.go.exception.StatusCodeException;
import ru.mrgrd56.go.services.UrlService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class MainController {

    @Value("${security.secret-key}")
    private String actualSecretKey;

    private final UrlService urlService;

    public MainController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping(value = "/{shortUrl}")
    public ResponseEntity<?> goToUrl(
            @PathVariable String shortUrl,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        var url = urlService.getUrlByShort(shortUrl);
        if (url == null) {
            return ResponseEntity.notFound().build();
        }

        urlService.visitUrl(url, request);
        response.sendRedirect(url.getUrl());
        return null;
    }

    @GetMapping(value = "/api/shorten")
    public ResponseEntity<?> shortenUrl(
            @RequestParam String url,
            @RequestParam(required = false) String shortUrl,
            @CookieValue(name = "auth_key", required = false) String key) {
        try {
            url = url == null ? null : url.trim();

            if (!urlService.isValidUrl(url)) {
                return ResponseEntity.badRequest().body("INVALID_URL");
            }

            assert url != null;

            if ("go.mrgrd56.ru".equals(UriComponentsBuilder.fromUriString(url).build().getHost())) {
                return ResponseEntity.ok(url);
            }

            String shortenedUrl;

            if (StringUtils.isNotBlank(shortUrl)) {
                if (!actualSecretKey.equals(key)) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }

                shortenedUrl = urlService.shortenUrl(url, shortUrl).getShortUrl();
            } else {
                shortenedUrl = urlService.shortenUrl(url).getShortUrl();
            }

            var fullShortenedUrl = ServletUriComponentsBuilder.fromHttpUrl("https://go.mrgrd56.ru/")
                    .replacePath(shortenedUrl)
                    .toUriString();

            return ResponseEntity.ok(fullShortenedUrl);
        } catch (StatusCodeException e) {
            return new ResponseEntity<>(e.getStatusText(), e.getStatusCode());
        }
    }

    @GetMapping(value = "/api/remove-url")
    public ResponseEntity<?> removeShortUrl(
            @RequestParam String url,
            @CookieValue(name = "auth_key", required = false) String key) {
        if (!actualSecretKey.equals(key)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        var isSuccess = urlService.removeShortenedUrl(url);

        if (!isSuccess) {
            return ResponseEntity.badRequest().body("URL_NOT_FOUND");
        }

        return ResponseEntity.ok("DELETED");
    }

    @GetMapping(value = "/api/authorize/{key}")
    public ResponseEntity<?> authorize(
            HttpServletResponse response,
            @PathVariable String key) {
        if (!actualSecretKey.equals(key)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        var cookie = new Cookie("auth_key", key);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        var authorizedCookie = new Cookie("authorized", "true");
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        response.addCookie(authorizedCookie);

        return ResponseEntity.ok("OK");
    }
}
