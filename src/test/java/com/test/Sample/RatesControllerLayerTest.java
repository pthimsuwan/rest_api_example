package com.test.Sample;

import com.test.Sample.controllers.RatesController;
import com.test.Sample.services.RatesService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ContextConfiguration(classes=SampleApplication.class)
@RunWith(SpringRunner.class)
@WebMvcTest(RatesController.class)
class RatesControllerLayerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private RatesService ratesService;

	@Test
	public void testGetRatesRequest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/rates/get")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testPutRatesRequest() throws Exception {

		JSONArray rates = new JSONArray();
		JSONObject item = new JSONObject();
		item.put("days","sun");
		item.put("times","0900-2100");
		item.put("tz","America/Chicago");
		item.put("price",1500);

		rates.add(item);

		JSONObject json = new JSONObject();
		json.put("rates",rates);

		mvc.perform(MockMvcRequestBuilders.put("/rates/put")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json.toJSONString()))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testGetPriceRequest() throws Exception {

		JSONObject obj = new JSONObject();
		obj.put("price", 1750);

		Mockito.when(ratesService.getPrice("2015-07-01T07:00:00-05:00","2015-07-01T12:00:00-05:00")).thenReturn(obj);

		mvc.perform(MockMvcRequestBuilders.get("/price")
				.param("start","2015-07-01T07:00:00-05:00")
				.param("end","2015-07-01T12:00:00-05:00"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType("application/json"))
				.andExpect(MockMvcResultMatchers.content().json("{'price':1750}"));
	}
}
