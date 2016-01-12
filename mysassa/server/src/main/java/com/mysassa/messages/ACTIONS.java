package com.mysassa.messages;

/**
 * A list of actions supported by AjaxIntent in the core system.
 * Created by administrator on 2014-05-19.
 */
public class ACTIONS {
	public static final String ACTION_REQUEST_SAVE = "/Editor/File/Save";
	public static final String ACTION_WEBSITE_FILE_CLICKED = "/Website/File/Selected";
	public static final String ACTION_WEBSITE_SELECTED = "/Website/Selected";
	public static final String ACTION_WEBSITE_DELETED = "/Website/Deleted";
	public static final String ACTION_WEBSITE_CREATED = "/Website/Created";
	public static final String ACTION_WEBSITE_IFRAME_PAGELOAD = "/Website/IFrame/Pageload"; //The Admin iframe messaging when a page loads
	public static final String ACTION_USER_PREFS_UPDATED = "/User/Prefs/Updated";
	public static final String ACTION_DATA_UPDATED = "/Data/Updated";
	public static final String ACTION_MODULE_CLICKED = "/Module/Selected";
	public static final String ACTION_WEBSITE_FILE_CREATED = "/Website/File/Created";
}
