package net.jsdpu.process.queue;

import java.io.InputStream;

import org.junit.Ignore;

@Ignore
public class TestInputStreamSplitting {
	public void testSplitting() {
		// given
		String[] inputStreamsPattern = createInputStreamPatterns();
		Integer[] returnCodes = createReturnCodes();
		InputStream source = createSource(inputStreamsPattern, returnCodes);
		InputStreamSplitter splitter = new InputStreamSplitter(source,
				inputStreamsPattern.length);

		// when
		splitter.start();

		// then
	}

	private String[] createInputStreamPatterns() {
		return null;
	}

	private Integer[] createReturnCodes() {
		return null;
	}

	private InputStream createSource(String[] inputStreamPattern,
			Integer[] returnCodes) {
		return null;
	}
}
