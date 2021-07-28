package com.test.Sample.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.Sample.models.Rate;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class RatesService {

    private String rateFile;

    private List<Rate> listRates;

    public RatesService(@Value("${persistence.rateFile:}") String rateFile){
        this.rateFile = rateFile;
        storeCurrentRates(); //store current rates in memory on start up
    }

    public JSONArray getRates(){
        try {
            log.info("Getting Rates");
            JSONArray rates = rateFileReader();
            return rates;
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return null;
    }

    public long putRates(JSONObject obj){
        FileWriter file = null;

        log.info("Putting new rates: \n" + obj);
        try {
            file = new FileWriter(rateFile);
            String test = obj.toJSONString();
            log.info("Overwriting old rates wit new Rates");
            file.write(test); //submitted JSON overwrites the stored rates per instructions
            file.flush();
            file.close();

            storeCurrentRates(); //update in memory rates

            return 1;
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return -1l;
    }

    public JSONObject getPrice(String startDate, String endDate){
        try {
            log.info("Getting Price");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;

            log.info("Converting Query DateTimes to ISO DATE format");
            OffsetDateTime queryStart = OffsetDateTime.parse(startDate, timeFormatter);
            OffsetDateTime queryEnd = OffsetDateTime.parse(endDate, timeFormatter);

            JSONObject result = new JSONObject();
            result.put("price", "Unavailable"); //default return

            log.info("QueryStart day: " + queryStart.getDayOfWeek());
            log.info("QueryEnd day: " + queryEnd.getDayOfWeek());

            if(!queryStart.getDayOfWeek().equals(queryEnd.getDayOfWeek())) {
                log.info("Returning unavailable because input spans more than one day");
                return result;
            }

            ZoneOffset queryStartTimeZone = queryStart.toOffsetTime().getOffset();
            ZoneOffset queryEndTimeZone = queryEnd.toOffsetTime().getOffset();

            log.info("Query Start TimeZone: " + queryStartTimeZone);
            log.info("Query End TimeZone: " + queryEndTimeZone);

            if(!queryStartTimeZone.equals(queryEndTimeZone)){
                log.info("Returning unavailable because input has mismatched time zones");
                return result;
            }

            for(Rate rate : listRates){

                log.info("check for day from query vs day in rate. start day == end day if we got passed the earlier check");
                boolean checkDays = Arrays.stream(rate.getDays()
                        .split(","))
                        .anyMatch(queryStart.getDayOfWeek().name().substring(0,3).toLowerCase()::contains);

                LocalTime startRateTime = LocalTime.parse(rate.getTimes().split("-")[0].substring(0,2)+":"+rate.getTimes().split("-")[0].substring(2,4)); //had to change military time string to local time string, i.e. 0900 to 09:00, before converting string to localtime
                LocalTime endRateTime = LocalTime.parse(rate.getTimes().split("-")[1].substring(0,2)+":"+rate.getTimes().split("-")[1].substring(2,4));

                LocalTime queryStartTime = queryStart.toLocalTime();
                LocalTime queryEndTime = queryEnd.toLocalTime();

                boolean queryTimeWithinRateTime = false;

                log.info("QueryStartTime: " + queryStartTime);
                log.info("RatesStartTime: " + startRateTime);

                log.info("QueryEndTime: " + queryEndTime);
                log.info("RatesEndTime: " + endRateTime);

                log.info("checking if query times are within times of rate");
                if(
                    (startRateTime.equals(queryStartTime) || startRateTime.isBefore(queryStartTime)) //check if query times are within times of rate
                    && queryStartTime.isBefore(endRateTime)
                    && queryEndTime.isAfter(startRateTime)
                    && (queryEndTime.equals(endRateTime) || queryEndTime.isBefore(endRateTime))
                ){
                    log.info("Query times are within times of rate");
                    queryTimeWithinRateTime = true;
                }
                else{
                    log.info("Query times not within times of rate");
                }

                Instant instant = Instant.now();
                ZoneId z = ZoneId.of(rate.getTz().replace(" ","_"));
                ZoneOffset rateTimeZone = instant.atZone(z).getOffset(); //convert rate's timezone to zone id for comparison

                log.info("RatesStartTime: " + queryEndTimeZone);

                boolean timeZoneMatch = rateTimeZone.equals(queryStartTimeZone);

                if(!timeZoneMatch)
                    log.info("TimeZone mismatch");

                if( checkDays && queryTimeWithinRateTime && timeZoneMatch){ //if all the above checks are true, we can return the price
                    log.info("Price Available: "+rate.getPrice());
                    result.clear();
                    result.put("price",rate.getPrice());
                    return result;
                }
            }

            return result;
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return null;
    }

    //helper functions
    private void storeCurrentRates(){
        try{
            JSONArray rates = rateFileReader();
            ObjectMapper mapper = new ObjectMapper();
            String jsonArray = mapper.writeValueAsString(rates);
            listRates = mapper.readValue(jsonArray, new TypeReference<List<Rate>>(){});
            log.info("stored current rates in memory");
        }
        catch(Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private JSONArray rateFileReader() { //read persisted rate file
        FileReader file = null;
        try{
            file = new FileReader(rateFile);
            log.info("Reading Rate File: "+rateFile);
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(file);
            log.info("Parsing rate file");
            JSONObject jsonObject = (JSONObject)obj;
            JSONArray rates = (JSONArray)jsonObject.get("rates");
            log.info("current rates: \n" + rates);
            return rates;
        }
        catch(Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        finally {
            try {
                file.close();
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
        }

        return null;
    }
}
