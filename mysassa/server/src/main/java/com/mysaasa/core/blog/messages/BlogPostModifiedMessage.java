package com.mysaasa.core.blog.messages;

import com.mysaasa.messages.AjaxMessage;

/**
 * Broadcast Message that states a BlogTemplateService Post has been saved.
 * Subscribe/Watch for this in order to react when it happens,
 * or trigger it once you save/modify the blog post database for the UI to update
 */
public abstract class BlogPostModifiedMessage extends AjaxMessage {
	public BlogPostModifiedMessage() {
		super();
	}
}
