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

package com.allogy.couch.exporters;

import com.allogy.mime.MimeGeneratingInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.ektorp.CouchDbConnector;
import org.ektorp.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MimeCouchDocumentExporter implements CouchDocumentExporter
{
	public void exportDocument(String id, String revision, CouchDbConnector couchDbConnector, OutputStream outputStream) throws IOException
	{
		String dbName = couchDbConnector.path();
		String getPath = dbName + id + "?revs=true&attachments=true&rev=" + revision;
		Map<String, String> getRequestHeaders = new HashMap<String, String>();
		getRequestHeaders.put(HttpHeaders.ACCEPT, "multipart/related");
		HttpResponse getResponse = couchDbConnector.getConnection().get(getPath, getRequestHeaders);

		long contentLength = getResponse.getContentLength();
		String contentType = getResponse.getContentType();
		String revisionReceived = getResponse.getETag();
		if(revisionReceived == null)
			return;

		List<Header> headers = new ArrayList<Header>();
		headers.add(new BasicHeader("Content-ID",  id));
		headers.add(new BasicHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(contentLength)));
		headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, contentType));
		headers.add(new BasicHeader(HttpHeaders.ETAG, revisionReceived));

		InputStream documentEntryInputStream = getResponse.getContent();

		InputStream mimeStream = new MimeGeneratingInputStream(headers, documentEntryInputStream);
		try
		{
			IOUtils.copy(mimeStream, outputStream);
		}
		finally
		{
			documentEntryInputStream.close();
		}
	}
}
