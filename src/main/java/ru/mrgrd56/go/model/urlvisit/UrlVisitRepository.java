package ru.mrgrd56.go.model.urlvisit;

import org.springframework.data.repository.CrudRepository;
import ru.mrgrd56.go.model.shortenedurl.ShortenedUrl;

public interface UrlVisitRepository extends CrudRepository<UrlVisit, String> {
    void deleteAllByShortenedUrl(ShortenedUrl shortenedUrl);
}
