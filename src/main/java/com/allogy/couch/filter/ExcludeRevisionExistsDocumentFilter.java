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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.ektorp.CouchDbConnector;
import org.ektorp.Revision;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A DocumentFilter which excludes documents which exist, or have existed, on
 * the destination Couch database.
 * Thus, if the document does not exist the filter will always include it. If
 * the document exists, but does not have the import revision in its history the
 * filter will include it.
 */
public class ExcludeRevisionExistsDocumentFilter implements DocumentFilter
{
	public boolean includeDocument(CouchDbConnector couchDbConnector, String documentId, final String revision)
	{
		if(!couchDbConnector.contains(documentId))
			return true;

		List<Revision> revisions = couchDbConnector.getRevisions(documentId);

		return !Iterables.any(revisions, new Predicate<Revision>()
		{
			public boolean apply(@Nullable Revision existingRevision)
			{
				if (existingRevision == null)
					return false;
				return revision.equals(existingRevision.getRev());
			}
		});
	}
}
