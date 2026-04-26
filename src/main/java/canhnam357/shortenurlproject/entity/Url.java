package canhnam357.shortenurlproject.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "url")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Url implements Persistable<Long> {
    @Id
    private Long id;
    private String shortUrl;
    private String longUrl;

    @Transient
    private boolean isNew = true;

    @Override
    public Long getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }

    @PostLoad
    @PostPersist
    void markNotNew() { this.isNew = false; }
}
