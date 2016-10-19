package com.perrier.music.tag;

import java.io.File;

public interface ITagParser {

	ITag parseTag(File file) throws TagException;
}
