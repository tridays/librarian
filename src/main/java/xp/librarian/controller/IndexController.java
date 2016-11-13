package xp.librarian.controller;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

import com.google.common.primitives.Bytes;

/**
 * @author xp
 */
@ApiIgnore
@RestController
@RequestMapping(value = "/")
public class IndexController {

    @GetMapping("")
    public Object index() {
        return "Welcome to Librarian API.";
    }

    @GetMapping("douban/{isbn:[0-9\\-]+}/")
    public Object douban(@PathVariable String isbn) throws Exception {
        URL url = new URL(String.format("https://api.douban.com/v2/book/isbn/%s", isbn));
        URLConnection conn = url.openConnection();
        conn.setReadTimeout(5000);
        InputStream is = null;
        try {
            is = new BufferedInputStream(conn.getInputStream());
            Charset utf_8 = Charset.forName("UTF-8");
            List<byte[]> buffer = new LinkedList<>();
            byte[] b = new byte[8192];
            int len;
            while ((len = is.read(b)) != -1) {
                buffer.add(Arrays.copyOf(b, len));
            }
            return new String(Bytes.concat(buffer.toArray(new byte[0][])), utf_8);
        } finally {
            if (is != null) {
                is.close();
                is = null;
            }
        }
    }

}
