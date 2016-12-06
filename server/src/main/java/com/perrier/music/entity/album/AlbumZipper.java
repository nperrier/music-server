/*
 * (c) Copyright 2016 Calabrio, Inc.
 * All Rights Reserved. www.calabrio.com LICENSED MATERIALS
 * Property of Calabrio, Inc., Minnesota, USA
 *
 * No part of this publication may be reproduced, stored or transmitted,
 * in any form or by any means (electronic, mechanical, photocopying,
 * recording or otherwise) without prior written permission from Calabrio, Inc.
 */

package com.perrier.music.entity.album;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;
import com.perrier.music.entity.track.Track;
import com.perrier.music.storage.S3StorageService;

/**
 * Creates a zip file of all the tracks for an album
 */
public class AlbumZipper {

	private static final String ZIP_PREFIX = "album-";

	private final S3StorageService storageService;

	@Inject
	public AlbumZipper(S3StorageService storageService) {
		this.storageService = storageService;
	}

	public File zip(Album album) throws IOException {

		Path zipPath = Files.createTempFile(ZIP_PREFIX + album.getId() + "-", ".zip");
		File zipFile = zipPath.toFile();

		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
			List<Track> tracks = album.getTracks();
			for (Track t : tracks) {
				String name = createFileName(t);
				ZipEntry zipEntry = new ZipEntry(name);
				zos.putNextEntry(zipEntry);

				try (InputStream audioStream = this.storageService.getAudioStream(t.getAudioStorageKey())) {
					IOUtils.copy(audioStream, zos);
				}
			}
		}

		return zipFile;
	}

	private static String createFileName(Track track) {
		StringBuilder b = new StringBuilder();

		if (track.getNumber() != null) {
			b.append(track.getNumber()).append("-");
		}

		b.append(track.getName());

		if (track.getArtist() != null) {
			b.append("-").append(track.getArtist().getName());
		}

		b.append(".mp3");

		return b.toString();
	}
}