package com.api.gpsutil;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

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
    void contextLoads() {
    }

}
