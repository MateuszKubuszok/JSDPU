package net.jsdpu.process.queue;

import java.io.Closeable;

interface Splitter<Stream extends Closeable> {
	static final String PREFIX = "FIN:";
	static final char SEPARATOR = '\n';

	void start();

	Stream[] getSplittedStreams();

	Integer[] getReturnedCodes();
}
