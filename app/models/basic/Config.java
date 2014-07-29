package models.basic;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
@Table(name="configs")
public class Config extends Model{
	
	/**
	 * Logger error types
	 */
	@Transient
	public static final int LOGGER_ERROR = 1;
	/**
	 * Logger error types
	 */
	@Transient
	public static final int LOGGER_INFO = 2;
	/**
	 * Logger error types
	 */
	@Transient
	public static final int LOGGER_WARN = 3;
	/**
	 * Logger error types
	 */
	@Transient
	public static final int LOGGER_DEBUG = 4;
	/**
	 * Logger error types
	 */
	@Transient
	public static final int LOGGER_TRACE = 5;
	
	@Transient
	public static final String ERROR_KEY = "error";
	@Transient
	public static final String DESCRIPTION_KEY = "description";
	@Transient
	public static final String RESPONSE_KEY = "response";
	@Transient
	public static final String SIZE_KEY = "size";
	@Transient
	public static final String MOD_KEY = "modify";
    @Transient
    public static final String EXCEPTION_KEY = "description";
	
	@Transient
	public static final int DEFAULT_PAGE_SIZE = 500;

    @Transient
    public static final int PARAMETERS_ERROR = 666;

	@Id
	private Long idConfig;
	@Required
	@MaxLength(50)
	private String key;
	@Required
	private String value;
	@Required
	private String description;
	
	public static Model.Finder<Long, Config> finder = new  
			Model.Finder<Long, Config>(Long.class, Config.class);
	
	public Long getIdConfig() {
		return idConfig;
	}
	public void setIdConfig(Long idConfig) {
		this.idConfig = idConfig;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public static Config getConfigByKey(String key){
		return finder.where().eq("key", key).findUnique();
	}
	
	public static String getString(String key){
		Config c = finder.where().eq("key", key).findUnique();
		return c.getValue();
	}
	
	public static Long getLong(String key){
		Config c = finder.where().eq("key", key).findUnique();
		return Long.parseLong(c.getValue());
	}
	
	public static String[] getStringArray(String key, String separator){
		Config c = finder.where().eq("key", key).findUnique();
		return c.getValue().split(separator);
	}
	
	public static int getInt(String key){
		Config c = finder.where().eq("key", key).findUnique();
		return Integer.parseInt(c.getValue());
	}
	
	/**
	 * Metodo para obtener el nombre del host actual
	 * @return nombre del host actual
	 */
	public static String getDaemonHost() {
		Config c = finder.where().eq("key","pmc-url").findUnique();
		return c.getValue();
	}
	
	/**
	 * Metodo para obtener el nombre del host actual
	 * @return nombre del host actual
	 */
	public static String getHost() {
		Config c = finder.where().eq("key","kraken-url").findUnique();
		return c.getValue();
	}
	
	/**
	 * Metodo para obtener el nombre del host actual
	 * @return nombre del host actual
	 */
	public static String getFervidHost() {
		Config c = finder.where().eq("key","fervid-url").findUnique();
		return c.getValue();
	}

}
