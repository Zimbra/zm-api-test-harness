package utilities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import exception.HarnessException;

public class CommandLineUtilities {

    public static JSch jsch = new JSch();
    public static Properties config = new Properties();

    public static String executeCommand(String host, int port, String user, String password, String command) {
        config.put("StrictHostKeyChecking", "no");
        ArrayList<String> out = new ArrayList<String>();
        if (host.isEmpty() || user.isEmpty() || password.isEmpty() || command.isEmpty()) {
            return null;
        }
        try {
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                out.add(line);
            }
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            throw new HarnessException(e.getMessage());
        }
        return out.toString();
    }

}
