package ConjurApi.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HttpClientTest {

	@Test
	void get() {
		HttpResponse response = HttpClient.get("https://google.com", "header");
		if(response.statusCode != 200) {
			fail("Non-200 status code returned");
		}
	}
	
	@Test
	void post() {
		HttpResponse response = HttpClient.post("https://google.com", "header", "bodyContent");
		// google returns a 405 if you try to post to google.com
		if(response.statusCode != 405) {
			fail("Non-200 status code returned: \n " + response.body);
		}
	}

}
