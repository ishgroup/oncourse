/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.*;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.net.URL;
import java.util.Date;

public class S3Service implements IS3Service {

	private static final String BUCKET_ACCESS_POLICY_DOCUMENT_TEMPLATE =
					"{\n" +
					"    \"Statement\": [\n" +
					"		{\n" +
					"      		\"Effect\": \"Allow\",\n" +
					"      		\"Action\": [\n" +
					"        		\"s3:ListBucket\",\n" +
					"        		\"s3:GetBucketLocation\",\n" +
					"        		\"s3:ListBucketMultipartUploads\"\n" +
					"      		],\n" +
					"      		\"Resource\": \"arn:aws:s3:::%s\",\n" +
					"      		\"Condition\": {}\n" +
					"    	},\n" +
					"    	{\n" +
					"      		\"Effect\": \"Allow\",\n" +
					"      		\"Action\": [\n" +
					"        		\"s3:AbortMultipartUpload\",\n" +
					"        		\"s3:DeleteObject\",\n" +
					"        		\"s3:DeleteObjectVersion\",\n" +
					"        		\"s3:GetObject\",\n" +
					"        		\"s3:GetObjectAcl\",\n" +
					"        		\"s3:GetObjectVersion\",\n" +
					"        		\"s3:GetObjectVersionAcl\",\n" +
					"        		\"s3:PutObject\",\n" +
					"        		\"s3:PutObjectAcl\",\n" +
					"        		\"s3:PutObjectVersionAcl\"\n" +
					"      		],\n" +
					"      		\"Resource\": \"arn:aws:s3:::%s/*\",\n" +
					"      		\"Condition\": {}\n" +
					"    	}\n" +
					"    ]\n" +
					"}";

	private AWSCredentials credentials;
	
	@Inject
	public S3Service(PreferenceController preferenceController) {
		this(preferenceController.getStorageAccessId(), preferenceController.getStorageAccessKey());
	}

	public S3Service(String accessKeyId, String accessKey) {
		this.credentials = new BasicAWSCredentials(accessKeyId, accessKey);
	}

	@Override
	public AccessKey createS3User(String userName, String bucketName) {
		AmazonIdentityManagementClient client = new AmazonIdentityManagementClient(credentials);

		CreateGroupRequest createGroupRequest = new CreateGroupRequest(String.format("%s-group", userName));
		CreateGroupResult createGroupResult = client.createGroup(createGroupRequest);

		Group group = createGroupResult.getGroup();

		PutGroupPolicyRequest putGroupPolicyRequest = new PutGroupPolicyRequest();
		putGroupPolicyRequest.setGroupName(group.getGroupName());
		putGroupPolicyRequest.setPolicyName(String.format("%s-%s-policy", group.getGroupName(), bucketName));
		putGroupPolicyRequest.setPolicyDocument(String.format(BUCKET_ACCESS_POLICY_DOCUMENT_TEMPLATE, bucketName, bucketName));

		client.putGroupPolicy(putGroupPolicyRequest);

		CreateUserRequest createUserRequest = new CreateUserRequest(userName);
		CreateUserResult createUserResult = client.createUser(createUserRequest);

		User user = createUserResult.getUser();

		AddUserToGroupRequest addUserToGroupRequest = new AddUserToGroupRequest(group.getGroupName(), user.getUserName());
		client.addUserToGroup(addUserToGroupRequest);

		CreateAccessKeyRequest createAccessKeyRequest = new CreateAccessKeyRequest();
		createAccessKeyRequest.setUserName(user.getUserName());

		CreateAccessKeyResult createAccessKeyResult = client.createAccessKey(createAccessKeyRequest);
		return createAccessKeyResult.getAccessKey();
	}

	@Override
	public void createBucket(String name) {
		AmazonS3Client client = new AmazonS3Client(credentials);

		CreateBucketRequest request = new CreateBucketRequest(name, Region.AP_Sydney);
		client.createBucket(request);
		
		// enable bucket versioning
		BucketVersioningConfiguration versioningConfiguration = 
				new BucketVersioningConfiguration(BucketVersioningConfiguration.ENABLED);
		SetBucketVersioningConfigurationRequest versioningConfigurationRequest = 
				new SetBucketVersioningConfigurationRequest(name, versioningConfiguration);
		
		client.setBucketVersioningConfiguration(versioningConfigurationRequest);
	}

	@Override
	public String getTemporaryUrl(String bucketName, String fileKey, Long expireTimeout) {
		AmazonS3Client client = new AmazonS3Client(credentials);

		Date expiration = new Date();
		expiration.setTime(expiration.getTime() + expireTimeout);

		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileKey);
		request.setMethod(HttpMethod.GET);
		request.setExpiration(expiration);

		URL url = client.generatePresignedUrl(request);

		return url.toString();
	}

	
	@Override
	public String getPermanentUrl(String bucketName, String fileKey) {
//		AmazonS3Client client = new AmazonS3Client(credentials);
//
//		return client.getUrl(bucketName, fileKey).toString();

        /**
         * workaround to exclude 'wrong certificate' warning
         * when url like https://s3.amazonaws.com/nida.assets.oncourse.net.au/78b710e2-a32d-420c-933e-93fe8fa3447b
         */

        return String.format("https://s3-ap-southeast-2.amazonaws.com/%s/%s", bucketName, fileKey);

	}
}
