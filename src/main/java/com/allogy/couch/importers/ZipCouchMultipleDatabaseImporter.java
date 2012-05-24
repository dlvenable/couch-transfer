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

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipCouchMultipleDatabaseImporter implements CouchMultipleDatabaseImporter
{
	private CouchDatabaseImporter couchDatabaseImporter;

	public ZipCouchMultipleDatabaseImporter(CouchDatabaseImporter couchDatabaseImporter)
	{
		this.couchDatabaseImporter = couchDatabaseImporter;
	}

	public void importDatabases(CouchDbInstance couchDbInstance, InputStream inputStream) throws IOException
	{
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);

		ZipEntry zipEntry;
		while ((zipEntry = zipInputStream.getNextEntry()) != null)
		{
			String databaseName = zipEntry.getName();
			CouchDbConnector couchDbConnector = couchDbInstance.createConnector(databaseName, true);

			couchDatabaseImporter.importDatabase(couchDbConnector, zipInputStream);
		}
	}
}
