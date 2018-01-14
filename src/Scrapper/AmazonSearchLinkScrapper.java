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
public class AmazonSearchLinkScrapper {
	public static ArrayList<Product> products = new ArrayList<Product>();
	public static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:53.0) Gecko/20100101 Firefox/53.0";
	AmazonDAL dal = new AmazonDAL();
	public  void amazonQuery(String keyword){
		//NLP.init();
		System.out.println("Product count in ArrayList : "+products.size());
		if(!products.isEmpty()){
			products.clear();
		}
		Document doc;
		String india = "https://www.amazon.in";
		String usa = "https://www.amazon.com";
		//String url =india+"/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords="+keyword;
		String url=usa+"/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords="+keyword;
		
		
		try {
			System.out.println("Amazon Search Link Scrapper.......BEGIN");
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

				totalPage = Integer.parseInt(doc.getElementsByClass("pagnDisabled").html());
			}catch(Exception ex){
				//Nothing to do just to avoid null pointer exception
				System.out.println("Total pages : "+totalPage);
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
				Elements elements = doc.getElementsByClass("s-item-container");
				for(Element element:elements){
					if(!(url1=element.getElementsByClass("a-link-normal").attr("href").toString()).isEmpty()){
						if(url1.substring(0, 1).equals("/")){
							url1=usa+url1;
						}
						url_list.add(url1);
					}
				}

				try{
					url = doc.getElementById("pagnNextLink").attr("href");
					if(url.substring(0, 1).equals("/")){
						url=usa+url;
					}
				}catch(Exception ex){
					if(!url_list.isEmpty()){
						System.out.println("Amazon Link Scrapping finished found "+url_list.size()+" links");
					}
					ex.printStackTrace();
					break;
				}
			}
			if(!url_list.isEmpty()){
				System.out.println("Amazon Link Scrapping finished found "+url_list.size()+" links");
				for(String uri:url_list){
					System.out.println(uri);
					AmazonReviewScrapper amazonReviewScrapper = new AmazonReviewScrapper();
					amazonReviewScrapper.amazonReviewScrapper(uri);
				}
				System.out.println(url_list);
				
				try {
					System.out.println("Started inserting products");
					dal.BulkInsertProduct(products);
					System.out.println("Finished inserting products");
					//System.out.println("Started inserting reviews");
					//dal.BulkInsertProductReview(products);
					//System.out.println("Finished inserting reviews");
					//dal.FetchProductReview();
				} catch (ClassNotFoundException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				reviewProduct(products);
				ArrayList<Product> bestChoice = calculateBestProduct(products);
				System.out.print("\n");
				System.out.print("*************************************************************************");
				System.out.print("\n");
				System.out.print("*");
				System.out.print("\n");
				System.out.print("*			  The best product as per the algorithm");
				System.out.print("\n");
				System.out.print("*");
				System.out.print("\n");
				System.out.print("*************************************************************************");
				System.out.print("\n");
				for(Product buy : bestChoice){
					String data = String.format(
							"Product Name : %s\n"
						+ 	"Price : %s\n"
						+ 	"Product Url : %s\n"
						+ 	"Final Score : %f\n",
						buy.getProductName(),
						buy.getPrice(),
						buy.getUrl(),
						buy.getFinalScore());
					System.out.println(data);
				}
				*/
			}
			else{
				System.out.println("Amazon Link Scrapping finished found "+url_list.size()+" links");
			}
		} catch (IOException e) {
			//Nothing to do just to avoid null pointer exceptio
			e.printStackTrace();
			this.amazonQuery(keyword);
		}

	}
	
	public void reviewProduct(ArrayList<Product> products){
		System.out.println("Reviewing and looking for best products for you...");
		float finalScore;
		int score;
		int index;
		for(Product p:products){
			score=0;
			index=0;
			finalScore=0;
			for(ProductReview pReview : p.getReview()){
				score += pReview.getSentiment();
				index++;
				System.out.println();
				System.out.println("Product Name : "+p.getProductName());
				System.out.println("Product Url : "+p.getUrl());
				System.out.println("Product Price : "+p.getPrice());
				System.out.println("Review Comment : "+pReview.getComment());
				System.out.println("Sentiment : "+pReview.getSentiment()+" | "+"Rating : "+pReview.getCommentRate());
				System.out.println();
			}
			if(!(index==0)){
				finalScore = (float)((float)score/(float)index);	
			}
			System.out.println("score : "+score+" index : "+index);
			System.out.println("Final score : "+finalScore);
			p.setFinalScore(finalScore);
		}
	}
	
	public ArrayList<Product> calculateBestProduct(ArrayList<Product> products){
		System.out.println("Sit back and relax getting best product for you...");
		float maxScore=0;
		ArrayList<Product> bestProducts = new ArrayList<Product>();
		for(Product p:products){
			float score = p.getFinalScore();
			if(score>=maxScore){
				maxScore = score;
			}
		}
		System.out.println("Max Score : "+maxScore);
		for(Product p:products){
			if(p.getFinalScore() == maxScore){
				bestProducts.add(p);
				System.out.println("Adding... "+p.getProductName()+". With total score : "+p.getFinalScore());
			}
		}
		return bestProducts;
	}
	
}
