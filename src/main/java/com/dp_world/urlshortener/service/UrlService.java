package com.dp_world.urlshortener.service;

import com.dp_world.urlshortener.model.Url;
import com.dp_world.urlshortener.model.UrlDto;
import org.springframework.stereotype.Service;

@Service
public interface UrlService {

    public Url generateShortLink(UrlDto urlDto);

    public Url persistShortLink(Url url);

    public Url getEncodedUrl(String url);

    public void deleteUrl(Url url);

}
