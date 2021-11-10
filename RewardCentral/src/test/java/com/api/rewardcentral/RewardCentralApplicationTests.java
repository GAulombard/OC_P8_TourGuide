package com.api.rewardcentral;

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

@SpringBootTest
@AutoConfigureMockMvc
class RewardCentralApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        Locale.setDefault(new Locale("en", "US"));
    }

    @Test
    public void getAttractionRewardPointsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getAttractionRewardPoints")
                        .param("attractionId", ("bea60f6d-aa4b-496d-87da-4db04b99f2e5"))
                        .param("userId", ("084291e4-c10b-479f-8c38-c50abaca9f89")))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getAttractionRewardPointsTestWrongUUID_sentBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getAttractionRewardPoints").param("attractionId", (""))
                        .param("userId", (""))).andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void getAttractionRewardPointsTestAsPost_sentBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/getAttractionRewardPoints")
                        .param("attractionId", ("bea60f6d-aa4b-496d-87da-4db04b99f2e5"))
                        .param("userId", ("084291e4-c10b-479f-8c38-c50abaca9f89")))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());

    }

    @Test
    public void getAttractionRewardPointsTest_WhenBadParameter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getAttractionRewardPoints")
                        .param("attractionId", ("bea60f6d-aa4b-496d-87da-4db04b99f2e5")).param("userId", ("jon")))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

}
