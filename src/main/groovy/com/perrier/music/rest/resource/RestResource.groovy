package com.perrier.music.rest.resource;

import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypes;

public class RestResource {

	private static final Tika TIKA = new Tika()
	
	def getMimeType(File file) {

		return TIKA.detect(file)
		//return URLConnection.getFileNameMap().getContentTypeFor(file.getName()) ?: "application/octet-stream"
	} 
	
}
