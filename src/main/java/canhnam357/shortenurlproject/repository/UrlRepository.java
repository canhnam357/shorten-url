package canhnam357.shortenurlproject.repository;

import canhnam357.shortenurlproject.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<Url, Long> {
}
