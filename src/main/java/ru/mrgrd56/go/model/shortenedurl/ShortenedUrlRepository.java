package ru.mrgrd56.go.model.shortenedurl;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ShortenedUrlRepository extends CrudRepository<ShortenedUrl, String> {
    Optional<ShortenedUrl> findByUrl(String url);

    @Query(value = "SELECT generate_url_id()", nativeQuery = true)
    String generateShortUrl();
}
