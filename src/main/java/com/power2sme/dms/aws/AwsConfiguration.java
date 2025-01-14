//package com.power2sme.dms.aws;
//
//import java.io.File;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//
//import com.amazonaws.AmazonServiceException;
//import com.amazonaws.SdkClientException;
//import com.amazonaws.auth.profile.ProfileCredentialsProvider;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import com.power2sme.dms.aws.configuration.AWSBucketConfiguration;
//
////@Configuration
//public class AwsConfiguration {
//
////	@Bean
////	UploadObjectServiceImpl uploadObjectServiceImplBean(){
////		return new UploadObjectServiceImpl();
////	}
//	
//	@Autowired
//	AWSBucketConfiguration aWSBucketConfiguration;
//	
//	public static void main(String[] args) {
////		String clientRegion = "Asia Pacific (Singapore)";
//		String bucketName = "p2sdms";
//		String fileObjKeyName = "newmargin.pdf";
//		String filePath ="/Users/p2s/Desktop/newmargin.pdf";
//		Map<String,URL> fileUploadMap =  new HashMap();
//        try {
//            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
//                    .withRegion(Regions.AP_SOUTHEAST_1)
//                    .withCredentials(new ProfileCredentialsProvider())
//                    .build();
//        
//            
//            // Upload a file as a new object with ContentType and title specified.
//            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(filePath));
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setContentType(MediaType.APPLICATION_PDF_VALUE);
//            metadata.addUserMetadata("x-amz-meta-title", "someTitle");
//            request.setMetadata(metadata);
//            s3Client.putObject(request);
//            //fileUploadMap.put(fileObjKeyName, s3Client.getUrl(bucketName, fileObjKeyName));
//            System.out.println(s3Client.getUrl(bucketName, fileObjKeyName));
//        }
//        catch(AmazonServiceException e) {
//            // The call was transmitted successfully, but Amazon S3 couldn't process 
//            // it, so it returned an error response.
//            e.printStackTrace();
//        }
//        catch(SdkClientException e) {
//            // Amazon S3 couldn't be contacted for a response, or the client
//            // couldn't parse the response from Amazon S3.
//            e.printStackTrace();
//        }
//	}
//}
