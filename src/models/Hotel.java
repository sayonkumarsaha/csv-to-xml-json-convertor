package models;

import java.util.Map;

/**
 * The model class Hotel having with the attribute definitions of each hotel.
 * 
 * @author Sayon Kumar Saha
 * @since 29.05.2017
 */

public class Hotel {

	private String name;
	private String address;
	private String phone;
	private String contact;
	private String stars;
	private String uri;
	private Map<String, String> kvMap;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Map<String, String> getKvMap() {
		return kvMap;
	}

	public void setKvMap(Map<String, String> kvMap) {
		this.kvMap = kvMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getStars() {
		return stars;
	}

	public void setStars(String stars) {
		this.stars = stars;
	}

}
