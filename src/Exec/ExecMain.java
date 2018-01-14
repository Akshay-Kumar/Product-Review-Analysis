/*
This is the main file the "keyword" value is the string which is fired on the e-commerce website search box.

There are two thread one for flipkart.com and another for amazon.in, currently only amazon.in is working.

When the amazon_scrapper_thread is executed it first get all the links for the products in search result by executing
"AmazonSearchLinkScrapper" class which takes keyword as the parameter
*/
package Exec;
import java.io.IOException;

import Scrapper.AmazonSearchLinkScrapper;
import Scrapper.MangaPandaScrapper;
import Scrapper.TorrentLinkScrapper;
import utils.AmazonDAL;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ExecMain {
	private static final int MYTHREADS = 30;
	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
		final String keyword = 
				"kong-skull-island-2017;"
				+ "beauty-and-the-beast-2017;"
				+ "a-walk-to-remember-2002;"
				+ "zootopia-2016;"
				+ "rio-2-2014;"
				+ "extinction-2015;"
				+ "timeline-2003";
		final int threadSize = 20;
		try {

//			Thread amazon_scrapper_thread = new Thread(){
//				public void run(){
//					AmazonSearchLinkScrapper amazonSearchLinkScrapper = new AmazonSearchLinkScrapper();
//					amazonSearchLinkScrapper.amazonQuery(keyword);
//				} 
//			};
			Thread amazon_scrapper_thread = new Thread(){
				public void run(){
					TorrentLinkScrapper amazonSearchLinkScrapper = new TorrentLinkScrapper();
					String[] keywords = keyword.split(";");
					for(String key : keywords){
						amazonSearchLinkScrapper.amazonQuery(key);
					}
				} 
			};
//			Thread flipkart_scrapper_thread = new Thread(){
//				public void run(){
//					FlipkartSearchLinkScrapper flipkartSearchLinkScrapper = new FlipkartSearchLinkScrapper();
//					flipkartSearchLinkScrapper.flipkartquery(keyword);
//				} 
//			};
//
//			flipkart_scrapper_thread.start();
			amazon_scrapper_thread.start();
			
//			Thread manga_scrapper_thread = new Thread(){
//				public void run(){
//					MangaPandaScrapper mangaSearchLinkScrapper = new MangaPandaScrapper();
//					//mangaSearchLinkScrapper.mangaQuery(keyword);
//					try {
//						mangaSearchLinkScrapper.downloadImages(keyword);
//						mangaSearchLinkScrapper.saveImage();
//					} catch (NumberFormatException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				} 
//			};	
//			
//			//manga_scrapper_thread.start();
//			MangaPandaScrapper mangaSearchLinkScrapper = new MangaPandaScrapper();
//			//mangaSearchLinkScrapper.mangaQuery(keyword);
//			try {
//				mangaSearchLinkScrapper.downloadImages(keyword);
//				mangaSearchLinkScrapper.Threading();
//			} catch (NumberFormatException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
