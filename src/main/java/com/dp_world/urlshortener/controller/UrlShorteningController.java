package com.dp_world.urlshortener.controller;

import com.dp_world.urlshortener.model.Url;
import com.dp_world.urlshortener.model.UrlDto;
import com.dp_world.urlshortener.model.UrlErrorResponseDto;
import com.dp_world.urlshortener.model.UrlResponseDto;
import com.dp_world.urlshortener.service.UrlService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class UrlShorteningController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateShortLink(@RequestBody UrlDto urlDto) {
        Url urlToReturn = urlService.generateShortLink(urlDto);

        if(urlToReturn!=null) {
            UrlResponseDto urlResponseDto = new UrlResponseDto();
            urlResponseDto.setOriginalUrl(urlToReturn.getOriginalUrl());
            urlResponseDto.setExpirationDate(urlToReturn.getExpirationDate());
            urlResponseDto.setShortLink(urlToReturn.getShortLink());
            return new ResponseEntity<UrlResponseDto>(urlResponseDto, HttpStatus.OK);
        }

        UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
        urlErrorResponseDto.setError("There was an error processing your Request. Please try again");
        urlErrorResponseDto.setStatus("404");
        return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);

    }

    @GetMapping("/{shortLink}")
    public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink, HttpServletResponse response) throws IOException {

        if(StringUtils.isEmpty(shortLink)) {
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Invalid Url");
            urlErrorResponseDto.setStatus("404");

            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
        }

        Url urlToReturn = urlService.getEncodedUrl(shortLink);

        if(urlToReturn==null) {
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Url Does Not Exist Or It Might Have Expired");
            urlErrorResponseDto.setStatus("404");

            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);

        }

        if(urlToReturn.getExpirationDate().isBefore(LocalDateTime.now())) {
            urlService.deleteUrl(urlToReturn);
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Url Expired. Please try generating again");
            urlErrorResponseDto.setStatus("200");

            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);

        }

        response.sendRedirect(urlToReturn.getOriginalUrl());
        return null;
    }
}
