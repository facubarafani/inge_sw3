package sample.actuator;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.*;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.junit.Test;

public class HelloWorldServiceTest {
	private int counter = 0;

	@Test
	public void expectedMessage() {
		HelloWorldService helloWorldService = new HelloWorldService();
		assertEquals("Expected correct message", "Spring boot says hello from a Docker container",
				helloWorldService.getHelloMessage());
	}

	@Test
	public void testMethodInvoke() {
		HelloWorldService helloWorldService = mock(HelloWorldService.class);

		// When getHelloMessage gets called:
		// First time it will return "Hola Hola"
		// Second time it will return "Hello Hello"
		when(helloWorldService.getHelloMessage()).thenReturn("Hola Hola").thenReturn("Hello Hello");

		// Verifies if the method returns "Hola Hola" the first time the method gets called
		assertEquals("Hola Hola", "Hola Hola", helloWorldService.getHelloMessage());

		// Verifies if the method returns "Hello Hello" the second time the method gets called
		assertEquals("Hello Hello", "Hello Hello", helloWorldService.getHelloMessage());
		
		// ACLARATION -> IF WE CHANGE LINE EX ORDER OF LINE 35 WITH 32 IT WILL NOT PASS TESTS.
	}
}
