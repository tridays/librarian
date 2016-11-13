package xp.librarian.utils;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xp
 */
public class UploadUtils {

    private static final Logger LOG = LoggerFactory.getLogger(UploadUtils.class);

    public static String PATH = "./uploads";

    private static File path;

    static {
        path = new File(PATH);
        if (!path.exists()) {
            assert path.mkdirs();
        }
    }

    public static File getPath() {
        return path;
    }

    public static String makePath(File file) {
        return makePath(file.getName());
    }

    public static String makePath(String filename) {
        return String.format("/uploads/%s/", filename);
    }

    public static String upload(MultipartFile file) throws RuntimeException {
        if (file == null) {
            return null;
        }
        try {
            File dstFile = new File(UploadUtils.getPath(), UUID.randomUUID().toString());
            OutputStream dstOutput = new BufferedOutputStream(new FileOutputStream(dstFile));
            dstOutput.write(file.getBytes());
            dstOutput.close();
            return UploadUtils.makePath(dstFile);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(null, e);
        }
    }

    public static String fetch(String url) throws RuntimeException {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        try {
            URL website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            File dstFile = new File(UploadUtils.getPath(), UUID.randomUUID().toString());
            FileOutputStream fos = new FileOutputStream(dstFile);
            fos.getChannel().transferFrom(rbc, 0, 3 * 1024 * 1024);
            return UploadUtils.makePath(dstFile);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(null, e);
        }
    }

    public static String makeUrl(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        if (path.contains("://")) {
            return path;
        }
        HttpServletRequest request = ServletUtils.getRequest();
        return String.format("http://%s:%s%s",
                request.getLocalName(),
                request.getLocalPort(),
                path);
    }

}
