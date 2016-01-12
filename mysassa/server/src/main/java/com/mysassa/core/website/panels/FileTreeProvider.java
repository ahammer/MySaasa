package com.mysassa.core.website.panels;

import java.io.File;
import java.util.*;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static com.google.common.base.Preconditions.checkNotNull;

public class FileTreeProvider implements ITreeProvider<File> {

	static final FileSorter FILE_SORTER = new FileSorter();
	/**
	 *
	 */
	private static final long serialVersionUID = -3126895670407361061L;
	private File root;

	public FileTreeProvider(File root) {
		this.root = root;
	}

	@Override
	public void detach() {}

	@Override
	public Iterator<? extends File> getChildren(File arg0) {
		if (arg0 == null || arg0.listFiles() == null)
			throw new NullPointerException("Null List of Files");
		List<File> list = Arrays.asList(arg0.listFiles());
		list.sort(FILE_SORTER);
		return list.iterator();

	}

	@Override
	public Iterator<? extends File> getRoots() {
		checkNotNull(root);
		try {
			List<File> list = Arrays.asList(root.listFiles());
			list.sort(FILE_SORTER);

			return list.iterator();

		} catch (Exception e) {
			return Collections.EMPTY_LIST.iterator();
		}

	}

	@Override
	public boolean hasChildren(File arg0) {
		if (arg0 == null)
			return false;
		if (arg0.list() == null)
			return false;
		return arg0.list().length > 0;
	}

	@Override
	public IModel<File> model(File arg0) {
		return new Model(arg0);
	}

	private static class FileSorter implements Comparator<File> {
		@Override
		public int compare(File o1, File o2) {

			String s1 = o1.getName();
			String s2 = o2.getName();
			// the +1 is to avoid including the '.' in the extension and to avoid exceptions
			// EDIT:
			// We first need to make sure that either both files or neither file
			// has an extension (otherwise we'll end up comparing the extension of one
			// to the start of the other, or else throwing an exception)
			final int s1Dot = s1.lastIndexOf('.');
			final int s2Dot = s2.lastIndexOf('.');
			if ((s1Dot == -1) == (s2Dot == -1)) { // both or neither
				s1 = s1.substring(s1Dot + 1);
				s2 = s2.substring(s2Dot + 1);
				return s1.compareTo(s2);
			} else if (s1Dot == -1) { // only s2 has an extension, so s1 goes first
				return -1;
			} else { // only s1 has an extension, so s1 goes second
				return 1;
			}
		}
	}

	/*
	public void setRoot(File root) {
	    this.root = root;
	}
	*/

}
