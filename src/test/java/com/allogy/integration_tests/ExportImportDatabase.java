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

package com.allogy.integration_tests;

import com.allogy.couch.filter.IncludeAllDocumentFilter;
import com.allogy.couch.exporters.*;
import com.allogy.couch.importers.*;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbPath;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class ExportImportDatabase
{
	private CouchDbInstance instance;
	private List<CouchDbConnector> couchDbConnectors;
	private File exportFile;
	private Map<String,Map<String,Object>> databaseToSingleDocumentMap;

	@Before
	public void setUp() throws MalformedURLException
	{
		StdHttpClient.Builder clientBuilder = new StdHttpClient.Builder().url("http://localhost:5984");
		HttpClient client = clientBuilder.build();
		instance = new StdCouchDbInstance(client);

		exportFile = new File("test_export_database.zip");

		databaseToSingleDocumentMap = new HashMap<String, Map<String, Object>>();
		couchDbConnectors = new ArrayList<CouchDbConnector>();
		for(int i = 0; i < 3; i++)
		{
			createRandomDatabase(couchDbConnectors, databaseToSingleDocumentMap);
		}
	}

	public void createRandomDatabase(List<CouchDbConnector> couchDbConnectors, Map<String, Map<String, Object>> dbToSingleDocumentMap)
	{
		String databaseName = "test" + UUID.randomUUID().toString();
		CouchDbConnector couchDbConnector = instance.createConnector(databaseName, true);
		couchDbConnectors.add(couchDbConnector);

		Map<String, Object> document = new HashMap<String, Object>();
		document.put("someValue", UUID.randomUUID().toString());

		couchDbConnector.create(document);

		String documentId = (String) document.get("_id");
		assertThat(documentId, notNullValue());
		assertThat(couchDbConnector.contains(documentId), is(true));

		dbToSingleDocumentMap.put(couchDbConnector.getDatabaseName(), document);
	}

	@After
	public void tearDown()
	{
		for(CouchDbConnector couchDbConnector: couchDbConnectors)
			instance.deleteDatabase(couchDbConnector.path());
		if(exportFile.exists())
			exportFile.delete();
	}

	@Test
	public void export_database_and_import_it_back() throws IOException
	{
		CouchDocumentExporter documentExporter = new MimeCouchDocumentExporter();
		CouchDatabaseExporter databaseExporter = new ZipCouchDatabaseExporter(documentExporter);
		CouchMultipleDatabaseExporter multipleDatabaseExporter = new ZipCouchMultipleDatabaseExporter(databaseExporter);

		OutputStream exportOutputStream = new FileOutputStream(exportFile);
		try
		{
			multipleDatabaseExporter.export(couchDbConnectors, exportOutputStream);
		}
		finally
		{
			exportOutputStream.close();
		}

		assertThat(exportFile.exists(), is(true));

		Iterable<String> exportedDatabaseNames = Iterables.transform(couchDbConnectors, new Function<CouchDbConnector, String>()
		{
			public String apply(@Nullable CouchDbConnector couchDbConnector)
			{
				return couchDbConnector.path();
			}
		});

		for(String databaseName: exportedDatabaseNames)
			instance.deleteDatabase(databaseName);

		CouchDocumentImporter documentImporter = new MimeCouchDocumentImporter(IncludeAllDocumentFilter.documentFilter());
		CouchDatabaseImporter databaseImporter = new ZipCouchDatabaseImporter(documentImporter);
		CouchMultipleDatabaseImporter multipleDatabaseImporter = new ZipCouchMultipleDatabaseImporter(databaseImporter);

		InputStream importInputStream = new FileInputStream(exportFile);

		try
		{
			multipleDatabaseImporter.importDatabases(instance, importInputStream);
		}
		finally
		{
			importInputStream.close();
		}

		for(String databaseName: exportedDatabaseNames)
		{
			DbPath dbPath = new DbPath(databaseName);
			assertTrue(databaseName, instance.checkIfDbExists(dbPath));

			Map<String, Object> expectedSingleObject = databaseToSingleDocumentMap.get(dbPath.getDbName());

			CouchDbConnector dbConnector = instance.createConnector(databaseName, false);
			Map<String, Object> actualDocument = dbConnector.find(Map.class, (String) expectedSingleObject.get("_id"));

			assertThat(actualDocument, notNullValue());
			assertThat(actualDocument, is(expectedSingleObject));
		}
	}
}
