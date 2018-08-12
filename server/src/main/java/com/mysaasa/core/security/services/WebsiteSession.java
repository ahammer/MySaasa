package com.mysaasa.core.security.services;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * This class represents a basic user session, it contains a cart right now and nothing else Created by adam on 2014-10-20.
 */
public class WebsiteSession implements Serializable {

	// The Users Order ID
	@Expose
	private String orderUid;

	/**
	 * Construct a website session. Creates and links a blank cart
	 */
	public WebsiteSession() {
		createNewCart();
	}

	/**
	 * Create a new Cart. This creates a blank Cart for the Websites session.
	 */
	public void createNewCart() {
		throw new RuntimeException("This needs to move");
		/* Cart cart = new Cart(); cart.setOrganization(Website.getCurrent().getOrganization()); cart = OrderService.getInstance().save(cart); orderUid = cart.getUid(); */
	}

	/**
	 * Retrieves the cart from the database, and returns it.
	 *
	 * @return
	 *
	 * 		public Cart getCart() { return OrderService.getInstance().findCartByUid(orderUid); }
	 * 
	 */

}
