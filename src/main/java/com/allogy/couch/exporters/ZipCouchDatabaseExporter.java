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

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipCouchDatabaseExporter implements CouchDatabaseExporter
{
	private CouchDocumentExporter couchDocumentExporter;

	public ZipCouchDatabaseExporter(CouchDocumentExporter couchDocumentExporter)
	{
		this.couchDocumentExporter = couchDocumentExporter;
	}

	public void export(CouchDbConnector couchDbConnector, OutputStream outputStream) throws IOException
	{
		ViewQuery q = new ViewQuery().allDocs();
		ViewResult viewResult = couchDbConnector.queryView(q);

		if(viewResult.getTotalRows() == 0)
			return;

		ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

		try
		{
			for(ViewResult.Row row: viewResult.getRows())
			{
				String id = row.getId();
				String revision = row.getValueAsNode().get("rev").getTextValue();
				ZipEntry zipEntry = new ZipEntry(id);
				zipOutputStream.putNextEntry(zipEntry);
				couchDocumentExporter.exportDocument(id, revision, couchDbConnector, zipOutputStream);
			}
		}
		finally
		{
			zipOutputStream.finish();
		}
	}
}
