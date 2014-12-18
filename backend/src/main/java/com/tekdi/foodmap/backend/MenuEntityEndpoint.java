package com.tekdi.foodmap.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "menuEntityApi",
        version = "v1",
        resource = "menuEntity",
        namespace = @ApiNamespace(
                ownerDomain = "backend.foodmap.tekdi.com",
                ownerName = "backend.foodmap.tekdi.com",
                packagePath = ""
        )
)
public class MenuEntityEndpoint {

    private static final Logger logger = Logger.getLogger(MenuEntityEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(MenuEntity.class);
    }

    /**
     * Returns the {@link MenuEntity} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code MenuEntity} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "menuEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public MenuEntity get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting MenuEntity with ID: " + id);
        MenuEntity menuEntity = ofy().load().type(MenuEntity.class).id(id).now();
        if (menuEntity == null) {
            throw new NotFoundException("Could not find MenuEntity with ID: " + id);
        }
        return menuEntity;
    }

    /**
     * Inserts a new {@code MenuEntity}.
     */
    @ApiMethod(
            name = "insert",
            path = "menuEntity",
            httpMethod = ApiMethod.HttpMethod.POST)
    public MenuEntity insert(MenuEntity menuEntity) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that menuEntity.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(menuEntity).now();
        logger.info("Created MenuEntity with ID: " + menuEntity.getId());

        return ofy().load().entity(menuEntity).now();
    }

    /**
     * Updates an existing {@code MenuEntity}.
     *
     * @param id         the ID of the entity to be updated
     * @param menuEntity the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code MenuEntity}
     */
    @ApiMethod(
            name = "update",
            path = "menuEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public MenuEntity update(@Named("id") Long id, MenuEntity menuEntity) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(menuEntity).now();
        logger.info("Updated MenuEntity: " + menuEntity);
        return ofy().load().entity(menuEntity).now();
    }

    /**
     * Deletes the specified {@code MenuEntity}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code MenuEntity}
     */
    @ApiMethod(
            name = "remove",
            path = "menuEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(MenuEntity.class).id(id).now();
        logger.info("Deleted MenuEntity with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "menuEntity",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<MenuEntity> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<MenuEntity> query = ofy().load().type(MenuEntity.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<MenuEntity> queryIterator = query.iterator();
        List<MenuEntity> menuEntityList = new ArrayList<MenuEntity>(limit);
        while (queryIterator.hasNext()) {
            menuEntityList.add(queryIterator.next());
        }
        return CollectionResponse.<MenuEntity>builder().setItems(menuEntityList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(MenuEntity.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find MenuEntity with ID: " + id);
        }
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "listForServer",
            path = "menuEntityForServer/{serverId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<MenuEntity> listForServer(@Named("serverId") Long serverId,
                                                        @Nullable @Named("cursor") String cursor,
                                                        @Nullable @Named("limit") Integer limit)
            throws NotFoundException {

        logger.info("listforServer with ID: " + serverId);
/*
        Query<MenuEntity> q = ofy().load().type(MenuEntity.class);
        q = q.
        q = q.filter("serverId >", 0);
        List<MenuEntity> foos = q.list();

        logger.info("listing foos3");
        for (MenuEntity element : foos) {
            logger.info("xx=>"+element.getName());
        }

*/

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<MenuEntity> query = ofy().load().type(MenuEntity.class).filter("serverId", serverId).limit(limit);

        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
            logger.info("cursor not null");
        }
        QueryResultIterator<MenuEntity> queryIterator = query.iterator();
        List<MenuEntity> menuEntityList = new ArrayList<MenuEntity>(limit);
        while (queryIterator.hasNext()) {
            menuEntityList.add(queryIterator.next());
            logger.info("menuitem ");
        }
        return CollectionResponse.<MenuEntity>builder().setItems(menuEntityList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }
}