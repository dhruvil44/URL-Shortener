package com.dp_world.urlshortener.repository;

import com.dp_world.urlshortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url,Long> {

    public Url findByShortLink(String shortLink);

}
