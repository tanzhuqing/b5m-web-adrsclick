package mogujie.domain;

import java.util.ArrayList;
import java.util.List;

public class Product {
	private String source;
	private String url;
	private String category;
	private String title;
	private String content;
	private List<Attribute> attributes = new ArrayList<Attribute>();
	private String picture;
	private String price;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Product [source=" + source + ", url=" + url + ", category="
				+ category + ", title=" + title + ", content=" + content
				+ ", attributes=" + attributes + ", picture=" + picture
				+ ", price=" + price + "]";
	}

}
