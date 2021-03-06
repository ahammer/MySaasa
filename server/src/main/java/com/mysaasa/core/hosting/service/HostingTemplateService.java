package com.mysaasa.core.hosting.service;

import com.mysaasa.core.organization.services.OrganizationService;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.users.service.UserService;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.interfaces.ITemplateService;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.core.organization.model.Organization;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mysaasa.MySaasa.getService;

/**
 * The templates need to be able to create websites too
 *
 * Created by adam on 2014-10-23.
 */
@SimpleService
public class HostingTemplateService implements ITemplateService {
	@Override
	public String getTemplateInterfaceName() {
		return "Hosting";
	}

	/**
	 * Checks to see if a domain is available for use, this does not take into account any DNS information or branding Simply whether it's valid or not. The brands will need to enforce their brand on their own.
	 *
	 * @param domain
	 *            domain
	 * @return true if you can use it as a website
	 *
	 */
	public boolean isDomainAvailable(String domain) {
		checkNotNull(domain);
		if (!domain.matches("^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$"))
			return false;
		if (domain.startsWith("."))
			return false;
		if (domain.trim().equals(""))
			return false; // Blank not available
		if (domain.toLowerCase().startsWith("admin."))
			return false; // Admin is reserved

		Website w = getService(HostingService.class).findWebsite(domain);
		return w == null;
	}

	/**
	 * Get's the count of all websites
	 *
	 * Later we can optimize this
	 *
	 * @return count of websites
	 */
	public int getWebsiteCount() {
		return getService(HostingService.class).getWebsites().size();
	}

	/**
	 * This will set up a Organization and website + user
	 * 
	 * @param domain
	 *            Used as the Organization name, User name and Domain name
	 * @param password
	 *            the password
	 * @param contact_email
	 *            How to contact this user
	 * @param product_sku
	 *            Optional sku
	 * @return the website
	 */
	public Website createWebsite(String domain, String password, String contact_email, String product_sku) {

		HostingService hostingService = getService(HostingService.class);
		UserService userService = UserService.get();
		OrganizationService organizationService = OrganizationService.get();

		Organization o = new Organization(domain);
		o.getContactInfo().setEmail(contact_email);
		o = organizationService.saveOrganization(o);

		User u = new User(domain, password, o);
		u.getContactInfo().setEmail(contact_email);
		u = userService.saveUser(u);

		/* Cart c = null; if (product_sku != null && !product_sku.trim().equalsIgnoreCase("")) { Product product = InventoryService.getInstance().getProductBySku(product_sku); if (product != null) { c = new Cart(); c.setUser(u); c.setOrganization(Website.getCurrent().organization); c = c.addProduct(product); c.setStatus(Cart.CartStatus.Active); Payment p = new Payment(); p.setCurrency(product.getPricing().getInstance(0).currency.getCurrency()); p.setValue(BigDecimal.valueOf(20)); p.setValidated(true); p.setSuccessful(true); p.setChannel("$20 Signup Promotion"); c = c.save(); c = c.logPayment(p); } }
		 * 
		 * Session.getInstance().bind(); SessionService.getInstance().registerUser(Session.getInstance(), u);
		 * 
		 * //Create Website Website website = new Website(); website.setOrganization(o); website.setProduction(domain.toLowerCase()); website.setStaging(UUID.randomUUID().toString().substring(0, 3) + domain.toLowerCase()); hostingService.saveWebsite(website);
		 * 
		 * website.calculateProductionRoot().mkdirs(); website.calculateStagingRoot().mkdirs();
		 * 
		 * WebsiteService.getInstance().installGettingStarted(website);
		 * 
		 * 
		 * //Demo Product Product p = new Product(); p.setOrganization(o); p.addCategory("Donation"); p.setName("Donate $1"); p.setSku("DONATE-1CAD"); ArrayList prices = new ArrayList<Pricing>(); prices.add(new Pricing(null, CurrencyOptions.CAD, BigDecimal.ONE)); p.setPricing(prices); InventoryService.getInstance().saveProduct(p);
		 * 
		 * p = new Product(); p.setOrganization(o); p.addCategory("Donation"); p.setName("Donate $10"); p.setSku("DONATE-10CAD"); prices = new ArrayList<Pricing>(); prices.add(new Pricing(null, CurrencyOptions.CAD, BigDecimal.TEN)); p.setPricing(prices); InventoryService.getInstance().saveProduct(p);
		 * 
		 * p = new Product(); p.setOrganization(o); p.addCategory("Donation"); p.setName("Donate $50"); p.setSku("DONATE-50CAD"); prices = new ArrayList<Pricing>(); prices.add(new Pricing(null, CurrencyOptions.CAD, BigDecimal.valueOf(50))); p.setPricing(prices); InventoryService.getInstance().saveProduct(p);
		 * 
		 * p = new Product(); p.setOrganization(o); p.addCategory("Donation"); p.setName("Donate $500"); p.setSku("DONATE-500CAD"); prices = new ArrayList<Pricing>(); prices.add(new Pricing(null, CurrencyOptions.CAD, BigDecimal.valueOf(500))); p.setPricing(prices); InventoryService.getInstance().saveProduct(p);
		 * 
		 * //Demo Blogpost BlogPost post = new BlogPost(); post.setOrganization(o); post.setAuthor(u); post.setTitle("Test Blog Post"); post.setSubtitle("Installed with love by the setup program"); post.setSummary("This summarizes that there is no real information here. This is a test blog post"); post.setBody("Welcome to your blog, you can start editing and posting immediately"); post.addCategory("Blog"); BlogService.getInstance().saveBlogPost(post); o.save(); u.setOrganization(o); o.setSubscription(c); return website; */

		return null;
	}
}
