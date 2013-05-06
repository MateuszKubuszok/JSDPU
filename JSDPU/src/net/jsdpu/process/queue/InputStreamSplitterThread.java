package net.jsdpu.process.queue;

import static net.jsdpu.process.queue.Splitter.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

class InputStreamSplitterThread implements Runnable {
    private final InputStream source;
    private final ProcessInputStream[] targets;
    private final Integer[] returnedCodes;
    private int currentStream;

    private final LinkedList<Integer> buffer;
    private String prefixed;
    private int currentChar;
    private boolean shouldCheck;
    private int matching;

    public InputStreamSplitterThread(InputStream source, ProcessInputStream[] targets,
            Integer[] returnedCodes) {
        this.source = source;
        this.targets = targets;
        this.returnedCodes = returnedCodes;
        this.currentStream = 0;

        buffer = new LinkedList<Integer>();

        currentChar = -1;
        shouldCheck = false;
        matching = 0;
    }

    @Override
    public void run() {
        do {
            try {
                getNextChar();

                if (isEndOfStream())
                    closeRemainingStreams();
                else if (isSeperatorMet())
                    checkSeparator();
                else if (shouldCheck)
                    checkMatching();
                else
                    passChar();
            } catch (IOException e) {
                // TODO: handle this situation
            }
        } while (!isEndOfStream());
    }

    private void getNextChar() throws IOException {
        currentChar = source.read();
    }

    private boolean isSeperatorMet() {
        return currentChar == SEPARATOR;
    }

    private void checkSeparator() {
        try {
            returnedCodes[currentStream] = Integer.parseInt(prefixed);
            targets[currentStream].close();
            currentStream++;
        } catch (NumberFormatException e) {
            flushBuffer();
        }

        prefixed = "";
        shouldCheck = true;
        matching = 0;
    }

    private void checkMatching() {
        if (matching == PREFIX.length()) {
            prefixed += currentChar;
            buffer.addLast(currentChar);
        } else if (PREFIX.charAt(matching) != currentChar) {
            shouldCheck = false;
            flushBuffer();
        } else {
            buffer.addLast(currentChar);
            matching++;
        }
    }

    private void closeRemainingStreams() {
        for (ProcessInputStream is : targets)
            is.close();
    }

    private boolean isEndOfStream() {
        return currentChar < 0;
    }

    private void flushBuffer() {
        while (!buffer.isEmpty())
            targets[currentStream].add(buffer.pollFirst());
        passChar();
    }

    private void passChar() {
        targets[currentStream].add(currentChar);
    }
}