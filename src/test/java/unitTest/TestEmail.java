package unitTest;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.mail.EmailException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TestEmail {

	// for addBcc
	private static final String[] TEST_EMAILS = { "ab@bc.com", "a.c@c.org",
			"ajsdkajskldajflskjdslkf@jsdflajfkdlsasljfdk.com.bd" };
	private static final String[] NAMES = { "Name A", "Name hsofhei3939", "Name hfso3939.r.." };
	private final String hostName = "host";

	private final Date testDate = new Date(30);
	// concrete email for testing
	private static EmailConcrete email;

	@Before
	public void setUpBeforeClass() throws Exception {
		email = new EmailConcrete();
	}

	@After
	public void tearDownAfterClass() throws Exception {
		email = null;
	}

//	@Test
//	public void test() {
//		fail("Not yet implemented");
//	}
	@Test
	public void testAddBcc() throws Exception {
		email.addBcc(TEST_EMAILS);
		assertEquals(3, email.getBccAddresses().size());
	}

//	Email  addCc(String email)
	@Test
	public void testAddCc() throws EmailException {

		// Add CC
		email.addCc(TEST_EMAILS[1]); // Adding the second email address from TEST_EMAILS

		// Asserting the size of CC list
		assertEquals(1, email.getCcAddresses().size());
	}

//	void 	addHeader(String name, String value)
	@Test
	public void testAddHeader_ValidValues() throws EmailException {
		// set up
		String name = "this is header", value = "this is header's value";

		email.addHeader(name, value);
		Map<String, String> header = email.getHeader();

		// assert
		assertEquals(value, header.get(name));
		assertTrue(header.containsKey(name));
	}

	@Test
	public void testAddHeader_Null() throws EmailException {
		String name = null, value = "null header value";

		// Adding a header with null name should throw IllegalArgumentException
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			email.addHeader(name, value);
		});
		assertTrue(exception.getMessage().contains("value can not be null or empty"));
	}

//	Email  addReplyTo(String email, String name)
	@Test
	public void testAddReplyTo() throws EmailException {

		// set up
		email.addReplyTo(TEST_EMAILS[0], NAMES[0]);

		List<InternetAddress> replyToAddresses = email.getReplyToAddresses();

		// Assert the size of the reply-to list
		assertEquals(1, replyToAddresses.size());

		// Assert that the reply-to list contains the email address and name

		assertTrue(replyToAddresses.get(0).getPersonal().equals(NAMES[0]));
		assertEquals(TEST_EMAILS[0], replyToAddresses.get(0).getAddress());

	}

	@Test(expected = RuntimeException.class)
	public void buildMimeMessageNullCheck() throws EmailException {

		try {
			email.setHostName(hostName);
			email.setSmtpPort(50);
			email.setFrom(TEST_EMAILS[2]);
			email.addTo(TEST_EMAILS[0]);
			email.setSubject("test mail");
			email.setCharset("ISO-8859-1");
			email.setContent("content", "text");
			email.buildMimeMessage();
			MimeMessage myMessage = email.getMimeMessage();
			assertNotNull(email.getMimeMessage());
			email.buildMimeMessage();
		} catch (RuntimeException e) {
			String message = "The MimeMessage is already built.";
			assertEquals(message, e.getMessage());
			throw e;
		}

	}

	@Test(expected = EmailException.class)
	public void buildMimeMessageNullFromAddress() throws EmailException {

		// Set up email object without specifying from address
		try {
			email.setHostName("example.com");
			email.addTo("recipient@example.com");
			email.setSubject("Test Subject");
			email.setContent("Test Content", "text/plain");
			email.buildMimeMessage();
		} catch (Exception e) {
			String message = "From address required";
			assertEquals(message, e.getMessage());
		}

	}

	@Test(expected = EmailException.class)
	public void buildMimeMessageNullReceiver() throws EmailException {

		// Set up email object without specifying from address
		try {
			email.setHostName(hostName);
			email.setSmtpPort(50);
			email.setFrom(TEST_EMAILS[2]);
//			email.addTo(TEST_EMAILS[0]);
			email.setSubject("test mail");
			email.setCharset("ISO-8859-1");
			email.setContent("content", "text");
			email.buildMimeMessage();
//			assertNotNull(email.getMimeMessage());

		} catch (Exception e) {
			String message = "At least one receiver address required";
			assertEquals(message, e.getMessage());
		}

	}

	@Test
	public void buildMimeMessageHeaderFolding() throws Exception {
		// Set up email object with a long header value
		email.setHostName("example.com");
		email.setFrom("sender@example.com");
		email.addTo("recipient@example.com");
		email.setSubject("Test Subject");
		email.setContent("Test Content", "text/plain");
		email.addHeader("X-Long-Header",
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vehicula hendrerit ultricies.");

		// Invoke buildMimeMessage
		email.buildMimeMessage();

		// Assert header value is folded
		String[] headerValues = email.getMimeMessage().getHeader("X-Long-Header");
		assertNotNull(headerValues);
		assertEquals(1, headerValues.length);
		assertTrue(headerValues[0].startsWith("Lorem ipsum dolor sit amet,"));
		assertTrue(headerValues[0].endsWith("ultricies."));
	}

//	String  getHostName()
	@Test
	public void getHostName() throws EmailException {
		email.setHostName(hostName);
		assertEquals(hostName, email.getHostName());
	}

	@Test
	public void getHostName_NullValue() throws EmailException {
		Properties property = new Properties();
		Session session = Session.getInstance(property);

		email.setMailSession(session);
		assertNull(email.getHostName());
	}

//	Session getMailSession()
	@Test
	public void testGetMailSession() throws EmailException {
		Properties mockProperties = new Properties();

		mockProperties.setProperty("protocol", "smtp");

		Session mock = Session.getInstance(mockProperties);
		email.setMailSession(mock);

		Session session = email.getMailSession();

		assertEquals(mock, session);
	}

	@Test
	public void testGetMailSession_NullSession_SetOtherProperties() throws EmailException {
		email.setHostName("example.com");
		email.setSmtpPort(587);
		email.setDebug(true);
		email.setSSLOnConnect(true);

		Session session = email.getMailSession();
		assertNotNull(session);
		assertEquals("smtp", session.getProperty("mail.transport.protocol"));

	}

//	Date	getSentDate()
	@Test
	public void testGetSentDate() throws EmailException {
		// set up

		email.setSentDate(testDate);
		Date date = email.getSentDate();

		// assert
		assertEquals(testDate, date);

	}

//	int    	getSocketConnectionTimeout()
	@Test
	public void getSocketConnectionTimeout() throws EmailException {
		// set up
		int timeout = 504;
		email.setSocketConnectionTimeout(timeout);

		// assert
		assertEquals(timeout, email.getSocketConnectionTimeout());
	}

//	Email  setFrom(String email)
	@Test
	public void setFrom() throws EmailException {
		// set up
		email.setFrom(TEST_EMAILS[0]);
		InternetAddress tempAddress = email.getFromAddress();

		// assert
		assertEquals(TEST_EMAILS[0], tempAddress.getAddress());
	}

}
