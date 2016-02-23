package com.mysaasa.core.media.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLConnection;
import java.rmi.server.UID;

import javax.imageio.ImageIO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

import com.mysaasa.Simple;;
import com.mysaasa.core.website.templating.TemplatedSiteRequestHandler;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.request.resource.DynamicImageResource;

/**
 * The Media object represents a file, This may be subclassed into Image
 *
 * The UID is generated as a file system bucket for a file.
 */
@Entity
@Table(name = "Media")
public class Media implements Serializable {
	private static final long serialVersionUID = 1L;

	@Expose
	public long id;
	@Expose
	private Type type;
	@Expose
	private Format format;
	@Expose
	private String filename;
	@Expose
	private String uid;

	@Column(name = "uid")
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * Get's the original file.
	 * @return File location of the uploaded file in it's original form
	 */
	public File calculateOriginalFile() {
		File f = new File(Simple.get().getConfigPath() + "media/" + uid + "/" + getFilename());
		if (f.exists())
			return f;

		//Create directories and return blank file handlse
		new File(Simple.get().getConfigPath() + "media/" + uid).mkdirs();
		return f;
	}

	/**
	 * Get's the URL for the original image as stored on the server.
	 * @return A URL that can access this media.
	 */
	public String calculateMediaFolderPath() {
		return "/" + TemplatedSiteRequestHandler.CLIENT_URL_INTEGRATION + "/" + uid;
	}

	/**
	public static void main(String[] args) {
	
	    try {
	
	
	        BufferedImage resizeImageJpg = resizeImage(originalImage, type, 100, 100);
	        ImageIO.write(resizeImageJpg, "jpg", new File("c:\\images\\testresized.jpg")); //change path where you want it saved
	
	    } catch (IOException e) {
	        System.out.println(e.getMessage());
	    }
	
	}**/

	private static BufferedImage resizeImage(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		float ratio = (float) IMG_HEIGHT / originalImage.getHeight();
		float ratio_width = (float) IMG_WIDTH / originalImage.getWidth();

		int calc_width = (int) (originalImage.getWidth() * ratio);
		int calc_height = (int) (originalImage.getHeight() * ratio);
		if (calc_width < IMG_WIDTH) {
			calc_width = (int) (originalImage.getWidth() * ratio_width);
			calc_height = (int) (originalImage.getHeight() * ratio_width);
		}

		Graphics2D g = resizedImage.createGraphics();
		int margin_x = calc_width - IMG_WIDTH;
		int margin_y = calc_height - IMG_HEIGHT;

		g.drawImage(originalImage, -margin_x / 2, -margin_y / 2, IMG_WIDTH + margin_x, IMG_HEIGHT + margin_y, null);
		g.dispose();

		return resizedImage;
	}

	public File calculateScaledFile(int width, int height) {
		try {
			String path = Simple.get().getConfigPath() + "media/" + uid + "/" + getFilename() + "_" + width + "_" + height;
			File f = new File(path);
			//if (f.exists()) return f;
			BufferedImage originalImage = null;//change path to where file is located
			originalImage = ImageIO.read(calculateOriginalFile());
			int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			BufferedImage resizeImageJpg = resizeImage(originalImage, type, width, height);
			ImageIO.write(resizeImageJpg, "jpg", f); //change path where you want it saved
			//Scale and save
			return f;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Supported media types, currently images only.
	 */
	public static enum Type {
		IMAGE
	}

	public static enum Size {
		THUMBNAIL;

		public int getMaxSize() {
			switch (this) {
			case THUMBNAIL:
				return 100;
			default:
				return 1000;
			}
		}
	};

	/**
	 * File format information
	 */
	public static enum Format {
		JPEG, PNG, GIF, UNKNOWN, HTML, JS, CSS;
		public String getMimeType() {
			switch (this) {
			case JPEG:
				return "image/jpeg";
			case PNG:
				return "image/png";
			case GIF:
				return "image/gif";
			case HTML:
				return "text/html";
			case JS:
				return "text/javascript";
			case CSS:
				return "text/css";
			case UNKNOWN:
			default:
				return "application/octet-stream";
			}
		}

		public static Format fromFile(File f) throws IOException {
			if (f == null)
				throw new NullPointerException("Null? Are you serious??");
			String mime = URLConnection.guessContentTypeFromName(f.getName());

			if (f.getName().endsWith(".css"))
				mime = Format.CSS.getMimeType();
			for (Format fmt : Format.values()) {
				if (fmt.getMimeType().equals(mime))
					return fmt;
			}
			return UNKNOWN;
		}
	}

	/**
	 * Construct a blank media object
	 *
	 * put sensible non-null defaults, so serializable will show format.
	 */
	public Media() {
		uid = new UID().toString().replace("-", "").replace(":", "");
		type = Type.IMAGE;
		format = Format.UNKNOWN;
		filename = "";
		uid = "";
	}

	/**
	 * Creates a Media Object from a File Upload. This should register it in the database and save it to the file system
	 * @param fu
	 * @throws IOException
	 */
	public Media(FileUpload fu) throws IOException {
		uid = new UID().toString().replace("-", "").replace(":", "");
		filename = fu.getClientFileName();
		File f = calculateOriginalFile();
		try {
			fu.writeTo(f);
		} catch (Exception e) {
			throw new IOException(e);
		}
		format = Format.fromFile(f);
		type = Type.IMAGE;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Column
	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	@Column
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 *
	 * @return a URL that is thumbnail friendly
	 */
	public String calculateThumbnailUrl() {
		return "/" + TemplatedSiteRequestHandler.CLIENT_URL_INTEGRATION + "/" + uid + "/" + Size.THUMBNAIL.name();
	}

	/**
	 * This
	 * @param size The Size enum that you are requesting.
	 * @return A DynamicImageResource, for wicket to consume with Image fields.
	 */
	public MediaImageResource calculateImageData(final Size size) {
		return new MediaImageResource(size);
	}

	public class MediaImageResource extends DynamicImageResource {
		private final Size size;

		public MediaImageResource(Size size) {
			this.size = size;
		}

		@Override
		public byte[] getImageData(Attributes attributes) throws IllegalStateException {
			setFormat(Media.this.getFormat().getMimeType());
			//();
			File originalFile = calculateOriginalFile();
			try {
				BufferedImage img = ImageIO.read(originalFile);
				BufferedImage scaled = calculateScaledImage(img, size);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(scaled, "png", baos);
				baos.flush();
				return baos.toByteArray();

			} catch (IOException e) {
				throw new IllegalStateException(e) {
					@Override
					public String toString() {
						return super.toString() + " Unable to access original file";
					}
				};
			}
		}

		private BufferedImage calculateScaledImage(BufferedImage img, Size s) {
			boolean wide = img.getWidth() > img.getHeight();
			int new_width;
			int new_height;
			BufferedImage returnImage;

			if (wide) {
				new_height = (int) (img.getHeight() * ((double) s.getMaxSize() / img.getWidth()));
				new_width = s.getMaxSize();
				returnImage = new BufferedImage(new_width, new_height, Image.SCALE_SMOOTH);
			} else {
				new_width = (int) (img.getWidth() * ((double) s.getMaxSize() / img.getHeight()));
				new_height = s.getMaxSize();
				returnImage = new BufferedImage(new_width, new_height, Image.SCALE_SMOOTH);
			}
			Image scaledImage = img.getScaledInstance(new_width, new_height, Image.SCALE_SMOOTH);
			returnImage.getGraphics().drawImage(scaledImage, 0, 0, Color.BLACK, null);
			return returnImage;
		}
	}

	@Override
	public String toString() {
		return "Media{" + "id=" + id + ", type=" + type + ", format=" + format + ", filename='" + filename + '\'' + ", uid='" + uid + '\'' + '}';
	}
}
