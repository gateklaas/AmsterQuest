package vu.group6.amsterquest;

import android.content.res.Resources;

import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class RetrieveAndRankExt extends RetrieveAndRank {

    private static HttpClient createHttpClient(String uri, String username, String password) {
        final URI scopeUri = URI.create(uri);

        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(scopeUri.getHost(), scopeUri.getPort()),
                new UsernamePasswordCredentials(username, password));

        final HttpClientBuilder builder = HttpClientBuilder.create()
                .setMaxConnTotal(128)
                .setMaxConnPerRoute(32)
                .setDefaultRequestConfig(RequestConfig.copy(RequestConfig.DEFAULT).setRedirectsEnabled(true).build())
                .setDefaultCredentialsProvider(credentialsProvider)
                .addInterceptorFirst(new PreemptiveAuthInterceptor());
        return builder.build();
    }

    public void getQuests(Resources resources, final String queryString, final String fields, final ServiceCallback<List<Quest>> questsCallback) {

        // get details
        final String username = resources.getString(R.string.quest_retriever_username);
        final String password = resources.getString(R.string.quest_retriever_password);
        final String clusterId = resources.getString(R.string.quest_solr_cluster_id);
        final String collectionName = resources.getString(R.string.quest_collection_name);
        final String rankerId = resources.getString(R.string.quest_ranker_id);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // create the solr client
                String solrUrl = getSolrUrl(clusterId);
                HttpClient client = createHttpClient(solrUrl, username, password);
                HttpSolrClient solrClient = new HttpSolrClient(solrUrl, client);

                // build the query
                SolrQuery query = new SolrQuery("*:*");
                query.setRequestHandler("/fcselect");
                query.set("ranker_id", rankerId);

                // execute the query
                try {
                    QueryResponse response = solrClient.query(collectionName, query);
                    List<Quest> quests = new ArrayList<Quest>();
                    for (SolrDocument doc : response.getResults()) {
                        quests.add(new Quest(doc));
                    }
                    questsCallback.onResponse(quests);
                } catch (SolrServerException | IOException e) {
                    questsCallback.onFailure(e);
                }
            }
        }).start();
    }

    private static class PreemptiveAuthInterceptor implements HttpRequestInterceptor {
        public void process(final HttpRequest request, final HttpContext context) throws HttpException {
            final AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);

            if (authState.getAuthScheme() == null) {
                final BasicCredentialsProvider credsProvider = (BasicCredentialsProvider) context
                        .getAttribute(HttpClientContext.CREDS_PROVIDER);
                final HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
                final Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(),
                        targetHost.getPort()));
                if (creds == null) {
                    throw new HttpException("No creds provided for preemptive auth.");
                }
                authState.update(new BasicScheme(), creds);
            }
        }
    }
}
