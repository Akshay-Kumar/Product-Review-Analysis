/*
This class takes url as input of a product and scraps all the details from the page and
stores in mongodb
*/
package Scrapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import conf.Config;
import utils.DateConversion;

public class AmazonReviewScrapper {
	
	public void writeReviewToFile(Product product){
		System.out.println("writing reviews to file...");
		System.out.println(product.toString());
		try(FileWriter fw = new FileWriter("Product.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println(product.toString());
			    //more code
			    out.println(product.getReview());
			    //more code
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
	}
	public  void amazonReviewScrapper(String url) {
		Document doc;
		System.out.println("inside amazon review scrapper "+url);
		int totalNoOfComments=0;
		Product reviewProduct=null;
		try {
			//Setting Proxy
			System.setProperty("http.proxyHost", Config.config().getProperty("proxy_url"));
			System.out.println("Set proxy : "+Config.config().getProperty("proxy_url"));
			doc = Jsoup.connect(url)
					.data("query", "Java")
					.userAgent(AmazonSearchLinkScrapper.userAgent)
					.cookie("auth", "token")
					.timeout(Integer.parseInt(Config.config().getProperty("timeout")))
					.post();

			try{
				Elements comments = doc.getElementsByClass("a-link-emphasis");
				if(!(comments == null)){
					totalNoOfComments = Integer.parseInt(doc.getElementsByClass("a-link-emphasis").last().html().replaceAll("(?<=\\d),(?=\\d)", "").replaceAll("[^0-9?!\\.]","").replaceAll(",", "").trim());
				}
				System.out.println("totalNoOfComments : "+totalNoOfComments);
			}catch(Exception ex){
				try
				{
				System.out.println("Query Url : "+url);	
				String title = doc.getElementsByTag("title").html().toLowerCase();
				System.out.println("Title : "+title.toString());
				if(title.contains("Robot Check".toLowerCase())){
					System.out.println("Robot check has blocked web scrapping.");
					return;
				}
				}
				catch(Exception e){}
				if(doc == null || doc.toString().equals(""))
					return;
			}
			String productName = doc.getElementById("productTitle").html().toLowerCase();
			System.out.println("Product Name : "+productName);
			
			String ASIN;
			ASIN = doc.getElementById("ASIN").val();
			System.out.println("ASIN : "+ASIN);
			
			String category;

			category=doc.getElementById("nav-subnav").select("a").first().select("span").html().replace("&amp;", "&");
			System.out.println("category : "+category);
			String price = null;
			try{
				price=doc.getElementById("priceblock_ourprice").html().replaceAll("(?<=\\d),(?=\\d)", "").replaceAll("[^0-9.?!\\.]","");
			}catch(Exception ex){
				try{
					price=doc.getElementById("priceblock_saleprice").html().replaceAll("(?<=\\d),(?=\\d)", "").replaceAll("[^0-9.?!\\.]","");
				}catch(NullPointerException nu){
					try{
						price=doc.getElementById("olp_feature_div").select("span").last().html().replaceAll("(?<=\\d),(?=\\d)", "").replaceAll("[^0-9.?!\\.]","");
					}catch(Exception e){
						price="0";
					}
				}
			}
			System.out.println("price : "+price);
			String specification =null;
			try{
				specification = Jsoup.parse((doc.getElementById("prodDetails").html())).text();
			}catch(Exception ex){
				try{
					specification = Jsoup.parse((doc.getElementById("techSpecSoftlinesWrap").html())).text();
				}catch(Exception e){
					try{
						specification = Jsoup.parse((doc.getElementById("productDescription").html())).text();
					}catch(Exception e1){
						specification="NA";
					}
				}
			}
			System.out.println("specification : "+specification);
			
			reviewProduct = new Product();
			reviewProduct.setASIN(ASIN);
			reviewProduct.setProductName(productName);
			reviewProduct.setTotalComments(totalNoOfComments);
			reviewProduct.setUrl(url);
			reviewProduct.setCategory(category);
			reviewProduct.setPrice(price);
			reviewProduct.setSpecification(specification);
			
			if(!AmazonSearchLinkScrapper.products.contains(reviewProduct))
			{
				AmazonSearchLinkScrapper.products.add(reviewProduct);
			}
			/*
			reviewProduct.setReview(new ArrayList<ProductReview>());
			//Getting link to open all review 
			url = doc.getElementsByClass("a-link-emphasis").attr("abs:href"); 
			System.out.println("URL : "+url);

				int count=0;
				Pattern pattern = Pattern.compile("profile/(.*?)/");
				
				//Iterating over all the pages
				for(int i=0;i<=totalNoOfComments/10;i++){
					if(!(url.isEmpty() || url.equals("") || (url == null))){
					System.out.println("URL : "+url);
					doc = Jsoup.connect(url)
							.data("query", "Java")
							.userAgent(AmazonSearchLinkScrapper.userAgent)
							.cookie("auth", "token")
							.timeout(Integer.parseInt(Config.config().getProperty("timeout")))
							.post();
					
					Elements element = doc.getElementsByClass("review");
					for(Element temp: element){
						String stars = temp.getElementsByClass("a-icon-alt").html().substring(0, 1);
						String username = temp.getElementsByClass("author").html();
						Matcher matcher = pattern.matcher(temp.getElementsByClass("author").attr("href"));
						String user_profile_url = "NA";
						if (matcher.find()) {
							user_profile_url = matcher.group(1);
						}
						String post_date_raw=temp.getElementsByClass("review-date").html().replace("on ","");
						String post_date = DateConversion.dateParse(post_date_raw);
						java.util.Date date= new java.util.Date();
						int random = 0 +(int)(Math.random()*1000);
						String review_no = new Timestamp(date.getTime()).toString()+random;
						 UUID idOne = UUID.randomUUID();
						//review_no=review_no.replaceAll("\\s","").replaceAll(":","").replaceAll("-","").replaceAll(".", "");
						System.out.println("username : "+username);
						System.out.println("user_profile_url : "+user_profile_url);
						String review = temp.getElementsByClass("review-text").text();
						//int sentiment=NLP.findSentiment(review);
						ProductReview pReview = new ProductReview(ASIN,idOne.toString(), username, user_profile_url, stars, review, post_date);
						reviewProduct.getReview().add(pReview);
						System.out.println("product_review : "+review);
						//System.out.println("Sentiment : " + sentiment);
						count++;
					}
					
					try{
						url = doc.getElementsByClass("a-last").select("a").last().attr("abs:href");
						System.out.println("TEST : "+url);
					}catch(NullPointerException nu){
						try{
							url = "http://www.amazon.in"+doc.getElementsByClass("a-last").select("a").last().attr("href");
						}catch(Exception e){
							System.out.println("NUNUNUNUNUNU @"+url);
							
						}
						
					}
					//next page link

					System.out.println("Current url"+ url);

				}
				
				
			}
			
		System.out.println(count);
		*/

		} catch (Exception e) {
			
			System.out.println("Exception in Amazon review scrapper. @line 203");
			e.printStackTrace();

		}
	}
}
