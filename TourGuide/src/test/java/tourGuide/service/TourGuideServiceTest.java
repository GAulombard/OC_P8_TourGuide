package tourGuide.service;


import gpsUtil.GpsUtil;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import tourGuide.model.User;
import org.junit.jupiter.api.Test;


@SpringBootTest
//@RunWith(SpringRunner.class)
//@RunWith(MockitoJUnitRunner.class)
//@ExtendWith(MockitoExtension.class)
//@RunWith(JUnitPlatform.class)
public class TourGuideServiceTest {

    private Logger logger = LoggerFactory.getLogger(TourGuideServiceTest.class);

/*    @BeforeAll
    public static void setUp(){
        InternalTestHelper.setInternalUserNumber(0); //set the list of user to 0
        Locale.setDefault(new Locale("en", "US")); //Set default locale to avoid problems with comma between "," and "."
    }

    @BeforeEach
    void setUpBeforeEach() throws UserAlreadyExistsException {
        logger.debug("before each");
        tourGuideService = new TourGuideService(gpsUtil,rewardsService);
        tourGuideService.tracker.stopTracking();
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        tourGuideService.addUser(user);
    }

    @AfterEach
    void setUpAfterEach(){
        logger.debug("tear down");
        //tourGuideService.getInternalUserMap().remove(user.getUserName());
    }

    @Test
    public void getUser() throws UserNotFoundException {

        Assertions.assertEquals("jon", tourGuideService.getUser(user.getUserName()).getUserName());
        Assertions.assertEquals("000", tourGuideService.getUser(user.getUserName()).getPhoneNumber());
        Assertions.assertEquals("jon@tourGuide.com", tourGuideService.getUser(user.getUserName()).getEmailAddress());

    }*/
}
