package org.shop.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shop.dao.Search;
import org.shop.model.Product;
import org.shop.model.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by vprasanna on 5/15/2016.
 */
@Component
public class SearchImpl implements Search {
    private static final Log logger = LogFactory.getLog(SearchImpl.class);
    private static final String COLLECTION = "product";
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<String> getCategories() {
        List<String> response = mongoTemplate.getCollection(COLLECTION).distinct("category");
        logger.info(String.format("Found %s records!", response.size()));
        return response;
    }

    @Override
    public SearchResponse search(String keyword, int limit, int offset) {
        SearchResponse response = new SearchResponse();
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("name").regex(keyword), Criteria.where("description").regex(keyword), Criteria.where("category").regex(keyword));
        Query query = new Query(criteria);
        response.setMaxCount(getMaxCount(criteria));
        query.limit(limit).skip(offset);
        List<Product> products = mongoTemplate.find(query, Product.class);


        response.setProducts(products);
        response.setLimit(limit);
        response.setOffSet(offset);

        return response;
    }

    private long getMaxCount(Criteria criteria) {
        Query query = new Query(criteria);
        return mongoTemplate.count(query, COLLECTION);
    }
}