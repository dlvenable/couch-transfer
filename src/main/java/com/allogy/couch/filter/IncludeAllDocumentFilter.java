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

package com.allogy.couch.filter;

import org.ektorp.CouchDbConnector;

/**
 * A DocumentFilter which includes all documents.
 */
public class IncludeAllDocumentFilter implements DocumentFilter
{
	private IncludeAllDocumentFilter()
	{}

	public static DocumentFilter documentFilter()
	{
		return new IncludeAllDocumentFilter();
	}

	public boolean includeDocument(CouchDbConnector couchDbConnector, String documentId, String revision)
	{
		return true;
	}
}
