package tourGuide.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.controller.TourGuideController;
import tourGuide.model.User;
import tourGuide.service.TourGuideService;


public class UserUtil {

    private Logger logger = LoggerFactory.getLogger(UserUtil.class);

    @Autowired
    private TourGuideService tourGuideService;

}
