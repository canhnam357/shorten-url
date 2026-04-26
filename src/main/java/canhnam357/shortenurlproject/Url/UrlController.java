package canhnam357.shortenurlproject.Url;

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
    public ResponseEntity<?> shortenUrl(@RequestBody NewShortenUrlRequest newShortenUrlRequest) {
        if (newShortenUrlRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        return urlService.shortenUrl(newShortenUrlRequest.longUrl());
    }
}
