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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.perrier.music.entity.track.Track;
import org.apache.commons.io.IOUtils;

/**
 * Creates a zip file of all the tracks for an album
 */
public class AlbumZipper {

	private static final String ZIP_PREFIX = "album-";

	public static File zip(Album album) throws IOException {

		Path zipPath = Files.createTempFile(ZIP_PREFIX + album.getId() + "-", ".zip");
		File zipFile = zipPath.toFile();

		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
			List<Track> tracks = album.getTracks(); // TODO: Load track eagerly
			for (Track t : tracks) {
				File path = new File(t.getPath());
				ZipEntry zipEntry = new ZipEntry(path.getName());
				zos.putNextEntry(zipEntry);
				try (FileInputStream in = new FileInputStream(path)) {
					IOUtils.copy(in, zos);
				}
			}
		}

		return zipFile;
	}
}
