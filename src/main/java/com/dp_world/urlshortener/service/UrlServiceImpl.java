package com.dp_world.urlshortener.service;

import com.dp_world.urlshortener.model.Url;
import com.dp_world.urlshortener.model.UrlDto;
import com.dp_world.urlshortener.repository.UrlRepository;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class UrlServiceImpl implements UrlService{

    @Autowired
    private UrlRepository urlRepository;


    @Override
    public Url generateShortLink(UrlDto urlDto) {

        if(StringUtils.isNotEmpty(urlDto.getUrl())) {
            String encodedUrl = encodeUrl(urlDto.getUrl());
            Url urlToPersist = new Url();
            urlToPersist.setCreationDate(LocalDateTime.now());
            urlToPersist.setOriginalUrl(urlDto.getUrl());
            urlToPersist.setShortLink(encodedUrl);
            urlToPersist.setExpirationDate(getExpirationDate(urlDto.getExpirationDate(),urlToPersist.getCreationDate()));
            Url urlToReturn = persistShortLink(urlToPersist);
            return urlToReturn;
        }


        return null;
    }

    private LocalDateTime getExpirationDate(String expirationDate, LocalDateTime creationDate) {

        if(StringUtils.isBlank(expirationDate)) {
            return creationDate.plusSeconds(60);
        }

        LocalDateTime expirationDateToReturn = LocalDateTime.parse(expirationDate);
        return expirationDateToReturn;
    }

    private String encodeUrl(String url) {
        String encodedUrl = "";
        LocalDateTime time = LocalDateTime.now();

        encodedUrl = Hashing.murmur3_32()
                .hashString(url.concat(time.toString()),
                        StandardCharsets.UTF_8)
                .toString();

        return encodedUrl;
    }

    @Override
    public Url persistShortLink(Url url) {

        Url urlToReturn = urlRepository.save(url);

        return url;
    }

    @Override
    public Url getEncodedUrl(String url) {
        Url urlToReturn = urlRepository.findByShortLink(url);
        return urlToReturn;
    }

    @Override
    public void deleteUrl(Url url) {
        urlRepository.delete(url);
    }
}
