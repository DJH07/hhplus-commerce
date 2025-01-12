package kr.hhplus.be.commerce.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.commerce.app.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WebIntegrationTest extends IntegrationTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected BalanceFacade balanceFacade;
    @Autowired
    protected CouponFacade couponFacade;
    @Autowired
    protected OrderFacade orderFacade;
    @Autowired
    protected PaymentFacade paymentFacade;
    @Autowired
    protected ProductFacade productFacade;

}
