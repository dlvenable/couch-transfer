/*
 * Copyright (c) 2012 David Venable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.allogy.couch.filter;

import com.allogy.couch.filter.DocumentFilter;
import com.allogy.couch.filter.ExcludeRevisionExistsDocumentFilter;
import org.ektorp.CouchDbConnector;
import org.ektorp.Revision;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

public class ExcludeRevisionExistsDocumentFilterTest
{
	private CouchDbConnector couchDbConnector;
	private String documentId;
	private String revision;

	@Before
	public void setUp()
	{
		couchDbConnector = mock(CouchDbConnector.class);

		documentId = UUID.randomUUID().toString();
		revision = UUID.randomUUID().toString();
	}

	private DocumentFilter createObjectUnderTest()
	{
		return new ExcludeRevisionExistsDocumentFilter();
	}

	@Test
	public void includeDocument_should_return_false_if_the_document_exists_as_the_current_revision()
	{
		stub(couchDbConnector.contains(documentId)).toReturn(true);

		List<Revision> revisions = new ArrayList<Revision>();
		revisions.add(new Revision(revision, "available"));
		stub(couchDbConnector.getRevisions(documentId)).toReturn(revisions);

		assertThat(createObjectUnderTest().includeDocument(couchDbConnector, documentId, revision), is(false));
	}

	@Test
	public void includeDocument_should_return_false_if_the_document_exists_as_a_different_revision_with_document_revision_in_history()
	{
		stub(couchDbConnector.contains(documentId)).toReturn(true);

		List<Revision> revisions = new ArrayList<Revision>();
		revisions.add(new Revision(UUID.randomUUID().toString(), "available"));
		revisions.add(new Revision(revision, "available"));
		revisions.add(new Revision(UUID.randomUUID().toString(), "available"));
		stub(couchDbConnector.getRevisions(documentId)).toReturn(revisions);

		assertThat(createObjectUnderTest().includeDocument(couchDbConnector, documentId, revision), is(false));
	}

	@Test
	public void includeDocument_should_return_true_if_the_document_exists_as_a_different_revision_without_the_document_revision_in_history()
	{
		stub(couchDbConnector.contains(documentId)).toReturn(true);

		List<Revision> revisions = new ArrayList<Revision>();
		revisions.add(new Revision(UUID.randomUUID().toString(), "available"));
		stub(couchDbConnector.getRevisions(documentId)).toReturn(revisions);

		assertThat(createObjectUnderTest().includeDocument(couchDbConnector, documentId, revision), is(true));
	}

	@Test
	public void includeDocument_should_return_true_if_the_document_does_not_exist()
	{
		stub(couchDbConnector.contains(documentId)).toReturn(false);

		assertThat(createObjectUnderTest().includeDocument(couchDbConnector, documentId, revision), is(true));
	}
}
