package utils;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Scrapper.Product;
import Scrapper.ProductReview;
 
public class AmazonDAL {
	
    public static Connection getConnection(){ 
    	Connection conn=null;
    try { 
    	System.out.println("Registering ms sql driver...");
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    } catch (ClassNotFoundException e) { 
    e.printStackTrace(); 
    } 
    try { 
    	System.out.println("Creating connection...");
		conn = DriverManager.getConnection("jdbc:sqlserver://AKSHAY\\SQLEXPRESS;databaseName=AmazonReview;integratedSecurity=true"); 
    } catch (SQLException e) { 
    e.printStackTrace(); 
    } 
    return conn; 
    }
    
    public void FetchProductReview() throws SQLException, ClassNotFoundException {
    	System.out.println("Calculating product review..");
    	String sql = "select * from(SELECT [ProductName],[productUrl],[true_rating],row_number() over(order by [true_rating] desc) as rating FROM [AmazonReview].[dbo].[ProductReviewFinal])temp where rating=1";
        Connection conn = AmazonDAL.getConnection();
        Statement sta = conn.createStatement();
		ResultSet rs = sta.executeQuery(sql);
		while (rs.next()) {
			System.out.println("ProductName : "+rs.getString("ProductName")+"\nProduct URL : "+rs.getString("productUrl"));
		}
	}
    public void BulkInsertProductReview(ArrayList<Product> productList) throws SQLException, ClassNotFoundException {
        long start = System.currentTimeMillis();
        String sql = "INSERT INTO dbo.ProductReview(ProductId,AmazonUser,UserUrl,CommentRate,Comment,Date) VALUES(?,?,?,?,?,?)";
        int count=0;
        PreparedStatement pstmt = null;
        Connection conn = AmazonDAL.getConnection();
        try {
            pstmt = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            for(Product p : productList){
            	for(ProductReview review : p.getReview()){
            		count++;
                    String productId = p.getASIN();
                    String amazonUser = review.getUser();
                    String userURL = review.getUserUrl();
                    String commentRate = review.getCommentRate();
                    String comment = review.getComment();
                    String date = review.getDate();

                    pstmt.setString(1, productId);
                    pstmt.setString(2, amazonUser);
                    pstmt.setString(3, userURL);
                    pstmt.setString(4, commentRate);
                    pstmt.setString(5, comment);
                    pstmt.setString(6, date);
                    pstmt.addBatch();

                    if(count%100==0){
                        pstmt.executeBatch();
                        conn.commit();
                        conn.close();
                        pstmt.close();
                        conn = AmazonDAL.getConnection();
                        conn.setAutoCommit(false);
                        pstmt = conn.prepareStatement(sql);
                    }
                    System.out.println("insert "+count+"line");
            	}
            }
            if(count%100!=0){
                pstmt.executeBatch();
                conn.commit();
            }
            long end = System.currentTimeMillis();

            System.out.println("Total time spent:"+(end-start));
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
	public void BulkInsertProduct(ArrayList<Product> productList) throws SQLException, ClassNotFoundException {
        long start = System.currentTimeMillis();
        String sql = "INSERT INTO dbo.Product(ProductId,ProductName,TotalComments,ProductUrl,Category,Price,Specification) VALUES(?,?,?,?,?,?,?)";
        int count=0;
        PreparedStatement pstmt = null;
        Connection conn = AmazonDAL.getConnection();
        try {
            pstmt = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            for(Product p : productList){
            	 count++;
                 String productId = p.getASIN();
                 String productName = p.getProductName();
                 int totalComments = p.getTotalComments();
                 String productURL = p.getUrl();
                 String category = p.getCategory();
                 String price = p.getPrice().trim();
                 String specification = p.getSpecification();

                 pstmt.setString(1, productId);
                 pstmt.setString(2, productName);
                 pstmt.setInt(3, totalComments);
                 pstmt.setString(4, productURL);
                 pstmt.setString(5, category);
                 pstmt.setString(6, price);
                 pstmt.setString(7, specification);
                 pstmt.addBatch();

                 if(count%100==0){
                     pstmt.executeBatch();
                     conn.commit();
                     conn.close();
                     pstmt.close();
                     conn = AmazonDAL.getConnection();
                     conn.setAutoCommit(false);
                     pstmt = conn.prepareStatement(sql);
                 }
                 System.out.println("insert "+count+"line");
            }
            if(count%100!=0){
                pstmt.executeBatch();
                conn.commit();
            }
            long end = System.currentTimeMillis();

            System.out.println("Total time spent:"+(end-start));
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
}