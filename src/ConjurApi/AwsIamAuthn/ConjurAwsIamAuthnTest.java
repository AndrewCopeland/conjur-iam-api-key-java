package ConjurApi.AwsIamAuthn;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

import ConjurApi.Config.AwsConfig;

class ConjurAwsIamAuthnTest {

	private String AMZDATE = "20200119T195144Z";
	private String DATESTAMP = "20200119";
	private String PAYLOAD_HASH = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
	private String CANONICAL_REQUEST = "GET\n/\nAction=GetCallerIdentity&Version=2011-06-15\nhost:sts.amazonaws.com\nx-amz-content-sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\nx-amz-date:20200119T195144Z\nx-amz-security-token:thisIsMyToken\n\nhost;x-amz-content-sha256;x-amz-date;x-amz-security-token\ne3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

	@Test
	void getAmzDate() {
		Date now = new Date();
		System.out.println(ConjurAwsIamAuthn.getAmzDate(now));
	}
	
	@Test
	void createCanonicalRequest() {
		String result = ConjurAwsIamAuthn.createCanonicalRequest(AMZDATE, AwsConfig.TOKEN, "host;x-amz-content-sha256;x-amz-date;x-amz-security-token", PAYLOAD_HASH);
		String expectedResult = CANONICAL_REQUEST;
		
		System.out.println("Cannonical Request: " + result);
		
		if (!result.equals(expectedResult)) {
			fail("Did not get expected results");
		}
	}
	
	@Test
	void sha256() {
		String expectedResults = "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9";
		String result = ConjurAwsIamAuthn.sha256("hello world");
		if(!result.equals(expectedResults)) {
			fail("Did not get expected results");
		}
	}
	
	@Test
	void createStringToSign() {
		String expectedResults = "AWS4-HMAC-SHA256\n20200119T195144Z\n20200119/us-east-1/sts/aws4_request\n4468aeffb43aefa9e1e05832d0192fd853253c0810b9191ea48a0987817f8d91";
		String result = ConjurAwsIamAuthn.createStringToSign(DATESTAMP, AMZDATE, CANONICAL_REQUEST);
		System.out.println("String to sign: " + result);
		if(!result.equals(expectedResults)) {
			fail("Did not get expected results");
		}
	}
	
	@Test
	void getSignatureKey() throws Exception {
		// String expectedResults = "\\x13d3\\x18\\x94\\xd6\\xe6^a\\x94\\x0c\\x8aU\\x9e\\xc7\\xba\\xb6\\xe6\\xec\\xaa\\x18\\x1e\\x03\\xae\\xaco\\xbd^(\\xfc\\xb5\\x08";
		byte[] result = ConjurAwsIamAuthn.getSignatureKey(AwsConfig.SECRET_KEY, DATESTAMP, ConjurAwsIamAuthn.REGION, ConjurAwsIamAuthn.SERVICE);
		System.out.println("Signature Key: " + result);
	}
	
	@Test
	void signString() throws Exception {
		
		String stringToSign = ConjurAwsIamAuthn.createStringToSign(DATESTAMP, AMZDATE, CANONICAL_REQUEST);
		byte[] signingKey = ConjurAwsIamAuthn.getSignatureKey(AwsConfig.SECRET_KEY, DATESTAMP, ConjurAwsIamAuthn.REGION, ConjurAwsIamAuthn.SERVICE);
		
		String result = ConjurAwsIamAuthn.signString(stringToSign, signingKey);
		System.out.println("Signed String: " + result);
	}
	
	@Test
	void getApiKey() {
		String apiKey = ConjurAwsIamAuthn.getApiKey("conjur-authn-iam", AwsConfig.ACCESS_KEY, AwsConfig.SECRET_KEY, AwsConfig.TOKEN);
		System.out.println("Api key: " + apiKey);
	}
}
