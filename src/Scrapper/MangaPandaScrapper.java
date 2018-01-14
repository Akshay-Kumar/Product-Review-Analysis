package Scrapper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import conf.Config;

public class MangaPandaScrapper {
	private static final String IMAGE_HOME = "MangaPanda";
	public static ArrayList<Product> products = new ArrayList<Product>();
	public static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0";
	
	public static String storeImageIntoFS(String imageUrl, String fileName, String relativePath) {
	    String imagePath = null;
	    try {
	        //byte[] bytes = Jsoup.connect(imageUrl).ignoreContentType(true).execute().bodyAsBytes();
	        
	        byte[] bytes = Jsoup.connect(imageUrl)
					.data("query", "Java")
					.userAgent(userAgent)
					.cookie("auth", "token")
					.ignoreContentType(true)
					.execute().bodyAsBytes();
	        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(bytes);
	        String rootTargetDirectory = IMAGE_HOME + "/"+relativePath;
	        imagePath = rootTargetDirectory + "/"+fileName;
	        saveByteBufferImage(buffer, rootTargetDirectory, fileName);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return imagePath;
	}

	public static void saveByteBufferImage(java.nio.ByteBuffer imageDataBytes, String rootTargetDirectory, String savedFileName) {
	   String uploadInputFile = rootTargetDirectory + "/"+savedFileName;

	   File rootTargetDir = new File(rootTargetDirectory);
	   if (!rootTargetDir.exists()) {
	       boolean created = rootTargetDir.mkdirs();
	       if (!created) {
	           System.out.println("Error while creating directory for location- "+rootTargetDirectory);
	       }
	   }
	   String[] fileNameParts = savedFileName.split("\\.");
	   String format = fileNameParts[fileNameParts.length-1];

	   File file = new File(uploadInputFile);
	   BufferedImage bufferedImage;

	   InputStream in = new ByteArrayInputStream(imageDataBytes.array());
	   try {
	       bufferedImage = ImageIO.read(in);
	       ImageIO.write(bufferedImage, format, file);
	   } catch (IOException e) {
	       e.printStackTrace();
	   }
	}
	
	public  void mangaQuery(String keyword){
		Document doc;
		String url ="http://www.mangapanda.com/"+keyword;

		try {
			System.out.println("Mangapanda Search Link Scrapper.......BEGIN");
			//Setting Proxy
			System.setProperty("http.proxyHost", Config.config().getProperty("proxy_url"));
			System.setProperty("http.proxyPort", Config.config().getProperty("proxy_port"));

			doc = Jsoup.connect(url)
					.data("query", "Java")
					.userAgent(userAgent)
					.cookie("auth", "token")
					.timeout(Integer.parseInt(Config.config().getProperty("timeout")))
					.post();	    
			ArrayList<String> url_list = new ArrayList<>();
			String url1=null;
			Integer totalPage = 0;

			try{

				totalPage = Integer.parseInt(doc.getElementsByClass("chico_manga").html());
				System.out.println("Total pages : "+totalPage);
			}catch(Exception ex){
				//Nothing to do just to avoid null pointer exception
				System.out.println("Exception | Total pages : "+totalPage);
				System.out.println(doc.toString());
			}
			for(int i=0;i<=4;i++){
				System.out.println("Search_link_scrapper_url: "+url);
				doc = Jsoup.connect(url)
						.data("query", "Java")
						.userAgent(userAgent)
						.cookie("auth", "token")
						.timeout(Integer.parseInt(Config.config().getProperty("timeout")))
						.post();		    
				Elements elements = doc.getElementById("chapterlist").getElementsByAttribute("href");
				for(Element element:elements){
					if(!(url1=element.attr("href").toString()).isEmpty()){
						if(url1.substring(0, 1).equals("/")){
							url1="http://www.mangapanda.com"+url1;
						}
						url_list.add(url1);
					}
				}

//				try{
//					url = doc.getElementById("pagnNextLink").attr("href");
//					if(url.substring(0, 1).equals("/")){
//						url="http://www.amazon.in"+url;
//					}
//				}catch(Exception ex){
//					if(!url_list.isEmpty()){
//						System.out.println("Amazon Link Scrapping finished found "+url_list.size()+" links");
//					}
//					ex.printStackTrace();
//					break;
//				}
			}
		} catch (IOException e) {
			//Nothing to do just to avoid null pointer exceptio
			e.printStackTrace();
			this.mangaQuery(keyword);
		}

	}
	public static ArrayList<String> imageUrl = new ArrayList<String>();
	public static int index=0;
	Document doc;
	String urlString="";
	public void downloadImages(String url) {
		index++;
		if(url.substring(0, 1).equals("/")){
			url="http://www.mangapanda.com"+url;
		}
		if(!url.isEmpty()){
				System.out.println("url : "+url);
				try
				{
				doc = Jsoup.connect(url)
						.data("query", "Java")
						.userAgent(userAgent)
						.cookie("auth", "token")
						.timeout(Integer.parseInt(Config.config().getProperty("timeout")))
						.post();		    
				Element elementsImg = doc.getElementById("imgholder").getElementById("img");
				String ImageUrl = elementsImg.attr("src").trim();
				urlString = ImageUrl;
				System.out.println("ImageUrl : "+ImageUrl);
				imageUrl.add(urlString);
				//Go to next page
				Element elementsNext = doc.getElementsByClass("next").get(0).getElementsByAttribute("href").get(0);
				String nextPageUrl = elementsNext.attr("href");
				System.out.println("nextPageUrl : "+nextPageUrl);
				if(!nextPageUrl.isEmpty()){
					downloadImages(nextPageUrl);
				}
				}
				catch(Exception ex){
					return;
				}
		}
		else{
			System.out.println("Links are empty");
		}
	}
	
	public void saveImage(){
		int index=0;
		for(String url : imageUrl){
			String filename = url.substring(url.lastIndexOf("/")+1);
			System.out.println("Saving file : "+filename);
			index++;
			String relativePath = filename.substring(0,filename.lastIndexOf("-"));
			storeImageIntoFS(url,filename,relativePath);
		}
		System.out.println("Finished saving images : "+index);
	}
	
	public void saveImage(String url){
		String filename = url.substring(url.lastIndexOf("/")+1);
		System.out.println("Saving file : "+filename);
		String relativePath = filename.substring(0,filename.lastIndexOf("-"));
		storeImageIntoFS(url,filename,relativePath);
		System.out.println("Finished saving images");
	}
	ArrayList<String> list = new ArrayList<String>();
//	public void Threading(){
//		int index=0;
//		
//		for(String uri : imageUrl){ 
//			list.add(uri);
//			if(index%10==0){
//				MangaPandaScrapper s = new MangaPandaScrapper();
//				s.list = list;
//				MyThread worker = new MyThread(s);
//				worker.start();
//				// wait for threads to end
//		         try {
//		        	 worker.join();
//		      }catch( Exception e) {
//		         System.out.println("Interrupted");
//		      }
//			}
//			index++;
//		}
//	}
	
	public void Threading(){
		
		         try {
		        	 
		        	 MyThread worker = new MyThread(this);
						worker.start();
		      }catch( Exception e) {
		         System.out.println("Interrupted");
		      }
		}
}

class MyThread extends Thread {
	private final ArrayList<String> url;
	private final MangaPandaScrapper scrapper;
	MyThread(MangaPandaScrapper scrapper) {
		this.url = MangaPandaScrapper.imageUrl;
		this.scrapper = scrapper;
	}

	@Override
	public void run() {
		//mangaSearchLinkScrapper.mangaQuery(keyword);
		try {
			for(String ur : url)
			scrapper.saveImage(ur);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
