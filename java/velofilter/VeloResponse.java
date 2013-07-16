package black.ti.server;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Это Класс-пустышка, его назначение "обернуть" пришедший на обслуживание объект  httpServletResponse, так что бы никакой
 * печатаемый код из сервлета/jsp страницы не поступил к пользователю
 */
public class VeloResponse extends HttpServletResponseWrapper {
    public VeloResponse(HttpServletResponse httpServletResponse) {
        super(httpServletResponse);
    }

    CharArrayWriter charBuf = null;
    PrintWriter charWriter = null;

    ByteArrayOutputStream byteBuf = null;
    ServletOutputStream byteWriter = null;

    public PrintWriter getWriter() throws IOException {
        if (charWriter == null) {
            charBuf = new CharArrayWriter(10000);
            charWriter = new PrintWriter(charBuf);
        }
        return charWriter;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (byteBuf == null) {
            byteBuf = new ByteArrayOutputStream(10000);
            byteWriter = new ServletOutputStream() {
                public void write(int b) throws IOException {
                    byteBuf.write(b);
                }
            };
        }
        return byteWriter;
    }

    public String getContent() {
        if (charBuf != null) {
            charWriter.flush();
            return charBuf.toString();
        }
        if (byteBuf != null) {
            String enc = System.getProperty("fio");
            try {
                byteWriter.flush();
                return byteBuf.toString(enc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
