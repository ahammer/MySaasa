package com.mysaasa.core.event_log.service;

import com.mysaasa.MySaasa;
import com.mysaasa.core.blog.model.BlogPost;
import com.mysaasa.core.blog.services.BlogService;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.core.event_log.model.Event;
import com.mysaasa.core.blog.model.BlogComment;
import org.apache.commons.collections.ListUtils;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by adam on 2014-10-07.
 */
@SimpleService
public class EventQueueService {
	ArrayBlockingQueue<Event> eventQueue = new ArrayBlockingQueue(10000);

	@Inject
	private EntityManager em;

	static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

	public static EventQueueService get() {
		return MySaasa.getInstance().getInjector().getProvider(EventQueueService.class).get();
	}

	public void submitCommand(Event command) {
		//eventQueue.add(command);
		consume(command);
	}

	public Event saveEvent(Event event) {
		checkNotNull(event);
		em.getTransaction().begin();
		Event tracked = em.merge(event);
		em.flush();
		em.getTransaction().commit();
		em.close();
		return tracked;
	}

	/**
	 * Consume the event
	 * @param event
	 */
	private void consume(Event event) {
		switch (event.method) {
		case BlogPostVote:
			VoteOnBlogPost(event);
			break;
		case BlogCommentVote:
			VoteOnComment(event);
			break;
		}
	}

	private void VoteOnBlogPost(Event event) {
		Long id = Long.parseLong(event.getPayload());
		BlogPost bp = BlogService.get().getBlogPostById(id);
		List<Event> past_events = getPastVoteEvents(event);
		if (past_events.size() % 2 == 0) {
			bp.setScore(bp.getScore() + 1);
		} else {
			bp.setScore(bp.getScore() - 1);
		}
		BlogService.get().saveBlogPost(bp);
		event.setConsumed(true);
		saveEvent(event);
	}

	private void VoteOnComment(Event event) {
		Long id = Long.parseLong(event.getPayload());
		BlogComment blogPost = BlogService.get().getBlogCommentById(id);
		List<Event> past_events = getPastVoteEvents(event);
		if (past_events.size() % 2 == 0) {
			blogPost.setScore(blogPost.getScore() + 1);
		} else {
			blogPost.setScore(blogPost.getScore() - 1);
		}
		BlogService.get().saveBlogComment(blogPost);
		event.setConsumed(true);
		saveEvent(event);
	}

	private List<Event> getPastVoteEvents(Event event) {
		List<Event> results = ListUtils.unmodifiableList(em.createQuery("SELECT E FROM Event E WHERE E.user=:user AND E.payload=:payload AND E.method=:method")
				.setParameter("user", event.getUser())
				.setParameter("payload", event.getPayload())
				.setParameter("method", event.getMethod()).getResultList());
		em.close();
		return results;
	}

}
