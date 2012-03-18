
/*
 * Copyright (c) 2012 Yan Pujante
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pongasoft.kiwidoc.index.impl.keyword.impl;

import com.pongasoft.kiwidoc.index.api.MalformedQueryException;
import com.pongasoft.kiwidoc.index.api.KeywordQuery;
import com.pongasoft.kiwidoc.index.api.Visibility;
import com.pongasoft.kiwidoc.index.api.SearchResults;
import com.pongasoft.kiwidoc.index.impl.ResourceEncoder;
import com.pongasoft.kiwidoc.index.impl.StringResourceEncoder;
import com.pongasoft.kiwidoc.index.impl.keyword.api.MutableKeywordIndex;
import com.pongasoft.kiwidoc.index.impl.result.api.SearchResultsAccumulator;
import com.pongasoft.kiwidoc.index.impl.result.impl.SearchResultsAccumulatorImpl;
import com.pongasoft.kiwidoc.index.impl.lucene.api.LuceneDirectory;
import com.pongasoft.kiwidoc.index.impl.lucene.api.LuceneIndexWriter;
import com.pongasoft.kiwidoc.index.impl.lucene.api.LuceneIndexSearcher;
import com.pongasoft.kiwidoc.index.impl.lucene.api.LuceneHitCollector;
import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.PackageResource;
import com.pongasoft.kiwidoc.model.resource.ManifestResource;
import com.pongasoft.util.core.annotations.FieldInitializer;
import com.pongasoft.util.core.annotations.ObjectInitializer;
import com.pongasoft.util.core.exception.InternalException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleHTMLEncoder;
import org.apache.lucene.search.highlight.Encoder;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;

/**
 * This class is not thread safe and thread safety needs to be built on top of it (using a dynamic
 * proxy based on the annotations on the interface).
 *
 * @author yan@pongasoft.com
 */
public class KeywordIndexImpl implements MutableKeywordIndex
{
  public static final Log log = LogFactory.getLog(KeywordIndexImpl.class);

  private static final Encoder HTML_ENCODER = new SimpleHTMLEncoder();

  private class MyHitCollector implements LuceneHitCollector<Resource>
  {
    private final SearchResultsAccumulator _accumulator;

    public MyHitCollector(SearchResultsAccumulator accumulator)
      throws IOException
    {
      _accumulator = accumulator;
    }

    public void collect(int doc, float score, Resource userData)
    {
      _accumulator.accumulateSearchResult(userData, score);
    }
  }

  private Analyzer _analyzer = new StandardAnalyzer();
  private LuceneDirectory<Resource> _publicOnlyDirectory;
  private LuceneDirectory<Resource> _publicAndPrivateDirectory;
  private ResourceEncoder<String> _resourceEncoder = new StringResourceEncoder();
  private Formatter _highlighterFormatter = new SimpleHTMLFormatter("<span class=\"highlight\">", "</span>");
  private Map<Class<? extends Resource>, DocumentFactory> _documentFactories;

  @ObjectInitializer
  public KeywordIndexImpl()
  {
    _documentFactories = new HashMap<Class<? extends Resource>, DocumentFactory>();
    _documentFactories.put(ClassResource.class, new ClassModelDocumentFactory());
    _documentFactories.put(PackageResource.class, new PackageModelDocumentFactory());
    _documentFactories.put(LibraryVersionResource.class, new LibraryVersionModelDocumentFactory());
    _documentFactories.put(ManifestResource.class, new ManifestModelDocumentFactory());
  }

  /**
   * Constructor
   */
  public KeywordIndexImpl(Analyzer analyzer,
                          LuceneDirectory<Resource> publicOnlyDirectory,
                          LuceneDirectory<Resource> publicAndPrivateDirectory,
                          ResourceEncoder<String> resourceEncoder,
                          Map<Class<? extends Resource>, DocumentFactory> documentFactories)
    throws InternalException
  {
    _analyzer = analyzer;
    _publicOnlyDirectory = publicOnlyDirectory;
    _publicAndPrivateDirectory = publicAndPrivateDirectory;
    _resourceEncoder = resourceEncoder;
    _documentFactories = documentFactories;
  }

  public Analyzer getAnalyzer()
  {
    return _analyzer;
  }

  @FieldInitializer
  public void setAnalyzer(Analyzer analyzer)
  {
    _analyzer = analyzer;
  }

  public LuceneDirectory<Resource> getPublicOnlyDirectory()
  {
    return _publicOnlyDirectory;
  }

  @FieldInitializer
  public void setPublicOnlyDirectory(LuceneDirectory<Resource> publicOnlyDirectory)
  {
    _publicOnlyDirectory = publicOnlyDirectory;
  }

  public LuceneDirectory<Resource> getPublicAndPrivateDirectory()
  {
    return _publicAndPrivateDirectory;
  }

  @FieldInitializer
  public void setPublicAndPrivateDirectory(LuceneDirectory<Resource> publicAndPrivateDirectory)
  {
    _publicAndPrivateDirectory = publicAndPrivateDirectory;
  }

  public ResourceEncoder<String> getResourceEncoder()
  {
    return _resourceEncoder;
  }

  @FieldInitializer
  public void setResourceEncoder(ResourceEncoder<String> resourceEncoder)
  {
    _resourceEncoder = resourceEncoder;
  }

  public Formatter getHighlighterFormatter()
  {
    return _highlighterFormatter;
  }

  @FieldInitializer
  public void setHighlighterFormatter(Formatter highlighterFormatter)
  {
    _highlighterFormatter = highlighterFormatter;
  }

  /**
   * Adds the model to the index.
   *
   * @param model the model to add
   * @throws InternalException if there is something wrong
   */
  public void indexModel(Model model) throws InternalException
  {
    batchIndexModels(Arrays.asList(model));
  }

  /**
   * Removes the resource from the index
   *
   * @param resource the resource to remove
   * @throws InternalException if there is something wrong
   */
  public void unindexResource(Resource resource) throws InternalException
  {
    batchUnindexResources(Arrays.asList(resource));
  }

  /**
   * Removes the resources from the index (batch version)
   *
   * @param resources the resources to remove
   * @throws InternalException if there is something wrong
   */
  public void batchUnindexResources(Collection<? extends Resource> resources) throws InternalException
  {
    batchUnindexResources(resources, DocumentFactory.ID_FIELD);
  }

  /**
   * Unindex resources
   */
  public void batchUnindexResources(Collection<? extends Resource> resources, String field)
    throws InternalException
  {
    try
    {
      LuceneIndexWriter writerPub = getDirectory(Visibility.publicOnly).getWriter();
      try
      {
        LuceneIndexWriter writerPubPriv = getDirectory(Visibility.publicAndPrivate).getWriter();
        try
        {
          for(Resource resource : resources)
          {
            Term term = new Term(field, _resourceEncoder.encodeResource(resource));
            writerPub.deleteDocuments(term);
            writerPubPriv.deleteDocuments(term);
          }

          writerPubPriv.prepareCommit();
          writerPub.prepareCommit();

          writerPubPriv.commit();
          writerPub.commit();
        }
        finally
        {
          writerPubPriv.close();
        }
      }
      finally
      {
        writerPub.close();
      }

    }
    catch(IOException e)
    {
      throw new InternalException(e);
    }
  }

  /**
   * Batch index multiple models.
   *
   * @param models models to index
   * @throws InternalException if there is something wrong
   */
  public void batchIndexModels(Collection<? extends Model> models) throws InternalException
  {
    try
    {
      LuceneIndexWriter writerPub = getDirectory(Visibility.publicOnly).getWriter();

      try
      {
        LuceneIndexWriter writerPubPriv = getDirectory(Visibility.publicAndPrivate).getWriter();

        try
        {
          // first unindex all the models
          for(Model model : models)
          {
            unindexModel(writerPub, writerPubPriv, model);
          }

          // then index each model
          for(Model model : models)
          {
            indexModel(writerPub, writerPubPriv, model);
          }

          writerPubPriv.prepareCommit();
          writerPub.prepareCommit();

          writerPubPriv.commit();
          writerPub.commit();
        }
        finally
        {
          writerPubPriv.close();
        }
      }
      finally
      {
        writerPub.close();
      }
    }
    catch(IOException e)
    {
      throw new InternalException(e);
    }
  }

  private void unindexModel(LuceneIndexWriter writerPub,
                            LuceneIndexWriter writerPubPriv,
                            Model model)
    throws IOException
  {
    Term term =
      new Term(DocumentFactory.ID_FIELD, _resourceEncoder.encodeResource(model.getResource()));
    writerPub.deleteDocuments(term);
    writerPubPriv.deleteDocuments(term);
  }

  private void indexModel(LuceneIndexWriter writerPub,
                          LuceneIndexWriter writerPubPriv,
                          Model model)
    throws IOException
  {
    Document document = createDocument(model);

    if(document != null)
    {
      writerPubPriv.addDocument(document);
      if(model.isPublicAPI())
      {
        Model publicClassModel = (Model) model.toPublicAPI();
        if(publicClassModel != model)
          document = createDocument(publicClassModel);
        writerPub.addDocument(document);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Document createDocument(Model model)
  {
    DocumentFactory factory = _documentFactories.get(model.getResource().getClass());

    if(factory == null)
    {
      log.warn("Could not find a factory for " + model.getResource() + "... (ignored)");
      return null;
    }

    return factory.createDocument(model);
  }

  /**
   * Removes the entire library version from the index
   *
   * @throws InternalException if there is something wrong
   */
  public void unindexLibraryVersion(LibraryVersionResource libraryVersion) throws InternalException
  {
    batchUnindexResources(Arrays.asList(libraryVersion), DocumentFactory.LIBRARY_VERSION_FIELD);
  }

  /**
   * Find all resources that matches the query.
   *
   * @param accumulator result accumulator
   * @throws MalformedQueryException if the query cannot be parsed
   * @throws InternalException       if there is an internal problem
   */
  public void findResources(KeywordQuery query, SearchResultsAccumulator accumulator)
    throws MalformedQueryException, InternalException
  {
    Query parsedQuery = parseQuery(query);

    if(parsedQuery != null)
    {
      try
      {
        LuceneIndexSearcher<Resource> searcher = getDirectory(query.getVisibility()).getSearcher();
        searcher.search(parsedQuery, new MyHitCollector(accumulator));
      }
      catch(IOException e)
      {
        throw new InternalException(e);
      }
    }
  }

  /**
   * Find all resources that matches the query.
   *
   * @return all the matching resource
   * @throws MalformedQueryException if the query cannot be parsed
   * @throws InternalException       if there is an internal problem
   */
  public SearchResults findResources(KeywordQuery query)
    throws MalformedQueryException, InternalException
  {
    SearchResultsAccumulatorImpl accumulator =
      new SearchResultsAccumulatorImpl(query);
    findResources(query, accumulator);
    return accumulator.getSearchResults();
  }

  /**
   */
  private Query parseQuery(KeywordQuery query) throws MalformedQueryException
  {
    try
    {
      return generateSimpleQuery(query.getKeyword(), DocumentFactory.BODY_FIELD);
    }
    catch(ParseException e)
    {
      throw new MalformedQueryException(query.getKeyword(), e);
    }

//    QueryParser qp = new QueryParser(DocumentFactory.BODY_FIELD, _analyzer);
//
//    Query parsedQuery;
//    try
//    {
//      parsedQuery = qp.parse(query.getKeyword());
//    }
//    catch(ParseException e)
//    {
//      throw new MalformedQueryException(query.getKeyword(), e);
//    }
//    return parsedQuery;
  }

  /**
   * Generates a simple query: a boolean query made of TermQuery separated
   * by AND.
   *
   * @param query
   * @return <code>null</code> if there is no terms
   * @throws ParseException
   */
  private Query generateSimpleQuery(String keyword, String field) throws ParseException
  {
    int termCount = 0;
    TokenStream source = _analyzer.tokenStream(field,
                                               new StringReader(keyword));

    BooleanQuery q = new BooleanQuery();
    org.apache.lucene.analysis.Token t = new org.apache.lucene.analysis.Token();

    while(true)
    {
      try
      {
        t = source.next(t);
      }
      catch(IOException e)
      {
        if(log.isDebugEnabled())
          log.debug("ingnored exception", e);

        t = null;
      }

      if(t == null)
        break;

      termCount++;
      q.add(new TermQuery(new Term(field, t.term())), BooleanClause.Occur.MUST);
    }
    try
    {
      source.close();
    }
    catch(IOException e)
    {
      if(log.isDebugEnabled())
        log.debug("ingnored exception", e);
    }

    if(termCount == 0)
    {
      return null;
    }

    BooleanClause[] clauses = q.getClauses();

    if(clauses != null && clauses.length == 1)
     return clauses[0].getQuery();

    return q;
  }


  /**
   * Highlights the provided results obtained using the provided query.
   *
   * @param query  the query from which the results were computed
   * @param models the models to highlight
   * @return a map representing for each entry in the model its associated resource and highlight
   * @throws MalformedQueryException if the query cannot be parsed
   * @throws InternalException if there is an internal problem
   */
  public <R extends Resource> Map<R, String[]> highlightResults(KeywordQuery query,
                                                                Collection<Model<R>> models)
    throws InternalException, MalformedQueryException
  {
    Map<R, String[]> res = new LinkedHashMap<R, String[]>();

    Query parsedQuery = parseQuery(query);

    if(parsedQuery != null)
    {
      Highlighter highlighter =
        new Highlighter(_highlighterFormatter,
                        HTML_ENCODER,
                        new QueryScorer(parsedQuery));

      for(Model<R> model : models)
      {
        Document document = new Document();
        String bodyText = buildBody(model);
        document.add(new Field(DocumentFactory.BODY_FIELD, bodyText, Field.Store.NO, Field.Index.ANALYZED));
        TokenStream tokenStream = TokenSources.getTokenStream(document, DocumentFactory.BODY_FIELD, _analyzer);
        try
        {
          res.put(model.getResource(), highlighter.getBestFragments(tokenStream, bodyText, 2));
        }
        catch(IOException e)
        {
          log.warn("exception while computing highlight... [ignored]", e);
        }
      }
    }

    return res;
  }

  /**
   * Optimizes the index.
   *
   * @throws InternalException if there is something wrong
   */
  public void optimize() throws InternalException
  {
    try
    {
      getDirectory(Visibility.publicOnly).optimize();
      getDirectory(Visibility.publicAndPrivate).optimize();
    }
    catch(IOException e)
    {
      throw new InternalException(e);
    }
  }

  private LuceneDirectory<Resource> getDirectory(Visibility visibility)
  {
    return visibility == Visibility.publicAndPrivate ?
      _publicAndPrivateDirectory : _publicOnlyDirectory;
  }

  /**
   * Builds the body for the given model (note that all models are supported...)
   */
  private <R extends Resource> String buildBody(Model<R> model)
  {
    Document document = createDocument(model);
    if(document != null)
    {
      return document.getField(DocumentFactory.BODY_FIELD).stringValue();
    }
    else
      return "";
  }
}
