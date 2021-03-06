// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice._2011_08_01.topitemset;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import java.util.List;


public class TopItem implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "ASIN")    
	@Order(value=0)
	public String asin;	
	
	@Element(name = "Title")    
	@Order(value=1)
	public String title;	
	
	@Element(name = "DetailPageURL")    
	@Order(value=2)
	public String detailPageURL;	
	
	@Element(name = "ProductGroup")    
	@Order(value=3)
	public String productGroup;	
	
	@Element(name = "Author")    
	@Order(value=4)
	public List<String> author;	
	
	@Element(name = "Artist")    
	@Order(value=5)
	public List<String> artist;	
	
	@Element(name = "Actor")    
	@Order(value=6)
	public List<String> actor;	
	
    
}