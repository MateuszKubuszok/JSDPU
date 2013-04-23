package net.jsdpu.process.queue;

import java.io.InputStream;

class InputStreamSplitter implements Splitter<InputStream> {
	private final InputStream source;
	private final ProcessInputStream[] targets;
	private final Integer[] returnedCodes;

	InputStreamSplitter(InputStream source, int expectedInputs) {
		this.source = source;
		targets = new ProcessInputStream[expectedInputs];
		for (int i = 0; i < expectedInputs; i++)
			targets[i] = new ProcessInputStream();
		returnedCodes = new Integer[expectedInputs];
	}

	@Override
	public void start() {
		new Thread(
				new InputStreamSplitterThread(source, targets, returnedCodes))
				.start();
	}

	@Override
	public Integer[] getReturnedCodes() {
		return returnedCodes;
	}

	@Override
	public InputStream[] getSplittedStreams() {
		return targets;
	}
}
