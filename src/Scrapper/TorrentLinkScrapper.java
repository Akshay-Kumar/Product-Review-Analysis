/*
 This class get all the links available in search page of amazon.in
 
 These links are then passes on to  "AmazonReviewScrapper" class to get all the details about that product
*/
package Scrapper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import conf.Config;
import utils.AmazonDAL;
public class TorrentLinkScrapper {
	public static ArrayList<Product> products = new ArrayList<Product>();
	public static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:53.0) Gecko/20100101 Firefox/53.0";
	AmazonDAL dal = new AmazonDAL();
	public  void amazonQuery(String keyword){
		Document doc;
		//String india = "https://www.amazon.in";
		String usa = "https://yts.ag/movie/";
		//String url =india+"/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords="+keyword;
		String url=usa+keyword;
		String torrentDirectory=System.getProperty("user.home")+"\\Downloads\\Torrents\\";
		
		try {
			System.out.println("Torrent Search Link Scrapper.......BEGIN");
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
			Elements elements;
			try{
				elements = doc.getElementsByAttribute("href");
				for(Element element:elements){
					if(element.text().trim().equals("720p")){
						String torrentUrl=element.attr("href");
						url_list.add(torrentUrl);
						System.out.println(torrentUrl);
						HttpDownloadUtility.downloadFile(torrentUrl,torrentDirectory);
					}
				}
			}catch(Exception ex){
				//Nothing to do just to avoid null pointer exception
				System.out.println(doc.toString());
				ex.printStackTrace();
			}
		} catch (Exception e) {
			//Nothing to do just to avoid null pointer exceptio
			e.printStackTrace();
		}

	}
	
}
