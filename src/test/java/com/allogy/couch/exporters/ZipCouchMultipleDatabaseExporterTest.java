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
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class ZipCouchMultipleDatabaseExporterTest
{
	private CouchDatabaseExporter couchDatabaseExporter;
	private OutputStream outputStream;
	private List<CouchDbConnector> couchDbConnectors;

	@Before
	public void setUp()
	{
		couchDatabaseExporter = mock(CouchDatabaseExporter.class);
		couchDbConnectors = new ArrayList<CouchDbConnector>();
		couchDbConnectors.add(mock(CouchDbConnector.class));
		outputStream = mock(OutputStream.class);
	}
	public ZipCouchMultipleDatabaseExporter createObjectUnderTest()
	{
		return new ZipCouchMultipleDatabaseExporter(couchDatabaseExporter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void export_should_throw_if_couchDbConnectors_is_null() throws IOException
	{
		createObjectUnderTest().export(null, outputStream);
	}

	@Test(expected = IllegalArgumentException.class)
	public void export_should_throw_if_couchDbConnectors_is_empty() throws IOException
	{
		createObjectUnderTest().export(new ArrayList<CouchDbConnector>(), outputStream);
	}

	@Test(expected = IllegalArgumentException.class)
	public void export_should_throw_if_outputStream_is_null() throws IOException
	{
		createObjectUnderTest().export(couchDbConnectors, null);
	}
}
