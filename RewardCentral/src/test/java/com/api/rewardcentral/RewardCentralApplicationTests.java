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

/**
 * The type Reward central application tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RewardCentralApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Sets up before class.
     *
     * @throws Exception the exception
     */
    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        Locale.setDefault(new Locale("en", "US"));
    }

    /**
     * Gets attraction reward points test.
     *
     * @throws Exception the exception
     */
    @Test
    public void getAttractionRewardPointsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getAttractionRewardPoints")
                        .param("attractionId", ("bea60f6d-aa4b-496d-87da-4db04b99f2e5"))
                        .param("userId", ("084291e4-c10b-479f-8c38-c50abaca9f89")))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Gets attraction reward points test wrong uuid sent bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void getAttractionRewardPointsTestWrongUUID_sentBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getAttractionRewardPoints").param("attractionId", (""))
                        .param("userId", (""))).andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    /**
     * Gets attraction reward points test as post sent bad request.
     *
     * @throws Exception the exception
     */
    @Test
    public void getAttractionRewardPointsTestAsPost_sentBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/getAttractionRewardPoints")
                        .param("attractionId", ("bea60f6d-aa4b-496d-87da-4db04b99f2e5"))
                        .param("userId", ("084291e4-c10b-479f-8c38-c50abaca9f89")))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());

    }

    /**
     * Gets attraction reward points test when bad parameter.
     *
     * @throws Exception the exception
     */
    @Test
    public void getAttractionRewardPointsTest_WhenBadParameter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getAttractionRewardPoints")
                        .param("attractionId", ("bea60f6d-aa4b-496d-87da-4db04b99f2e5")).param("userId", ("jon")))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

}
