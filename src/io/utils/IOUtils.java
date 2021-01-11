package io.utils;

import java.nio.charset.*;
import java.net.*;
import java.io.*;

public class IOUtils
{
    public static final char DIR_SEPARATOR_UNIX = '/';
    public static final char DIR_SEPARATOR_WINDOWS = '\\';
    public static final String LINE_SEPARATOR_UNIX = "\n";
    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";
    private static char[] SKIP_CHAR_BUFFER;
    private static byte[] SKIP_BYTE_BUFFER;
    
    public static String toString(final InputStream input) throws IOException {
        return toString(input, Charset.defaultCharset());
    }
    
    public static String toString(final InputStream input, final Charset encoding) throws IOException {
        final StringBuilderWriter sw = new StringBuilderWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }
    
    public static String toString(final InputStream input, final String encoding) throws IOException {
        return toString(input, Charsets.toCharset(encoding));
    }
    
    public static String toString(final Reader input) throws IOException {
        final StringBuilderWriter sw = new StringBuilderWriter();
        copy(input, sw);
        return sw.toString();
    }
    
    public static String toString(final URI uri) throws IOException {
        return toString(uri, Charset.defaultCharset());
    }
    
    public static String toString(final URI uri, final Charset encoding) throws IOException {
        return toString(uri.toURL(), Charsets.toCharset(encoding));
    }
    
    public static String toString(final URI uri, final String encoding) throws IOException {
        return toString(uri, Charsets.toCharset(encoding));
    }
    
    public static String toString(final URL url) throws IOException {
        return toString(url, Charset.defaultCharset());
    }
    
    public static String toString(final URL url, final Charset encoding) throws IOException {
        final InputStream inputStream = url.openStream();
        try {
            return toString(inputStream, encoding);
        }
        finally {
            inputStream.close();
        }
    }
    
    public static String toString(final URL url, final String encoding) throws IOException {
        return toString(url, Charsets.toCharset(encoding));
    }
    
    @Deprecated
    public static String toString(final byte[] input) throws IOException {
        return new String(input);
    }
    
    public static String toString(final byte[] input, final String encoding) throws IOException {
        return new String(input, Charsets.toCharset(encoding));
    }
    
    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int)count;
    }
    
    public static long copyLarge(final InputStream input, final OutputStream output) throws IOException {
        return copyLarge(input, output, new byte[4096]);
    }
    
    public static long copyLarge(final InputStream input, final OutputStream output, final byte[] buffer) throws IOException {
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    
    public static long copyLarge(final InputStream input, final OutputStream output, final long inputOffset, final long length) throws IOException {
        return copyLarge(input, output, inputOffset, length, new byte[4096]);
    }
    
    public static long copyLarge(final InputStream input, final OutputStream output, final long inputOffset, final long length, final byte[] buffer) throws IOException {
        if (inputOffset > 0L) {
            skipFully(input, inputOffset);
        }
        if (length == 0L) {
            return 0L;
        }
        final int bufferLength;
        int bytesToRead = bufferLength = buffer.length;
        if (length > 0L && length < bufferLength) {
            bytesToRead = (int)length;
        }
        long totalRead;
        int read;
        for (totalRead = 0L; bytesToRead > 0 && -1 != (read = input.read(buffer, 0, bytesToRead)); bytesToRead = (int)Math.min(length - totalRead, bufferLength)) {
            output.write(buffer, 0, read);
            totalRead += read;
            if (length > 0L) {}
        }
        return totalRead;
    }
    
    public static void copy(final InputStream input, final Writer output) throws IOException {
        copy(input, output, Charset.defaultCharset());
    }
    
    public static void copy(final InputStream input, final Writer output, final Charset encoding) throws IOException {
        final InputStreamReader in = new InputStreamReader(input, Charsets.toCharset(encoding));
        copy(in, output);
    }
    
    public static void copy(final InputStream input, final Writer output, final String encoding) throws IOException {
        copy(input, output, Charsets.toCharset(encoding));
    }
    
    public static int copy(final Reader input, final Writer output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int)count;
    }
    
    public static long copyLarge(final Reader input, final Writer output) throws IOException {
        return copyLarge(input, output, new char[4096]);
    }
    
    public static long copyLarge(final Reader input, final Writer output, final char[] buffer) throws IOException {
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    
    public static void skipFully(final InputStream input, final long toSkip) throws IOException {
        if (toSkip < 0L) {
            throw new IllegalArgumentException("Bytes to skip must not be negative: " + toSkip);
        }
        final long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
        }
    }
    
    public static void skipFully(final Reader input, final long toSkip) throws IOException {
        final long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Chars to skip: " + toSkip + " actual: " + skipped);
        }
    }
    
    public static long skip(final InputStream input, final long toSkip) throws IOException {
        if (toSkip < 0L) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        if (IOUtils.SKIP_BYTE_BUFFER == null) {
            IOUtils.SKIP_BYTE_BUFFER = new byte[2048];
        }
        long remain;
        long n;
        for (remain = toSkip; remain > 0L; remain -= n) {
            n = input.read(IOUtils.SKIP_BYTE_BUFFER, 0, (int)Math.min(remain, 2048L));
            if (n < 0L) {
                break;
            }
        }
        return toSkip - remain;
    }
    
    public static long skip(final Reader input, final long toSkip) throws IOException {
        if (toSkip < 0L) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        if (IOUtils.SKIP_CHAR_BUFFER == null) {
            IOUtils.SKIP_CHAR_BUFFER = new char[2048];
        }
        long remain;
        long n;
        for (remain = toSkip; remain > 0L; remain -= n) {
            n = input.read(IOUtils.SKIP_CHAR_BUFFER, 0, (int)Math.min(remain, 2048L));
            if (n < 0L) {
                break;
            }
        }
        return toSkip - remain;
    }
    
    public static long copyLarge(final Reader input, final Writer output, final long inputOffset, final long length) throws IOException {
        return copyLarge(input, output, inputOffset, length, new char[4096]);
    }
    
    public static long copyLarge(final Reader input, final Writer output, final long inputOffset, final long length, final char[] buffer) throws IOException {
        if (inputOffset > 0L) {
            skipFully(input, inputOffset);
        }
        if (length == 0L) {
            return 0L;
        }
        int bytesToRead = buffer.length;
        if (length > 0L && length < buffer.length) {
            bytesToRead = (int)length;
        }
        long totalRead;
        int read;
        for (totalRead = 0L; bytesToRead > 0 && -1 != (read = input.read(buffer, 0, bytesToRead)); bytesToRead = (int)Math.min(length - totalRead, buffer.length)) {
            output.write(buffer, 0, read);
            totalRead += read;
            if (length > 0L) {}
        }
        return totalRead;
    }
    
    public static void copy(final Reader input, final OutputStream output) throws IOException {
        copy(input, output, Charset.defaultCharset());
    }
    
    public static void copy(final Reader input, final OutputStream output, final Charset encoding) throws IOException {
        final OutputStreamWriter out = new OutputStreamWriter(output, Charsets.toCharset(encoding));
        copy(input, out);
        out.flush();
    }
    
    public static void copy(final Reader input, final OutputStream output, final String encoding) throws IOException {
        copy(input, output, Charsets.toCharset(encoding));
    }
    
    public static class Charsets
    {
        public static final Charset ISO_8859_1;
        public static final Charset US_ASCII;
        public static final Charset UTF_16;
        public static final Charset UTF_16BE;
        public static final Charset UTF_16LE;
        public static final Charset UTF_8;
        
        public static Charset toCharset(final Charset charset) {
            return (charset == null) ? Charset.defaultCharset() : charset;
        }
        
        public static Charset toCharset(final String charset) {
            return (charset == null) ? Charset.defaultCharset() : Charset.forName(charset);
        }
        
        static {
            ISO_8859_1 = Charset.forName("ISO-8859-1");
            US_ASCII = Charset.forName("US-ASCII");
            UTF_16 = Charset.forName("UTF-16");
            UTF_16BE = Charset.forName("UTF-16BE");
            UTF_16LE = Charset.forName("UTF-16LE");
            UTF_8 = Charset.forName("UTF-8");
        }
    }
}
