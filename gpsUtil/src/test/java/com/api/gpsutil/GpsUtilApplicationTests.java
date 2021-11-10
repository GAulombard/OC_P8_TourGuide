package com.api.gpsutil;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class GpsUtilApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setUp() {
        Locale.setDefault(new Locale("en", "US"));
    }

    @Test
    public void getUserLocationTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getUserLocation").param("userId",
                        ("bea60f6d-aa4b-496d-87da-4db04b99f2e5"))).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.userId", is("bea60f6d-aa4b-496d-87da-4db04b99f2e5")))
                .andExpect(jsonPath("$.location").isMap()).andExpect(jsonPath("$.location.longitude").isNotEmpty())
                .andExpect(jsonPath("$.location.latitude").isNotEmpty())
                .andExpect(jsonPath("$.timeVisited").isNotEmpty());
    }

    @Test
    public void getUserLocationTest_WhenWrongParameters() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getUserLocation").param("uuid", ""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void getUserLocationTest_WhenEmptyParamaters() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getUserLocation").param("userId", ""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void getUserLocationTest_WhenErrorParametersString() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getUserLocation").param("userId", "jon"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void getAttractionsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getAttractions")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$[0].longitude").isNotEmpty())
                .andExpect(jsonPath("$[0].latitude").isNotEmpty())
                .andExpect(jsonPath("$[0].attractionName").isNotEmpty()).andExpect(jsonPath("$[0].city").isNotEmpty())
                .andExpect(jsonPath("$[0].state").isNotEmpty()).andExpect(jsonPath("$[0].attractionId").isNotEmpty());
    }

    @Test
    public void getAttractionsTest_WhenBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/getAttractions"))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());

    }

}
