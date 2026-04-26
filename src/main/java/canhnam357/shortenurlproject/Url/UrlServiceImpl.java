package canhnam357.shortenurlproject.Url;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService{

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
                throw new TooManyRequest("Too many requests for the same timestamp");
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
            else {
                code += (c - '0' + 52);
            }
        }
        return code;
    }


    @Override
    public ResponseEntity<?> shortenUrl(String longUrl) {
        try {
            long currentSecond = Instant.now().getEpochSecond();
            long id = getNextId(currentSecond);
            long code = calculateCode(lastEpochSecond, id);
            String shortUrl = getCodeString(code);
            Url url = Url.builder()
                    .id(code)
                    .longUrl(longUrl)
                    .shortUrl(shortUrl)
                    .build();

            urlRepository.save(url);
            return ResponseEntity.ok(url);
        } catch (TooManyRequest e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<?> expandUrl(String shortUrl) {
        long code = getCode(shortUrl);
        Optional<Url> url = urlRepository.findById(code);
        return url.map(value -> ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .header(HttpHeaders.LOCATION, value.getLongUrl())
                .build()).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
