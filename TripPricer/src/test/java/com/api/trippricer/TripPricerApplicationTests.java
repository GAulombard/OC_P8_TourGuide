package com.api.trippricer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Locale;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class TripPricerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        Locale.setDefault(new Locale("en", "US"));
    }

    @Test
    public void getPriceTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getPrice")
                        .param("tripPricerApiKey", "test-server-api-key")
                        .param("userId", "084291e4-c10b-479f-8c38-c50abaca9f89")
                        .param("numberOfAdults", "1")
                        .param("numberChildren", "8")
                        .param("tripDuration", "4")
                        .param("cumulatativeRewardPoints", "300"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].price").isNotEmpty())
                .andExpect(jsonPath("$[0].tripId").isNotEmpty());
    }

    @Test
    public void getPriceTest_WhenWrongParameters() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getPrice")
                        .param("tripPricerApiKey", "test-server-api-key")
                        .param("userId", "")
                        .param("numberOfAdults", "1")
                        .param("numberChildren", "8")
                        .param("tripDuration", "4")
                        .param("cumulatativeRewardPoints", "300"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void getPriceTestAsPost_sentBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/getPrice")
                        .param("tripPricerApiKey", "test-server-api-key")
                        .param("userId", "")
                        .param("numberOfAdults", "1")
                        .param("numberChildren", "8")
                        .param("tripDuration", "4")
                        .param("cumulatativeRewardPoints", "300"))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());

    }

    @Test
    public void getPriceTestBadParameter_sentBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getPrice")
                        .param("tripPricerApiKey", "1")
                        .param("userId", "084291e4-c10b-479f-8c38-c50abaca9f89")
                        .param("numberOfAdults", "1.8")
                        .param("numberChildren", "8.2")
                        .param("tripDuration", "4")
                        .param("cumulatativeRewardPoints", "300"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

}
