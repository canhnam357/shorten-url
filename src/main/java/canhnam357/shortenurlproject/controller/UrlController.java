package canhnam357.shortenurlproject.controller;

import canhnam357.shortenurlproject.dto.UrlRequest;
import canhnam357.shortenurlproject.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @RequestMapping("/{shortUrl}")
    public ResponseEntity<?> expandUrl(@PathVariable String shortUrl) {
        return urlService.expandUrl(shortUrl);
    }

    @RequestMapping("/new")
    public ResponseEntity<?> shortenUrl(@RequestBody UrlRequest newShortenUrlRequest) {
        if (newShortenUrlRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        if (newShortenUrlRequest.longUrl() == null || newShortenUrlRequest.longUrl().isBlank() || newShortenUrlRequest.longUrl().length() > 2048) {
            return ResponseEntity.badRequest().build();
        }
        return urlService.shortenUrl(newShortenUrlRequest.longUrl());
    }
}
