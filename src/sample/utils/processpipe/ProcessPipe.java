package sample.utils.processpipe;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ProcessPipe {
    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;

    public enum ExitCodes {
        SUCCEEDED,
        ERROR,
    }

    public ProcessPipe(String exePath, String arg) throws IOException {
        this.process = Runtime.getRuntime().exec(exePath + " " + arg);
    }

    public BufferedReader getReader() throws UnsupportedEncodingException {
        assert (process != null);

        reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), "windows-1251")
        );

        return reader;
    }

    public BufferedWriter getWriter() {
        assert (process != null);

        writer = new BufferedWriter(
                new OutputStreamWriter(process.getOutputStream())
        );

        return writer;
    }

    public int waitFor() throws InterruptedException {
        process.waitFor();
        return process.exitValue();
    }

    public void destroy() throws IOException {
        assert process != null;

        process.destroyForcibly();
        writer.close();
        reader.close();
    }
}
