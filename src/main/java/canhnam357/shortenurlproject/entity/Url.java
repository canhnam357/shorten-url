package canhnam357.shortenurlproject.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "urls")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Url implements Persistable<Long> {
    @Id
    private Long id;
    private String shortUrl;

    @Column(name = "long_url", length = 2048)
    private String longUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

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
