package com.mysassa.simple.core.messaging.panels;

import com.mysassa.simple.core.messaging.services.MessagingService;
import com.mysassa.simple.core.messaging.model.Message;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Reply in a thread, should always use the ThreadRoot as the Model, because Messages are linear, not trees.
 * Created by Adam on 3/21/2015.
 */
public abstract class MessageReplyPanel extends Panel {
	public MessageReplyPanel(String id, IModel<Message> model) {
		super(id, model);
		add(new MessageReplyForm("ReplyForm", new CompoundPropertyModel(new MessageReplyFormData())));
	}

	public static class MessageReplyFormData implements Serializable {
		String response;

		public MessageReplyFormData() {}

		public String getResponse() {
			return response;
		}

		public void setResponse(String response) {
			this.response = response;
		}
	}

	public class MessageReplyForm extends Form {
		private final AjaxSubmitLink submitLink;
		private final TextArea textArea;

		@Override
		public void renderHead(IHeaderResponse response) {
			//On enter in text-area, submit this form
			response.render(OnDomReadyHeaderItem.forScript("$(function(){\n" + "    $('form > textarea').on('keyup', function(e){\n" + "        if (e.keyCode == 13 && !e.altKey && !e.shiftKey && !e.ctrlKey) {\n" + "            document.getElementById('" + submitLink.getMarkupId() + "').click();\n" + "            document.getElementById('" + textArea.getMarkupId() + "').style.enabled='false';\n" + "}\n" + "    });\n" + "});"));
			//On load/render head, we need to focus on the textarea
			response.render(OnDomReadyHeaderItem.forScript("document.getElementById('" + textArea.getMarkupId() + "').focus()"));
			;
		}

		public MessageReplyForm(String id, final IModel<MessageReplyFormData> model) {
			super(id, model);
			add(textArea = new TextArea("response"));
			add(submitLink = new AjaxSubmitLink("submit") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					super.onSubmit(target, form);

					Message m = (Message) MessageReplyPanel.this.getDefaultModelObject();
					MessagingService.get().replyMessage(m, model.getObject().response);
					model.getObject().response = "";

					target.add(MessageReplyForm.this);
					replyComplete(target);
				}
			});

			setDefaultButton(submitLink);
			setOutputMarkupId(true);
		}
	}

	protected abstract void replyComplete(AjaxRequestTarget target);
}
