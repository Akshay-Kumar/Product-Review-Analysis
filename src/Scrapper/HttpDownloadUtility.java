package Scrapper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.*;

/**
 * A utility that downloads a file from a URL.
 * @author www.codejava.net
 *
 */
public class HttpDownloadUtility {
    private static final int BUFFER_SIZE = 4096;
    public static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:53.0) Gecko/20100101 Firefox/53.0";
    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    public static void downloadFile(String fileURL, String saveDir)
            throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.addRequestProperty("User-Agent", userAgent);
        int responseCode = httpConn.getResponseCode();
        
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();
 
            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }
 
            //Remove special characters from filename
            fileName = fileName.replaceAll("[-+^:,]","");
            
            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);
 
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
             
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
 
            outputStream.close();
            inputStream.close();
 
            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }
    
    /**
     * Downloads from a (http/https) URL and saves to a file. 
     * Does not consider a connection error an Exception. Instead it returns:
     *  
     *    0=ok  
     *    1=connection interrupted, timeout (but something was read)
     *    2=not found (FileNotFoundException) (404) 
     *    3=server error (500...) 
     *    4=could not connect: connection timeout (no internet?) java.net.SocketTimeoutException
     *    5=could not connect: (server down?) java.net.ConnectException
     *    6=could not resolve host (bad host, or no internet - no dns)
     * 
     * @param file File to write. Parent directory will be created if necessary
     * @param url  http/https url to connect
     * @param secsConnectTimeout Seconds to wait for connection establishment
     * @param secsReadTimeout Read timeout in seconds - trasmission will abort if it freezes more than this 
     * @return See above
     * @throws IOException Only if URL is malformed or if could not create the file
     */
    public static int saveUrl(final Path file, final URL url, 
      int secsConnectTimeout, int secsReadTimeout) throws IOException {
        Files.createDirectories(file.getParent()); // make sure parent dir exists , this can throw exception
        URLConnection conn = url.openConnection(); // can throw exception if bad url
        if( secsConnectTimeout > 0 ) conn.setConnectTimeout(secsConnectTimeout * 1000);
        if( secsReadTimeout > 0 ) conn.setReadTimeout(secsReadTimeout * 1000);
        int ret = 0;
        boolean somethingRead = false;
        try (InputStream is = conn.getInputStream()) {
            try (BufferedInputStream in = new BufferedInputStream(is); OutputStream fout = Files
                    .newOutputStream(file)) {
                final byte data[] = new byte[8192];
                int count;
                while((count = in.read(data)) > 0) {
                    somethingRead = true;
                    fout.write(data, 0, count);
                }
            }
        } catch(java.io.IOException e) { 
            int httpcode = 999;
            try {
                httpcode = ((HttpURLConnection) conn).getResponseCode();
            } catch(Exception ee) {}
            if( somethingRead && e instanceof java.net.SocketTimeoutException ) ret = 1;
            else if( e instanceof FileNotFoundException && httpcode >= 400 && httpcode < 500 ) ret = 2; 
            else if( httpcode >= 400 && httpcode < 600 ) ret = 3; 
            else if( e instanceof java.net.SocketTimeoutException ) ret = 4; 
            else if( e instanceof java.net.ConnectException ) ret = 5; 
            else if( e instanceof java.net.UnknownHostException ) ret = 6;  
            else throw e;
        }
        return ret;
    }
    
    public static void downloadFiles(String url,String savePath) throws MalformedURLException {
    	
    	URL uri = new URL(url);
        try {
        	
           String fileName = url.substring(url.lastIndexOf("/") + 1,url.length());
           System.out.println("fileName = " + fileName);
           String saveFilePath = savePath + File.separator + fileName;
			org.apache.commons.io.FileUtils.copyURLToFile(uri, new File(saveFilePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          
    }
}
