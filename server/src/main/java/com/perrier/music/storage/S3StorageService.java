package com.perrier.music.storage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.util.concurrent.AbstractIdleService;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class S3StorageService extends AbstractIdleService {

	private static final Logger log = LoggerFactory.getLogger(S3StorageService.class);

	private static final String AUDIO_KEY_PREFIX = "audio";
	private static final String COVER_KEY_PREFIX = "cover";

	private final String accessKeyId;
	private final String secretAccessKey;
	private final String bucket;
	private final String region;

	private AmazonS3Client s3Client;

	public S3StorageService(String accessKeyId, String secretAccessKey, String bucket, String region) {
		this.accessKeyId = accessKeyId;
		this.secretAccessKey = secretAccessKey;
		this.bucket = bucket;
		this.region = region;
	}

	@Override
	protected void startUp() throws Exception {
		log.info("Starting AWS S3 Storage Service");

		this.s3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard() //
				.withCredentials(
						new AWSStaticCredentialsProvider(new BasicAWSCredentials(this.accessKeyId, this.secretAccessKey))) //
				.withRegion(Regions.fromName(this.region))
				.build();
	}

	@Override
	protected void shutDown() throws Exception {
		this.s3Client.shutdown();
	}

	public InputStream getAudioStream(String key) {
		checkNotNull(key);

		S3Object s3Object = s3Client.getObject(this.bucket, key);
		S3ObjectInputStream inputStream = s3Object.getObjectContent();

		return inputStream;
	}

	public URL putCover(String coverHash, byte[] imageData, String contentType) {
		checkNotNull(coverHash);
		checkNotNull(imageData);
		checkArgument(imageData.length > 0);

		String key = getCoverKey(coverHash, contentType);
		InputStream input = new ByteArrayInputStream(imageData);
		ObjectMetadata metaData = new ObjectMetadata();
		metaData.setContentLength(imageData.length);
		metaData.setContentType(contentType);

		s3Client.putObject(this.bucket, key, input, metaData);
		URL url = s3Client.getUrl(this.bucket, key);

		return url;
	}

	public static String getCoverKey(String coverHash, String contentType) {
		String extension;

		switch (contentType) {
		case "image/png":
			extension = "png";
			break;
		case "image/jpg":
		case "image/jpeg":
			extension = "jpg";
			break;
		default:
			throw new RuntimeException("unsupported media type");
		}

		return COVER_KEY_PREFIX + "/" + coverHash + "." + extension;
	}

	public static void main(String[] args) throws Exception {
		S3StorageService s3 = new S3StorageService(args[0], args[1], args[2], args[3]);

		s3.startUp();
		List<Bucket> buckets = s3.s3Client.listBuckets();

		System.out.println(buckets);

		Bucket bucket = buckets.get(0);

		ListObjectsRequest req = new ListObjectsRequest()
				.withBucketName(bucket.getName())
				.withPrefix(AUDIO_KEY_PREFIX + "/");
		ObjectListing objectListing = s3.s3Client.listObjects(req);

		List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
		System.out.println(objectSummaries);

		String key = objectSummaries.get(1).getKey();
		System.out.println("KEY: " + key);

		System.out.println(s3.s3Client.getResourceUrl(s3.bucket, key));
		System.out.println(s3.s3Client.getUrl(s3.bucket, key));

		s3.shutDown();
	}
}
