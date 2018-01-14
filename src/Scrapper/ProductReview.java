package Scrapper;

public class ProductReview{
	public ProductReview(){}
	public ProductReview(String ASIN,String reviewNo, String user, String userUrl, String commentRate, String comment, String date) {
		super();
		this.reviewNo = reviewNo;
		this.user = user;
		this.userUrl = userUrl;
		this.commentRate = commentRate;
		this.comment = comment;
		this.date = date;
	}
	public ProductReview(String ASIN,String reviewNo, String user, String userUrl, String commentRate, String comment, String date,
			int sentiment) {
		super();
		this.ASIN = ASIN;
		this.reviewNo = reviewNo;
		this.user = user;
		this.userUrl = userUrl;
		this.commentRate = commentRate;
		this.comment = comment;
		this.date = date;
		this.sentiment = sentiment;
	}
	String ASIN;
	public String getReviewNo() {
		return reviewNo;
	}
	public void setReviewNo(String reviewNo) {
		this.reviewNo = reviewNo;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getUserUrl() {
		return userUrl;
	}
	public void setUserUrl(String userUrl) {
		this.userUrl = userUrl;
	}
	public String getCommentRate() {
		return commentRate;
	}
	public void setCommentRate(String commentRate) {
		this.commentRate = commentRate;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	String reviewNo;
	String user;
	String userUrl;
	String commentRate;
	String comment;
	String date;
	int sentiment;
	public int getSentiment() {
		return sentiment;
	}
	public void setSentiment(int sentiment) {
		this.sentiment = sentiment;
	}
}
