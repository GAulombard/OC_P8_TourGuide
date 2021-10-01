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
import org.springframework.boot.test.mock.mockito.MockBean;
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

    //private User user;

    @BeforeAll
    public static void setUpBeforeAll(){
        InternalTestHelper.setInternalUserNumber(0); //set the list of user to 0
        Locale.setDefault(new Locale("en", "US")); //Set default locale to avoid problems with comma between "," and "."
    }

    @BeforeEach
    void setUpBeforeEach(){
        tourGuideService = new TourGuideService(gpsUtil,rewardsService);
        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        tourGuideService.addUser(user);
    }

    @AfterEach
    void setUpAfterEach(){
        tourGuideService.getInternalUserMap().remove(user.getUserName());
    }

    @Test
    public void test_getUser() throws UserNotFoundException {

        assertTrue(tourGuideService.getUser(user.getUserName()).getUserName().equals("jon"));
        assertTrue(tourGuideService.getUser(user.getUserName()).getPhoneNumber().equals("000"));
        assertTrue(tourGuideService.getUser(user.getUserName()).getEmailAddress().equals("jon@tourGuide.com"));

    }
}
