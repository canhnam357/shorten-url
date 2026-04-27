package canhnam357.shortenurlproject.service.implementation;

import canhnam357.shortenurlproject.dto.GeneralResponse;
import canhnam357.shortenurlproject.dto.UrlResponse;
import canhnam357.shortenurlproject.entity.Url;
import canhnam357.shortenurlproject.service.UrlCacheService;
import canhnam357.shortenurlproject.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {
    private final UrlCacheService urlCacheService;

    @Override
    public ResponseEntity<?> shortenUrl(String longUrl) {
        Url url = urlCacheService.saveUrl(longUrl);
        GeneralResponse<Object> res = new GeneralResponse<>(
                Instant.now(), "Shortened URL", 200,
                new UrlResponse(url.getShortUrl(), url.getLongUrl())
        );
        return ResponseEntity.ok(res);
    }

    @Override
    public ResponseEntity<?> expandUrl(String shortUrl) {
        Url url = urlCacheService.findByShortUrl(shortUrl);
        if (url == null) return ResponseEntity.notFound().build();
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .header(HttpHeaders.LOCATION, url.getLongUrl())
                .build();
    }
}
