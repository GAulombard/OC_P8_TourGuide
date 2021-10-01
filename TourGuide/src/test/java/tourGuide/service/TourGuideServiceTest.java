package tourGuide.service;


import gpsUtil.GpsUtil;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tourGuide.exception.UserNotFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;

import java.util.Locale;
import java.util.UUID;


import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class TourGuideServiceTest {

    @Mock
    private GpsUtil gpsUtil;

    @Mock
    private RewardsService rewardsService;

    @InjectMocks
    private TourGuideService tourGuideService;
    private User user;

    @BeforeAll
    public static void setUpBeforeAll(){
        InternalTestHelper.setInternalUserNumber(0); //set the list of user to 0
        Locale.setDefault(new Locale("en", "US")); //Set default locale to avoid problems with comma between "," and "."
    }

    @BeforeEach
    public void setUpBeforeEach(){
        tourGuideService = new TourGuideService(gpsUtil,rewardsService);

    }

    @AfterEach
    public void setUpAfterEach(){
        tourGuideService.getInternalUserMap().remove(user.getUserName());
    }

    @Test
    public void test_getUser() throws UserNotFoundException {
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        tourGuideService.addUser(user);
        assertTrue(tourGuideService.getUser(user.getUserName()).getUserName().equals("jon"));

    }
}
