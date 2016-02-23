package com.mysaasa.core.website.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.google.gson.annotations.Expose;
import com.mysaasa.Simple;;
import com.mysaasa.SimpleImpl;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.security.services.session.AdminSession;
import com.mysaasa.core.website.services.WebsiteService;
import org.apache.wicket.Session;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import static com.google.common.base.Preconditions.checkNotNull;

@Entity
@Table(name = "Website")
public class Website implements Serializable {
	public final static String DIRECT_INTEGRATION_PATH = Simple.get().getConfigPath() + "websites/";
	private static final long serialVersionUID = 2L;

	@Expose
	public int id;
	@Expose
	public String production;
	@Expose
	public String staging;

	@Expose
	public Boolean isVisible = true;
	@Expose
	public Organization organization;
	@Expose
	public List<Domain> domains = new ArrayList();

	public Website() {}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = {CascadeType.DETACH})
	@JoinTable
	public List<Domain> getDomains() {
		return domains;
	}

	public void setDomains(List<Domain> domains) {
		this.domains = domains;
	}

	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "organization")
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@Column(name = "isVisible")
	public void setVisible(boolean isTheme) {
		this.isVisible = isTheme;
	}

	public boolean isVisible() {
		return isVisible;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Website website = (Website) o;

		if (id != website.id)
			return false;
		if (production != null ? !production.equals(website.production) : website.production != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (production != null ? production.hashCode() : 0);
		return result;
	}

	/**
	 * This is the staging production, once persisted
	 *
	 * @return
	 */
	@Column(name = "staging")
	public String getStaging() {
		return staging;
	}

	public void setStaging(String staging) {
		this.staging = staging;
	}

	@Column(name = "production")
	public String getProduction() {
		return production;
	}

	public void setProduction(String production) {
		this.production = production;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public File calculateStagingRoot() {
		return new File(DIRECT_INTEGRATION_PATH + getStaging());
	}

	/*
	 * Get's the File root of this website
	 *
	 * @Returns a reference to the Home Directory of the website
	 */
	public File calculateProductionRoot() {
		return new File(DIRECT_INTEGRATION_PATH + getProduction());
	}

	/**
	 * Converts a File into a URL for this website
	 *
	 * @param file
	 * @return
	 */
	public String getUrl(File file, boolean debug) {
		checkNotNull(file);
		boolean secure = RequestCycle.get().getRequest().getUrl().getProtocol().equalsIgnoreCase("https");
		final String path = file.getAbsolutePath();
		int port = SimpleImpl.getPort();
		int secure_port = SimpleImpl.getSecurePort();

		/**
		 * Illustration
		 * Path         = /opt/simple/websites/www.test.ca/index.html
		 * Pos          = /opt/simple/w <- Find index (lastIndexOf, find the www.test.ca
		 * End          = /index.html
		 *
		 * Compose Result
		 * http://www.test.ca:port/index.html;
		 *
		 * It might use Staging Domain
		 */

		AdminSession prefs = AdminSession.get();
		String domain = getProduction();
		if (prefs.getEnv() == AdminSession.Environment.Staging) {
			domain = staging;
		}

		final int pos = path.lastIndexOf(domain);

		final String end = HostingService.normalizePath(path.substring(pos + domain.length()));

		String url;
		if (debug) {
			String host = RequestCycle.get().getRequest().getClientUrl().getHost();
			Session s = Session.get();
			domain = HostingService.EDITOR_PREFIX + s.getId() + "_" + domain;
		}
		if (secure) {
			url = "https://" + domain + ((port != 443) ? ":" + secure_port : "") + end;

		} else {
			url = "http://" + domain + ((port != 80) ? ":" + port : "") + end;
		}

		return url;
	}

	@Override
	public String toString() {
		return "Website{" + "production='" + production + '\'' + ", staging='" + staging + '\'' + ", id=" + id + ", isVisible=" + isVisible + ", organization=" + organization + ", domains=" + domains + '}';
	}

	public String calculateWebsiteRootAsString() {
		return calculateProductionRoot().getAbsolutePath() + "/";
	}

	public String calculateStagingRootAsString() {
		return calculateStagingRoot().getAbsolutePath() + "/";
	}

	/**
	 * Look at this websites data and choose a default value;
	 *
	 * @return
	 */
	public TemplateFile calculateDefaultFile() {
		Website w = this;
		checkNotNull(w);
		if (w.getId() == 0) {
			w = HostingService.get().findWebsite(w.getProduction());
		}
		if (w == null)
			throw new RuntimeException("Null Website in Website Admin, how does that make sense?");

		File calculateRoot = AdminSession.get().getEnv() == AdminSession.Environment.Staging ? w.calculateStagingRoot() : w.calculateProductionRoot();

		if (calculateRoot == null) {
			throw new RuntimeException("Could not calculate the root of the website");
		}
		File[] files = calculateRoot.listFiles();
		if (w == null || calculateRoot == null || files == null) {
			WebsiteService.get().installGettingStarted(this);
			files = calculateRoot.listFiles();

		}

		for (Object f : files) {
			if (f instanceof File) {
				File file = (File) f;
				//Select index.html or the first html file.
				if (file.getName().equalsIgnoreCase("index.html")) {
					return new TemplateFile(file.getAbsolutePath(), w);

				}
			}
		}

		for (Object f : files) {
			if (f instanceof File) {
				File file = (File) f;
				if (file.isFile()) {
					return new TemplateFile(files[0].getAbsolutePath(), w);
				}
			}

		}

		return null;
	}

	/**
	 * Static helper to get the website for the current request
	 * @return
	 */
	public static Website getCurrent() {
		System.out.println("RequestCycle getCurrent(): " + RequestCycle.get().getRequest().getClientUrl());
		return HostingService.get().findWebsite(RequestCycle.get().getRequest().getClientUrl());
	}

	public TemplateFile calculateProductionFile(Url url) {
		return new TemplateFile(calculateProductionRoot() + "/" + url.getPath());
	}

	public TemplateFile calculateStagingFile(Url url) {
		return new TemplateFile(calculateStagingRoot() + "/" + url.getPath());
	}

}
