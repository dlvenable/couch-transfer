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

package com.allogy.couch;

import org.ektorp.http.HttpClient;
import org.ektorp.http.HttpResponse;

import java.io.InputStream;
import java.util.Map;

public class PathToDbHttpClient implements HttpClient
{
	private HttpClient innerClient;
	private String path;

	public PathToDbHttpClient(HttpClient innerClient, String path)
	{
		this.innerClient = innerClient;
		this.path = normalizePath(path);
	}

	private static String normalizePath(String path)
	{
		String normalizedPath = path.charAt(0) == '/' ? path : "/" + path;

		if(normalizedPath.charAt(normalizedPath.length()-1) == '/')
			normalizedPath = normalizedPath.substring(0, normalizedPath.length()-1);

		return normalizedPath;
	}

	private String pathToUri(String uri)
	{
		return uri.charAt(0) != '/' ?
				path + "/" + uri :
				path + uri;
	}

	public HttpResponse get(String uri)
	{
		return innerClient.get(pathToUri(uri));
	}

	public HttpResponse get(String uri, Map<String, String> headers)
	{
		return innerClient.get(pathToUri(uri), headers);
	}

	public HttpResponse put(String uri, String content)
	{
		return innerClient.put(pathToUri(uri), content);
	}

	public HttpResponse put(String uri)
	{
		return innerClient.put(pathToUri(uri));
	}

	public HttpResponse put(String uri, InputStream data, String contentType, long contentLength)
	{
		return innerClient.put(pathToUri(uri), data, contentType, contentLength);
	}

	public HttpResponse post(String uri, String content)
	{
		return innerClient.post(pathToUri(uri), content);
	}

	public HttpResponse post(String uri, InputStream content)
	{
		return innerClient.post(pathToUri(uri), content);
	}

	public HttpResponse delete(String uri)
	{
		return innerClient.delete(pathToUri(uri));
	}

	public HttpResponse head(String uri)
	{
		return innerClient.head(pathToUri(uri));
	}

	public HttpResponse getUncached(String uri)
	{
		return innerClient.getUncached(pathToUri(uri));
	}

	public HttpResponse postUncached(String uri, String content)
	{
		return innerClient.postUncached(pathToUri(uri), content);
	}

	public HttpResponse copy(String sourceUri, String destination)
	{
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void shutdown()
	{
		innerClient.shutdown();
	}
}
