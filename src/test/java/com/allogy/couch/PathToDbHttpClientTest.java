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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class PathToDbHttpClientTest
{
	private String path;
	private String uri;
	private String correctUri;
	private HttpClient httpClient;
	private HttpResponse httpResponse;

	@Parameterized.Parameters
	public static Collection<String[]> data()
	{
		String noOuterSlashes = "use/this/path";
		String firstOuterSlash = "/" + noOuterSlashes;
		String lastOuterSlash = noOuterSlashes + "/";
		String bothOuterSlashes = "/" + noOuterSlashes + "/";

		String correctUri = bothOuterSlashes + noOuterSlashes;

		return Arrays.asList(new String[][]
			{
					{ noOuterSlashes, noOuterSlashes, correctUri },
					{ noOuterSlashes, firstOuterSlash, correctUri },
					{ firstOuterSlash, noOuterSlashes, correctUri },
					{ firstOuterSlash, firstOuterSlash, correctUri },
					{ lastOuterSlash, noOuterSlashes, correctUri },
					{ lastOuterSlash, firstOuterSlash, correctUri },
					{ bothOuterSlashes, noOuterSlashes, correctUri },
					{ bothOuterSlashes, firstOuterSlash, correctUri }
			});
	}

	public PathToDbHttpClientTest(String path, String uri, String correctUri)
	{
		this.path = path;
		this.uri = uri;
		this.correctUri = correctUri;

		assertThat(correctUri.startsWith("/"), is(true));
		assertThat(correctUri.indexOf("//"), is(-1));
		assertThat(correctUri.endsWith("/"), is(false));
	}

	@Before
	public void setUp()
	{
		httpClient = mock(HttpClient.class);
		httpResponse = mock(HttpResponse.class);
	}

	private PathToDbHttpClient createObjectUnderTest()
	{
		return new PathToDbHttpClient(httpClient, path);
	}

	@Test
	public void get_should_call_inner_HttpClient_get_using_path_to_uri()
	{
		stub(httpClient.get(correctUri)).toReturn(httpResponse);

		assertThat(createObjectUnderTest().get(uri), is(httpResponse));
	}

	@Test
	public void get_with_headers_should_call_inner_HttpClient_get_with_headers()
	{
		Map<String, String> headers = mock(Map.class);
		stub(httpClient.get(correctUri, headers)).toReturn(httpResponse);

		assertThat(createObjectUnderTest().get(uri, headers), is(httpResponse));
	}

	@Test
	public void getUncached_should_call_inner_HttpClient_get_using_path_to_uri()
	{
		stub(httpClient.getUncached(correctUri)).toReturn(httpResponse);

		assertThat(createObjectUnderTest().getUncached(uri), is(httpResponse));
	}

	@Test
	public void put_should_call_inner_HttpClient_get_using_path_to_uri()
	{
		stub(httpClient.put(correctUri)).toReturn(httpResponse);

		assertThat(createObjectUnderTest().put(uri), is(httpResponse));
	}

	@Test
	public void put_with_content_should_call_inner_HttpClient_put_with_content_using_path_to_uri()
	{
		String content = UUID.randomUUID().toString();
		stub(httpClient.put(correctUri, content)).toReturn(httpResponse);

		assertThat(createObjectUnderTest().put(uri, content), is(httpResponse));
	}

	@Test
	public void put_with_stream_should_call_inner_HttpClient_put_with_stream_using_path_to_uri()
	{
		InputStream data = mock(InputStream.class);
		String contentType = UUID.randomUUID().toString();
		long contentLength = 99;
		stub(httpClient.put(correctUri, data, contentType, contentLength)).toReturn(httpResponse);

		assertThat(createObjectUnderTest().put(uri, data, contentType, contentLength), is(httpResponse));
	}

	@Test
	public void post_with_string_should_call_inner_HttpClient_post_with_string_using_path_to_uri()
	{
		String content = UUID.randomUUID().toString();
		stub(httpClient.post(correctUri, content)).toReturn(httpResponse);

		assertThat(createObjectUnderTest().post(uri, content), is(httpResponse));
	}

	@Test
	public void post_with_stream_should_call_inner_HttpClient_post_with_stream_using_path_to_uri()
	{
		InputStream content = mock(InputStream.class);
		stub(httpClient.post(correctUri, content)).toReturn(httpResponse);

		assertThat(createObjectUnderTest().post(uri, content), is(httpResponse));
	}

	@Test
	public void postUncached_with_string_should_call_inner_HttpClient_postUncached_with_string_using_path_to_uri()
	{
		String content = UUID.randomUUID().toString();
		stub(httpClient.postUncached(correctUri, content)).toReturn(httpResponse);

		assertThat(createObjectUnderTest().postUncached(uri, content), is(httpResponse));
	}

	@Test
	public void delete_should_call_inner_HttpClient_delete_using_path_to_uri()
	{
		stub(httpClient.delete(correctUri)).toReturn(httpResponse);

		assertThat(createObjectUnderTest().delete(uri), is(httpResponse));
	}

	@Test
	public void head_should_call_inner_HttpClient_head_using_path_to_uri()
	{
		stub(httpClient.head(correctUri)).toReturn(httpResponse);

		assertThat(createObjectUnderTest().head(uri), is(httpResponse));
	}

	@Test
	public void shutdown_should_call_inner_HttpClient_shutdown()
	{
		createObjectUnderTest().shutdown();
		verify(httpClient).shutdown();
	}
}
