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

import java.io.IOException;
import java.io.OutputStream;

public interface CouchDocumentExporter
{
	/**
	 * Exports a document from a Couch database into an OutputStream.
	 * @param id the document id of the document to export
	 * @param revision the revision of the document to export
	 * @param couchDbConnector the CouchDbConnector from which to retrieve the document
	 * @param outputStream the OutputStream to which the export is written; this method
	 *                     will not close the stream
	 * @throws java.io.IOException an error occurred reading or writing the document
	 */
	void exportDocument(String id, String revision, CouchDbConnector couchDbConnector, OutputStream outputStream) throws IOException;
}
