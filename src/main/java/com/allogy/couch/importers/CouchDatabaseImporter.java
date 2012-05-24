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

import java.io.IOException;
import java.io.InputStream;

public interface CouchDatabaseImporter
{
	/**
	 * Imports the data in an InputStream into a Couch database.
	 * @param couchDbConnector the CouchDbConnector for the destination database
	 * @param inputStream the InputStream containing the Couch database export; this function does not close it
	 * @throws java.io.IOException an error occurred reading or writing the import documents
	 */
	void importDatabase(CouchDbConnector couchDbConnector, InputStream inputStream) throws IOException;
}
