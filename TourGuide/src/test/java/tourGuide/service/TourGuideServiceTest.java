package tourGuide.service;


import gpsUtil.GpsUtil;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import rewardCentral.RewardCentral;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;

import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class TourGuideServiceTest {

    private final static Logger logger = LoggerFactory.getLogger(TourGuideServiceTest.class);
    private GpsUtil gpsUtil = new GpsUtil();
    private RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
    private TourGuideService tourGuideService;
    private User user;

    @BeforeAll
    public static void setup(){

        logger.debug("@BeforeAll");
        InternalTestHelper.setInternalUserNumber(0); //set the list of user to 0
        Locale.setDefault(new Locale("en", "US")); //Set default locale to avoid problems with comma between "," and "."
    }

    @BeforeEach
    void init() throws UserAlreadyExistsException {
        logger.debug("@BeforeEach");
        tourGuideService = new TourGuideService(gpsUtil,rewardsService);
        tourGuideService.tracker.stopTracking();

        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        tourGuideService.addUser(user);

    }

    @AfterEach
    void tearDown(){
        logger.debug("@AfterEach");
        InternalTestHelper.freeInternalUserMap();
    }

    @Test
    public void getUser() throws UserNotFoundException {

        Assertions.assertEquals("jon", tourGuideService.getUser(user.getUserName()).getUserName());
        Assertions.assertEquals("000", tourGuideService.getUser(user.getUserName()).getPhoneNumber());
        Assertions.assertEquals("jon@tourGuide.com", tourGuideService.getUser(user.getUserName()).getEmailAddress());

    }

    @Test
    public void getUser_shouldThrowUserNotFoundException() throws UserNotFoundException {

        assertThrows(UserNotFoundException.class, () -> tourGuideService.getUser("test"));

    }
}
