package com.test.Sample;

import com.test.Sample.services.RatesService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@Import(RatesService.class)
public class RatesServiceLayerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RatesService ratesService ;

    private JSONObject rates;
    private JSONObject price;
    private JSONArray ratesArr;

    @BeforeEach
    public void setUp() {
        ratesService = new RatesService("testRates.json");

        ratesArr = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("days","wed");
        item.put("times","0600-1800");
        item.put("tz","America/Chicago");
        item.put("price",1750);

        ratesArr.add(item);

        rates = new JSONObject();
        rates.put("rates",ratesArr);

        price = new JSONObject();
        price.put("price", 1750);
    }

    @Test
    public void testGetRates(){
        ratesService.putRates(rates);
        Assert.assertEquals(ratesArr.toJSONString(),ratesService.getRates().toJSONString());
    }
    
    @Test
    public void testPutRates(){
        Assert.assertEquals(1L,ratesService.putRates(rates));
    }


    @Test
    public void testGetPrice(){
        ratesService.putRates(rates);
        Assert.assertEquals(price.toJSONString(),ratesService.getPrice("2015-07-01T07:00:00-05:00","2015-07-01T12:00:00-05:00").toJSONString());
    }
}
