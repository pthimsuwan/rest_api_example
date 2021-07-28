package com.test.Sample.controllers;

import com.test.Sample.services.RatesService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin("*")
@Slf4j
@RestController
@RequestMapping(path="")
public class RatesController {

    @Autowired
    private RatesService ratesService;

    @GetMapping(path="/rates/get")
    public ResponseEntity<JSONArray> getRates(HttpServletRequest req) {
        log.info("Received /rates/get request");
        final JSONArray r = ratesService.getRates();
        return ResponseEntity.ok(r);
    }

    @PutMapping(path="/rates/put")
    public ResponseEntity<Integer> putRates(HttpServletRequest req, @RequestBody JSONObject obj) {
        log.info("Received /rates/put request");
        final long status = ratesService.putRates(obj);
        final int httpStatus = status > 0 ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value();
        return ResponseEntity.ok(httpStatus);
    }

    @GetMapping(path="/price")
    public ResponseEntity<JSONObject> getPrice(HttpServletRequest req, @RequestParam String start, @RequestParam String end) {
        log.info("Received /price query");
        final JSONObject r = ratesService.getPrice(start,end);
        return ResponseEntity.ok(r);
    }
}
