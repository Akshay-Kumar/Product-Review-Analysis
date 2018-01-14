package Scrapper;

import java.sql.Date;
import java.util.ArrayList;

public class Product {
	
	public String toString(){
		return String.format(
				"Product Name : %s\n"
			+	"Total Comments : %s\n"
			+ 	"Category : %s\n"
			+ 	"Price : %s\n",this.getProductName(),this.getTotalComments(),this.getCategory(),this.getPrice());
	}
	public Product(){
	}
	public Product(String ASIN,String productName, int totalComments, String url, String category, String price,
			String specification, ArrayList<ProductReview> review) {
		super();
		this.ASIN = ASIN;
		this.productName = productName;
		this.totalComments = totalComments;
		this.url = url;
		this.category = category;
		this.price = price;
		this.specification = specification;
		this.review = review;
	}
	
	@Override
    public boolean equals(Object obj) {
		boolean flag = false;
        if (this.ASIN == ((Product)obj).ASIN)
            flag=true;
        return flag;
    }
	
	String ASIN;
	public String getASIN() {
		return ASIN;
	}
	public void setASIN(String aSIN) {
		ASIN = aSIN;
	}
	float finalScore;
	public float getFinalScore() {
		return finalScore;
	}
	public void setFinalScore(float finalScore) {
		this.finalScore = finalScore;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getTotalComments() {
		return totalComments;
	}
	public void setTotalComments(int totalComments) {
		this.totalComments = totalComments;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getSpecification() {
		return specification;
	}
	public void setSpecification(String specification) {
		this.specification = specification;
	}
	public ArrayList<ProductReview> getReview() {
		return review;
	}
	public void setReview(ArrayList<ProductReview> review) {
		this.review = review;
	}
	String productName;
	int totalComments;
	String url;
	String category;
	String price;
	String specification;
	ArrayList<ProductReview> review;
}