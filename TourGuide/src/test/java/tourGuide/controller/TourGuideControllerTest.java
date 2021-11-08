package tourGuide.controller;

import gpsUtil.GpsUtil;
import com.tourguide.commons.model.Attraction;
import com.tourguide.commons.model.VisitedLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import tourGuide.exception.UserNotFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.feign.GpsUtilFeign;

import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
public class TourGuideControllerTest {

    private Logger logger = LoggerFactory.getLogger(TourGuideControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    GpsUtilFeign gpsUtilFeign;

    @Autowired
    private TourGuideService tourGuideService;

    @Autowired
    private RewardsService rewardsService;

    @BeforeAll
    public static void setUp() {

        InternalTestHelper.setInternalUserNumber(1);

    }

    @BeforeEach
    void init() {


    }

    @AfterEach
    void tearDown(){

    }

    @Test
    public void index() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    public void indexBis() throws Exception {
        mockMvc.perform(get("")).andExpect(status().isOk());
    }

    @Test
    public void getUser() throws Exception {

        String username = "internalUser0";
        User user = tourGuideService.getUser(username);

        mockMvc.perform(get("/getUser").param("userName",username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId",is(user.getUserId().toString())));
    }

    @Test
    public void getPreferences() throws Exception {

        String username = "internalUser0";

        mockMvc.perform(get("/getPreferences").param("userName",username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attractionProximity",is(4200)))
                .andExpect(jsonPath("$.ticketQuantity",is(5)))
                .andExpect(jsonPath("$.numberOfAdults",is(2)))
                .andExpect(jsonPath("$.numberOfChildren",is(1)));
    }

    @Test
    public void getAllUsers() throws Exception {

        String username = "internalUser0";
        User user = tourGuideService.getUser(username);

        mockMvc.perform(get("/getAllUsers"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].userId",is(user.getUserId().toString())))
                .andExpect(jsonPath("$[0].userName",is(username)));
    }

    @Test
    public void getLocation() throws Exception {

        String username = "internalUser0";
        User user = tourGuideService.getUser(username);

        mockMvc.perform(get("/getLocation").param("userName",username))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.userId",is(user.getUserId().toString())))
                .andExpect(jsonPath("$.location").isNotEmpty())
                .andExpect(jsonPath("$.location").isMap());
    }

    @Test
    public void trackAllUsersLocation() throws Exception {

        String username = "internalUser0";
        User user = tourGuideService.getUser(username);

        mockMvc.perform(get("/trackUsers"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$."+username).isArray())
                .andExpect(jsonPath("$."+username,hasSize(5)));
               // .andExpect(jsonPath("$."+username+".userId",is(user.getUserId().toString())));
    }

    @Test
    public void getNearbyAttractions() throws Exception {
        String username = "internalUser0";
        User user = tourGuideService.getUser(username);

        mockMvc.perform(get("/getNearbyAttractions").param("userName",username))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$",hasSize(5)));

    }

    @Test
    public void getRewards() throws Exception {
        String username = "internalUser0";
        User user = tourGuideService.getUser(username);

        Attraction attraction = gpsUtilFeign.getAttractions().get(0);
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), attraction, new Date());
        user.addToVisitedLocations(visitedLocation);
        tourGuideService.trackUserLocation(user);

        mockMvc.perform(get("/getRewards").param("userName",username))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].rewardPoints").isNotEmpty())
                .andExpect(jsonPath("$[0].rewardPoints").isNumber());

    }

    @Test
    public void getAllCurrentLocations() throws Exception {
        String username = "internalUser0";
        User user = tourGuideService.getUser(username);

        mockMvc.perform(get("/getAllCurrentLocations"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$",hasKey(user.getUserId().toString())));
    }

    @Test
    public void getTripDeals() throws Exception {
        String username = "internalUser0";
        User user = tourGuideService.getUser(username);

        mockMvc.perform(get("/getTripDeals").param("userName",username))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$",hasSize(130)));
    }

    @Test
    public void updatePreferences() throws Exception {
        String username = "internalUser0";
        User user = tourGuideService.getUser(username);
        String updatePreferences = "{\"attractionProximity\": \"4200\",\"lowerPricePoint\": \"0\",\"highPricePoint\": \"1255.55\",\"tripDuration\": \"3\",\"ticketQuantity\": \"5\",\"numberOfAdults\": \"2\",\"numberOfChildren\": \"1\"}";

        mockMvc.perform(put("/updatePreferences").param("userName",username)
                        .content(updatePreferences)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void updatePreferences_shouldReturnBadRequest() throws Exception {
        String username = "internalUser0";
        User user = tourGuideService.getUser(username);
        String updatePreferences = "{\"attractionProximity\": \"-4200\",\"lowerPricePoint\": \"0\",\"highPricePoint\": \"1255.55\",\"tripDuration\": \"3\",\"ticketQuantity\": \"5\",\"numberOfAdults\": \"2\",\"numberOfChildren\": \"1\"}";

        mockMvc.perform(put("/updatePreferences").param("userName",username)
                        .content(updatePreferences)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

}
