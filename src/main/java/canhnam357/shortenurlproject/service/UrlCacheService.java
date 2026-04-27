package canhnam357.shortenurlproject.service;

import canhnam357.shortenurlproject.entity.Url;
import canhnam357.shortenurlproject.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static canhnam357.shortenurlproject.service.utility.GenId.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlCacheService {
    private final UrlRepository urlRepository;

    @Cacheable(value = "URLs", key = "#shortUrl", unless = "#result == null")
    public Url findByShortUrl(String shortUrl) {
        long code = getCode(shortUrl);
        return urlRepository.findById(code).orElse(null);
    }

    @CachePut(value = "URLs", key = "#result.shortUrl")
    public Url saveUrl(String longUrl) {
        long currentSecond = Instant.now().getEpochSecond();
        long id = getNextId(currentSecond);
        long code = calculateCode(lastEpochSecond, id);
        String shortUrl = getCodeString(code);

        Url url = Url.builder()
                .id(code)
                .longUrl(longUrl)
                .shortUrl(shortUrl)
                .build();

        return urlRepository.save(url);
    }
}
