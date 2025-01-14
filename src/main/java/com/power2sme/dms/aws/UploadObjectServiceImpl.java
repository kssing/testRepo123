//package com.power2sme.dms.aws;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.power2sme.dms.aws.configuration.AWSBucketConfiguration;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//public class UploadObjectServiceImpl //implements UploadObjectService 
//{
//
//	@Autowired
//	AWSBucketConfiguration awsBucketConfiguration;
//
////	public URL uploadObject(String uniqueId, String filePaths, String mediaType) throws IOException {
////		Regions clientRegion = Regions.AP_SOUTHEAST_1;
////		String bucketName = awsBucketConfiguration.getBucketname();
////		String fileObjKeyName = uniqueId;
////		// String fileObjKeyName = "newmargin.pdf";
////		// String filePath = awsBucketConfiguration.getFilepath();
////		String filePath = filePaths;
////		URL url = null;
////		try {
////			AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(clientRegion)
////					.withCredentials(new ProfileCredentialsProvider()).build();
////
////			// Upload a file as a new object with ContentType and title
////			// specified.
////			PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(filePath));
////			request.setCannedAcl(CannedAccessControlList.PublicRead);
////			ObjectMetadata metadata = new ObjectMetadata();
////			metadata.setContentType(mediaType);
////			metadata.addUserMetadata("x-amz-meta-title", "someTitles");
////			request.setMetadata(metadata);
////			s3Client.putObject(request);
////			url = s3Client.getUrl(bucketName, fileObjKeyName);
////		} catch (AmazonServiceException e) {
////
////			// The call was transmitted successfully, but Amazon S3 couldn't
////			// process
////			// it, so it returned an error response.
////			DmsLogUtil.logAtDebug(log, e);
////
////		} catch (SdkClientException e) {
////
////			// Amazon S3 couldn't be contacted for a response, or the client
////			// couldn't parse the response from Amazon S3.
////			DmsLogUtil.logAtDebug(log, e);
////
////		}
////		return url;
////	}
//}
