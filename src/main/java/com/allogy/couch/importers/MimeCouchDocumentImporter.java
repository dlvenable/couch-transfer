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
import com.allogy.couch.importers.command.BasicImportCommand;
import com.allogy.couch.importers.command.CouchImporter;
import com.allogy.couch.importers.command.ImmediateCouchImporter;
import com.allogy.couch.importers.command.ImportCommand;
import com.allogy.io.UnCloseableInputStream;
import com.allogy.mime.MimeStreamingReader;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.ektorp.CouchDbConnector;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

public class MimeCouchDocumentImporter implements CouchDocumentImporter
{
	private DocumentFilter documentFilter;
    private CouchImporter couchImporter;

	public MimeCouchDocumentImporter(DocumentFilter documentFilter)
	{
        this(documentFilter, new ImmediateCouchImporter());
	}

    public MimeCouchDocumentImporter(DocumentFilter documentFilter, CouchImporter couchImporter)
    {
        this.documentFilter = documentFilter;
        this.couchImporter = couchImporter;
    }

	public void importDocument(CouchDbConnector couchDbConnector, InputStream inputStream) throws IOException
	{
		MimeStreamingReader mimeReader = new MimeStreamingReader(inputStream);

		Iterable<Header> headers = mimeReader.getHeaders();

		String id = getHeader(headers, "Content-ID").getValue();
		String revision = getHeader(headers, HttpHeaders.ETAG).getValue();

		if (!documentFilter.includeDocument(couchDbConnector, id, revision))
			return;

		Header contentTypeHeader = getHeader(headers, HttpHeaders.CONTENT_TYPE);
		NameValuePair boundaryNameValuePair = contentTypeHeader.getElements()[0].getParameterByName("boundary");
		String boundary = boundaryNameValuePair != null ?
				boundaryNameValuePair.getValue() :
				null;

		String contentLength = getHeader(headers, HttpHeaders.CONTENT_LENGTH).getValue();
		long size = Long.parseLong(contentLength);

		InputStream uploadStream = new UnCloseableInputStream(inputStream);

        ImportCommand importCommand = new BasicImportCommand(couchDbConnector, id, uploadStream, size, boundary);

        couchImporter.commandImport(importCommand);
	}

	private static Header getHeader(Iterable<Header> headers, final String headerName)
	{
		return Iterables.find(headers, new Predicate<Header>()
		{
			public boolean apply(@Nullable Header header)
			{
				return header != null && headerName.equalsIgnoreCase(header.getName());
			}
		});
	}
}
