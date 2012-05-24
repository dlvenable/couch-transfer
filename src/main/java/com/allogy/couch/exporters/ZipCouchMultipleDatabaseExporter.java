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

import com.google.common.collect.Iterables;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipCouchMultipleDatabaseExporter implements CouchMultipleDatabaseExporter
{
	private CouchDatabaseExporter couchDatabaseExporter;

	public ZipCouchMultipleDatabaseExporter(CouchDatabaseExporter couchDatabaseExporter)
	{
		this.couchDatabaseExporter = couchDatabaseExporter;
	}

	public void export(Iterable<CouchDbConnector> couchDbConnectors, OutputStream outputStream) throws IOException
	{
		if(couchDbConnectors == null || Iterables.size(couchDbConnectors) == 0)
			throw new IllegalArgumentException("couchDbConnectors");
		if(outputStream == null)
			throw new IllegalArgumentException("outputStream");

		ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

		try
		{
			for(CouchDbConnector couchDbConnector: couchDbConnectors)
			{
				if(couchDbConnector.queryView(new ViewQuery().allDocs()).getTotalRows() == 0)
					continue;

				String databaseName = couchDbConnector.path();
				databaseName = databaseName.substring(0, databaseName.length() - 1);
				ZipEntry zipEntry = new ZipEntry(databaseName);
				zipOutputStream.putNextEntry(zipEntry);

				couchDatabaseExporter.export(couchDbConnector, zipOutputStream);
			}
		}
		finally
		{
			zipOutputStream.finish();
		}
	}
}
