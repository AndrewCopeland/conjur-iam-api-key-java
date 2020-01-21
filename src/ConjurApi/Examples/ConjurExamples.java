package ConjurApi.Examples;

import ConjurApi.ConjurApi;
import ConjurApi.AwsIamAuthn.ConjurAwsIamAuthn;
import ConjurApi.Config.AwsConfig;
import ConjurApi.Config.ConjurConfig;
import ConjurApi.Exceptions.ConjurApiAuthenticateException;
import ConjurApi.HttpClient.HttpResponse;

public class ConjurExamples {
	public static void showExample() {
		// Authenticate using the IAM authenticator
		
		// STEP 1: setup the ConjurConfig object
		String conjurUrl = "https://conjur-master";
		String conjurAccount = "conjur";
		ConjurConfig config = new ConjurConfig(conjurUrl, conjurAccount);
		config.ignoreSsl = true;
		config.username = "host/622705945757/conjur-authn-iam";
		
		// STEP 2: init the api key and authenticate
		String apiKey = ConjurAwsIamAuthn.getApiKey("conjur-authn-iam", AwsConfig.ACCESS_KEY, AwsConfig.SECRET_KEY, AwsConfig.TOKEN);
		config.apiKey = apiKey;
		config.authnType = "iam";
		config.serviceId = "test";

		ConjurApi api = new ConjurApi(config);
		try {
			api.authenticate();
		} catch (ConjurApiAuthenticateException e) {
			e.printStackTrace();
			System.out.println("Failed to authenticate, see stacktrace for more info.");
			return;
		}
		
		// STEP 3: actually perform actions on the conjur server
		// create a new secret within policy
		String secretId = "new-secret";
		HttpResponse res = api.appendPolicy("ConjurApi", "- !variable " + secretId);
		System.out.println("Created '!variable new-secret' in policy 'ConjurApi': " + res.body);
		String fullSecretId = "ConjurApi/new-secret";
		
		// list resources we have access to
		res = api.list();
		System.out.println("List of resources: " + res.body);
		
		// set the value of the 'ConjurApi/new-secret'
		res = api.setSecret(fullSecretId, "defaultNewPasswordValue");
		System.out.println("'new-secret' value set");
		
		// get the value of 'ConjurApi/new-secret'
		res = api.getSecret(fullSecretId);
		System.out.println("'new-secret' value: " + res.body);	
	}
}
