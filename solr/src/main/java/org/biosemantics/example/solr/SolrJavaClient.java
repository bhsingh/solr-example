package org.biosemantics.example.solr;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

public class SolrJavaClient {
	private static final String SOLR_URL = "http://localhost:8983/solr";

	public static void main(String[] args) throws IOException, SolrServerException {
		addDocs();
		query();
		deleteAll();
	}

	public static void addDocs() throws IOException, SolrServerException {
		HttpSolrServer server = new HttpSolrServer(SOLR_URL);
		for (int i = 0; i < 1000; ++i) {
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("cat", "book");
			doc.addField("id", "book-" + i);
			doc.addField("name", "The Legend of the Hobbit part " + i);
			server.add(doc);
			if (i % 100 == 0)
				server.commit(); // periodically flush
		}
		server.commit();
		// should only be called after all inserts are done. Optimises the
		// indexes for fats retrieval. May take a long time to execute and may
		// cause timeout exception
		server.optimize();
	}

	public static void query() throws MalformedURLException, SolrServerException {
		HttpSolrServer solr = new HttpSolrServer(SOLR_URL);
		SolrQuery query = new SolrQuery();
		query.setQuery("The Legend of the Hobbit part 1");
//		query.addFilterQuery("cat:electronics", "store:amazon.com");
		query.setFields("id", "price", "merchant", "cat", "store");
		query.setStart(0);
		query.set("defType", "edismax");
		QueryResponse response = solr.query(query);
		SolrDocumentList results = response.getResults();
		for (int i = 0; i < results.size(); ++i) {
			System.out.println(results.get(i));
		}
	}

	public static void deleteAll() throws SolrServerException, IOException {
		HttpSolrServer solr = new HttpSolrServer(SOLR_URL);
		solr.deleteByQuery("*:*");// CAUTION: deletes everything!
	}

}
