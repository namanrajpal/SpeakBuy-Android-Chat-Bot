// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice._2011_08_01.cartitem;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import java.util.List;
import com.amazon.webservices.awsecommerceservice._2011_08_01.cartitem.metadata.KeyValuePair;


public class MetaData implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "KeyValuePair")    
	@Order(value=0)
	public List<KeyValuePair> keyValuePair;	
	
    
}