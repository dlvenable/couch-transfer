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

package com.allogy.couch.importers;

import com.allogy.couch.filter.DocumentFilter;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.ektorp.CouchDbConnector;
import org.ektorp.Options;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

public class MimeCouchDocumentImporterTest
{
	private CouchDbConnector couchDbConnector;
	private DocumentFilter documentFilter;
	private InputStream inputStream;

	private List<Header> headers;
	private String documentId;
	private String revision;
	private String boundary;
	private long contentLength;

	private String content;

	@Before
	public void setUp()
	{
		couchDbConnector = mock(CouchDbConnector.class);
		documentFilter = mock(DocumentFilter.class);

		documentId = UUID.randomUUID().toString();
		revision = UUID.randomUUID().toString();
		boundary = UUID.randomUUID().toString();
		contentLength = 99;
		content = UUID.randomUUID().toString();

		headers = new ArrayList<Header>();
		headers.add(new BasicHeader("Content-ID", documentId));
		headers.add(new BasicHeader(HttpHeaders.ETAG, revision));
		headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "multipart/related;boundary=\"" + boundary + "\""));
		headers.add(new BasicHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(contentLength)));

		inputStream = createMimeInputStream(headers, content);
	}

	private InputStream createMimeInputStream(Iterable<Header> headers, String content)
	{
		StringBuilder mimeStringBuilder = new StringBuilder();
		for(Header header: headers)
		{
			mimeStringBuilder.append(header.toString());
			mimeStringBuilder.append("\r\n");
		}
		mimeStringBuilder.append("\r\n");

		mimeStringBuilder.append(content);

		return IOUtils.toInputStream(mimeStringBuilder.toString());
	}

	private MimeCouchDocumentImporter createObjectUnderTest()
	{
		return new MimeCouchDocumentImporter(documentFilter);
	}

	@Test
	public void loadFromStream_should_not_update_couch_if_DocumentImportFilter_returns_false() throws IOException
	{
		stub(documentFilter.includeDocument(couchDbConnector, documentId, revision)).toReturn(false);

		createObjectUnderTest().importDocument(couchDbConnector, inputStream);

		verify(couchDbConnector, never()).updateMultipart(anyString(), any(InputStream.class), anyString(), anyLong(), any(Options.class));
	}

	@Test
	public void loadFromStream_should_update_couch_if_DocumentImportFilter_returns_true() throws IOException
	{
		stub(documentFilter.includeDocument(couchDbConnector, documentId, revision)).toReturn(true);

		createObjectUnderTest().importDocument(couchDbConnector, inputStream);

		verify(couchDbConnector).updateMultipart(anyString(), any(InputStream.class), anyString(), anyLong(), any(Options.class));
	}

	@Test
	public void loadFromStream_should_update_couch_with_correct_document_id() throws IOException
	{
		stub(documentFilter.includeDocument(couchDbConnector, documentId, revision)).toReturn(true);

		createObjectUnderTest().importDocument(couchDbConnector, inputStream);

		verify(couchDbConnector).updateMultipart(eq(documentId), any(InputStream.class), anyString(), anyLong(), any(Options.class));
	}

	@Test
	@Ignore
	public void loadFromStream_should_update_couch_with_content_InputStream()
	{}

	@Test
	public void loadFromStream_should_update_couch_with_correct_boundary() throws IOException
	{
		stub(documentFilter.includeDocument(couchDbConnector, documentId, revision)).toReturn(true);

		createObjectUnderTest().importDocument(couchDbConnector, inputStream);

		verify(couchDbConnector).updateMultipart(anyString(), any(InputStream.class), eq(boundary), anyLong(), any(Options.class));
	}

	@Test
	public void loadFromStream_should_update_couch_as_json_stream_if_no_boundary_is_provided() throws IOException
	{
		stub(documentFilter.includeDocument(couchDbConnector, documentId, revision)).toReturn(true);

		headers.remove(Iterables.find(headers, new Predicate<Header>()
		{
			public boolean apply(@Nullable Header header)
			{
				return header.getName().equals(HttpHeaders.CONTENT_TYPE);
			}
		}));
		headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "text/plain;charset=utf-8"));
		inputStream = createMimeInputStream(headers, content);

		createObjectUnderTest().importDocument(couchDbConnector, inputStream);

		verify(couchDbConnector).update(anyString(), any(InputStream.class), anyLong(), any(Options.class));
	}

	@Test
	public void loadFromStream_should_update_couch_with_correct_size() throws IOException
	{
		stub(documentFilter.includeDocument(couchDbConnector, documentId, revision)).toReturn(true);

		createObjectUnderTest().importDocument(couchDbConnector, inputStream);

		verify(couchDbConnector).updateMultipart(anyString(), any(InputStream.class), anyString(), eq(contentLength), any(Options.class));
	}

	@Test
	public void loadFromStream_should_update_couch_with_new_edits_set_to_false() throws IOException
	{
		stub(documentFilter.includeDocument(couchDbConnector, documentId, revision)).toReturn(true);

		createObjectUnderTest().importDocument(couchDbConnector, inputStream);

		ArgumentCaptor<Options> optionsArgumentCaptor = ArgumentCaptor.forClass(Options.class);
		verify(couchDbConnector).updateMultipart(anyString(), any(InputStream.class), anyString(), anyLong(), optionsArgumentCaptor.capture());

		Map<String, String> options = optionsArgumentCaptor.getValue().getOptions();
		assertThat(options.size(), is(1));
		assertThat(options.get("new_edits"), is("false"));
	}
}
