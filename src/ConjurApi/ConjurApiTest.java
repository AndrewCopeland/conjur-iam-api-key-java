package ConjurApi;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import ConjurApi.AwsIamAuthn.ConjurAwsIamAuthn;
import ConjurApi.Config.AwsConfig;
import ConjurApi.Config.ConjurConfig;
import ConjurApi.Exceptions.ConjurApiAuthenticateException;
import ConjurApi.HttpClient.HttpResponse;

class ConjurApiTest {
	
	private String username = "host/ConjurApi";
	private String apiKey = "2pdh79k1axtndy3tdr0022rfbvd315ze32p2r9wsvv2f5tq3s2jg9n4w";
	

	private ConjurConfig defaultConfig() {
		ConjurConfig config = new ConjurConfig("https://conjur-master", "conjur");
		config.ignoreSsl = true;
		return config;
	}
	
	private ConjurApi defaultApi() {
		return new ConjurApi(defaultConfig());
	}
	
	private void validStatusCode(HttpResponse response, int statusCode) {
		if(response.statusCode != statusCode) {
			fail("Invalid status code: " + response.statusCode);
		}
	}
	
	private void validStatusCode(HttpResponse response) {
		validStatusCode(response, 200);
	}

	private ConjurApi authenticatedApi() {
		ConjurConfig config = defaultConfig();
		config.username = username;
		config.apiKey = apiKey;
		ConjurApi api = new ConjurApi(config);
		try {
			api.authenticate();
		} catch (ConjurApiAuthenticateException e) {
			fail(e.getMessage());
		}
		return api;
	}
	
	private String exampleAppendPolicy() {
		String policy = "- !host new-host\n"
		         + "\n"
		         + "- !variable secret\n";
		return policy;
	}
	
	@Test
	void health() {
		ConjurApi api = defaultApi();
		HttpResponse res = api.health();
		validStatusCode(res);
		System.out.println(res.body);
	}
	
	@Test
	void authenticate() {
		authenticatedApi();
	}
	
	@Test
	void authenticateFailure() {
		ConjurConfig config = defaultConfig();
		config.username = username;
		config.apiKey = "notReal";
		ConjurApi api = new ConjurApi(config);
		try {
			api.authenticate();
		} catch (ConjurApiAuthenticateException e) {
			return;
		}
		fail("Authentication should have failed");
	}
	
	@Test
	void authenticateIam() throws ConjurApiAuthenticateException {
		// the configuration requires some tweeks when authenticating with iam,
		ConjurConfig config = defaultConfig();
		config.username = "host/622705945757/conjur-authn-iam";
		config.authnType = "iam";
		config.serviceId = "test";
		String apiKey = ConjurAwsIamAuthn.getApiKey("conjur-authn-iam", AwsConfig.ACCESS_KEY, AwsConfig.SECRET_KEY, AwsConfig.TOKEN);
		config.apiKey = apiKey;
		
		// make sure to authenticate using ConjurApi.authenticateIam()
		ConjurApi api = new ConjurApi(config);
		api.authenticate();
		HttpResponse res = api.list();
		validStatusCode(res);
		System.out.println(res.body);
	}
	
	@Test
	void list() {
		ConjurApi api = authenticatedApi();
		HttpResponse res = api.list();
		validStatusCode(res);
		System.out.println(res.body);
	}
	
	@Test
	void appendPolicy() {
		ConjurApi api = authenticatedApi();
		HttpResponse res = api.appendPolicy("ConjurApi", exampleAppendPolicy());
		validStatusCode(res, 201);
		System.out.println(res.body);
	}
	
	@Test
	void replacePolicy() {
		ConjurApi api = authenticatedApi();
		HttpResponse res = api.replacePolicy("ConjurApi", exampleAppendPolicy());
		validStatusCode(res, 201);
		System.out.println(res.body);
	}
	
	@Test
	void patchPolicy() {
		ConjurApi api = authenticatedApi();
		HttpResponse res = api.patchPolicy("ConjurApi", exampleAppendPolicy());
		validStatusCode(res, 201);
		System.out.println(res.body);
	}
	
	@Test
	void getSecret() {
		ConjurApi api = authenticatedApi();
		HttpResponse res = api.getSecret("ConjurApi/secret");
		validStatusCode(res);
		System.out.println(res.body);
	}
	
	@Test
	void getNonExistentSecret() {
		ConjurApi api = authenticatedApi();
		HttpResponse res = api.getSecret("ConjurApi/secret/notreal");
		validStatusCode(res, 404);
		System.out.println(res.body);
	}
	
	@Test
	void setSecret() {
		ConjurApi api = authenticatedApi();
		HttpResponse res = api.setSecret("ConjurApi/secret", "newValue");
		validStatusCode(res, 201);
		System.out.println(res.body);
	}
	
	@Test
	void setNonExistentSecret() {
		ConjurApi api = authenticatedApi();
		HttpResponse res = api.setSecret("ConjurApi/secret/notreal", "newValue");
		validStatusCode(res, 404);
		System.out.println(res.body);
	}
}
