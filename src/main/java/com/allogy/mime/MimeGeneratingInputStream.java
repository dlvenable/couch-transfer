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

package com.allogy.mime;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.http.Header;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;

public class MimeGeneratingInputStream extends InputStream
{
	private final InputStream innerInputStream;

	public MimeGeneratingInputStream(Iterable<Header> headers, InputStream bodyInputStream)
	{
		Iterable<InputStream> inputStreamHeaders = Iterables.transform(headers, new Function<Header, InputStream>()
		{
			public InputStream apply(@Nullable Header s)
			{
				InputStream headerStream = new ByteArrayInputStream(s.toString().getBytes());
				return new SequenceInputStream(headerStream, new ByteArrayInputStream(MimeUtilities.CRLFEnding.getBytes()));
			}
		});

		InputStream givenHeadersInputStream = new SequenceInputStream(new IteratorEnumeration(inputStreamHeaders.iterator()));

		InputStream headerInputStream = new SequenceInputStream(givenHeadersInputStream, new ByteArrayInputStream(MimeUtilities.CRLFEnding.getBytes()));

		innerInputStream = new SequenceInputStream(headerInputStream, bodyInputStream);
	}

	@Override
	public int read() throws IOException
	{
		return innerInputStream.read();
	}
}
