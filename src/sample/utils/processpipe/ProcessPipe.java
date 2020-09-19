package sample.utils.processpipe;

import java.io.*;

public class ProcessPipe {
    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;

    public ProcessPipe(String exePath, String arg) throws IOException {
        this.process = Runtime.getRuntime().exec(exePath + " " + arg);
    }

    public BufferedReader getReader() {
        assert (process != null);

        reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        return reader;
    }

    public BufferedWriter getWriter() {
        assert (process != null);

        writer =  new BufferedWriter(
                new OutputStreamWriter(process.getOutputStream())
        );

        return writer;
    }

    public void destroy() {
        assert process != null;

        process.destroyForcibly();
    }
}
