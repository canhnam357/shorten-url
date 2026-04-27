package canhnam357.shortenurlproject.service.implementation;

import canhnam357.shortenurlproject.dto.GeneralResponse;
import canhnam357.shortenurlproject.dto.UrlResponse;
import canhnam357.shortenurlproject.exception.TooManyRequestException;
import canhnam357.shortenurlproject.entity.Url;
import canhnam357.shortenurlproject.repository.UrlRepository;
import canhnam357.shortenurlproject.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;

    final int BIT_LENGTH_TIMESTAMP = 36;
    final int BIT_LENGTH_ID = 14;
    final long MAX_ID = (1L << BIT_LENGTH_ID) - 1;

    private long lastEpochSecond = -1L;
    private long sequence = 0L;

    private synchronized long getNextId(long currentEpochSecond) {
        if (currentEpochSecond < lastEpochSecond) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        if (currentEpochSecond == lastEpochSecond) {
            sequence++;
            if (sequence > MAX_ID) {
                throw new TooManyRequestException("Too many requests for the same timestamp");
            }
        } else {
            sequence = 0L;
            lastEpochSecond = currentEpochSecond;
        }
        return sequence;
    }

    private long calculateCode(long epochSecond, long id) {
        return (1L << BIT_LENGTH_TIMESTAMP | epochSecond) << BIT_LENGTH_ID | id;
    }

    private String getCodeString(long code) {
        StringBuilder codeString = new StringBuilder();
        while (code > 0) {
            long k = code % 62;
            code /= 62;
            if (k < 26) {
                codeString.insert(0, (char) ('A' + k));
            }
            else if (k < 52) {
                codeString.insert(0, (char) ('a' + k - 26));
            }
            else {
                codeString.insert(0, (char) ('0' + k - 52));
            }
        }
        return codeString.toString();
    }

    private long getCode(String codeString) {
        long code = 0;
        for (int i = 0; i < codeString.length(); i++) {
            code *= 62;
            char c = codeString.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                code += (c - 'A');
            }
            else if (c >= 'a' && c <= 'z') {
                code += (c - 'a' + 26);
            }
            else if (c >= '0' && c <= '9') {
                code += (c - '0' + 52);
            }
            else {
                code = -1;
                break;
            }
            if (code > (1L << (BIT_LENGTH_TIMESTAMP + BIT_LENGTH_ID + 1))) {
                code = -1;
                break;
            }
        }
        return code;
    }


    @Override
    public ResponseEntity<?> shortenUrl(String longUrl) {

        // 1. Get the current time (Epoch Second)
        long currentSecond = Instant.now().getEpochSecond();

        // 2. Generate id for this time
        long id = getNextId(currentSecond);

        // 3. Calculate the code
        long code = calculateCode(lastEpochSecond, id);

        // 4. Convert the code to string
        String shortUrl = getCodeString(code);

        // 5. Create a new URL object
        Url url = Url.builder()
                .id(code)
                .longUrl(longUrl)
                .shortUrl(shortUrl)
                .build();

        // 6. Save the URL to the database
        urlRepository.save(url);

        // 7. Return the shortened URL
        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Shortened URL", 200, new UrlResponse(url.getShortUrl(), url.getLongUrl()));
        return ResponseEntity.ok(res);
    }

    @Override
    public ResponseEntity<?> expandUrl(String shortUrl) {

        // 1. Convert the short URL to a code
        long code = getCode(shortUrl);

        // 2. Find the URL in the database
        Optional<Url> url = urlRepository.findById(code);

        // 3. If the URL exists, redirect to the long URL otherwise return 404
        return url.map(value -> ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .header(HttpHeaders.LOCATION, value.getLongUrl())
                .build()).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
