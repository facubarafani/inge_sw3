package sample.actuator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


public class SampleControllerTest extends AbstractTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testRootMessage() throws Exception {
        String uri = "/";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept( MediaType.APPLICATION_JSON_VALUE)).andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        assertEquals("Expected correct message","{\"message\":\"Spring boot says hello from a Docker container\"}",content);
    }
    
    @Test
    public void testRootPost() throws Exception {
        String uri = "/";
        String message = "This is a test message";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
        		.param("value", "This is a test message")
                .accept( MediaType.APPLICATION_JSON_VALUE)).andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        int status = mvcResult.getResponse().getStatus();

        assertEquals(200, status);
        assertTrue("Expected correct message",content.contains(message));
    }
}