package canhnam357.shortenurlproject.Url;

import org.springframework.http.ResponseEntity;

public interface UrlService {
    ResponseEntity<?> shortenUrl(String longUrl);
    ResponseEntity<?> expandUrl(String shortUrl);
    ResponseEntity<?> resolve(String shortUrl);
}
