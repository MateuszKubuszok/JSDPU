package net.jsdpu.process.queue;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

class ProcessInputStream extends InputStream {
	private final LinkedList<Integer> readChars;
	private boolean closed;
	private final Object mutex;

	ProcessInputStream() {
		readChars = new LinkedList<Integer>();
		closed = false;
		mutex = new Object();
	}

	void add(int nextChar) {
		readChars.addLast(nextChar);
		synchronized (mutex) {
			mutex.notify();
		}
	}

	@Override
	public int available() {
		return readChars.size();
	}

	@Override
	public void close() {
		closed = true;
	}

	@Override
	public int read() throws IOException {
		if (!closed && readChars.isEmpty())
			synchronized (mutex) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					throw new IOException(e);
				}
			}

		if (closed)
			return -1;

		return readChars.pollFirst();
	}
}