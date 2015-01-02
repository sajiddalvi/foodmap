package com.tekdi.foodmap.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.tekdi.foodmap.backend.OfyService.ofy;


/**
 * An endpoint class we are exposing
 */
@Api(
        name = "serveFoodEntityApi",
        version = "v1",
        resource = "serveFoodEntity",
        namespace = @ApiNamespace(
                ownerDomain = "backend.foodmap.tekdi.com",
                ownerName = "backend.foodmap.tekdi.com",
                packagePath = ""
        )
)
public class ServeFoodEntityEndpoint {

    private static final Logger logger = Logger.getLogger(ServeFoodEntityEndpoint.class.getName());

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(ServeFoodEntity.class);
    }

    /**
     * This method gets the <code>ServeFoodEntity</code> object associated with the specified <code>id</code>.
     *
     * @param id The id of the object to be returned.
     * @return The <code>ServeFoodEntity</code> associated with <code>id</code>.
     */
    /*
    @ApiMethod(name = "getServeFoodEntity")
    public ServeFoodEntity getServeFoodEntity(@Named("id") Long id) {
        // TODO: Implement this function
        logger.info("Calling getServeFoodEntity method");
        return null;
    }
    */

    @ApiMethod(
            name = "get",
            path = "serveFoodEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public ServeFoodEntity get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting ServeFoodEntity with ID: " + id);
        ServeFoodEntity serveFoodEntityEntity = ObjectifyService.ofy().load().type(ServeFoodEntity.class).id(id).now();
        if (serveFoodEntityEntity == null) {
            throw new NotFoundException("Could not find ServeFoodEntity with ID: " + id);
        }
        return serveFoodEntityEntity;
    }

    /**
     * This inserts a new <code>ServeFoodEntity</code> object.
     *
     * @param serveFoodEntity The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertServeFoodEntity")
    public ServeFoodEntity insertServeFoodEntity(ServeFoodEntity serveFoodEntity) {
        logger.info("Calling insertServeFoodEntity method for "+serveFoodEntity.getName());
        ofy().save().entity(serveFoodEntity).now();

        return serveFoodEntity;
    }



    /**
     * Updates an existing {@code ServeFoodEntity}.
     *
     * @param id         the ID of the entity to be updated
     * @param serverFoodEntity the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code ServeFoodEntity}
     */
    @ApiMethod(
            name = "update",
            path = "serverFoodEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public ServeFoodEntity update(@Named("id") Long id, ServeFoodEntity serverFoodEntity) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ObjectifyService.ofy().save().entity(serverFoodEntity).now();
        logger.info("Updated ServerFoodEntity: " + serverFoodEntity);
        return ObjectifyService.ofy().load().entity(serverFoodEntity).now();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ObjectifyService.ofy().load().type(ServeFoodEntity.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find ServeFoodEntity with ID: " + id);
        }
    }
    /**
     * Return a collection of servers
     *
     * @param count The number of servers
     * @return a list of servers
     */

    @ApiMethod(name = "listServers")
    public CollectionResponse<ServeFoodEntity> listServers(
            @Nullable @Named("cursor") String cursorString,
            @Nullable @Named("count") Integer count) {

        Query<ServeFoodEntity> query = ofy().load().type(ServeFoodEntity.class);
        if (count != null) query.limit(count);
        if (cursorString != null && cursorString != "") {
            query = query.startAt(Cursor.fromWebSafeString(cursorString));
        }

        List<ServeFoodEntity> records = new ArrayList<ServeFoodEntity>();
        QueryResultIterator<ServeFoodEntity> iterator = query.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            records.add(iterator.next());
            if (count != null) {
                num++;
                if (num == count) break;
            }
        }

        if (cursorString != null && cursorString != "") {
            Cursor cursor = iterator.getCursor();
            if (cursor != null) {
                cursorString = cursor.toWebSafeString();
            }
        }
        return CollectionResponse.<ServeFoodEntity>builder().setItems(records).setNextPageToken(cursorString).build();
    }
}