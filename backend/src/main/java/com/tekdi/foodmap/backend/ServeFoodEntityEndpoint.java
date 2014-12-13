package com.tekdi.foodmap.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
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

    /**
     * This method gets the <code>ServeFoodEntity</code> object associated with the specified <code>id</code>.
     *
     * @param id The id of the object to be returned.
     * @return The <code>ServeFoodEntity</code> associated with <code>id</code>.
     */
    @ApiMethod(name = "getServeFoodEntity")
    public ServeFoodEntity getServeFoodEntity(@Named("id") Long id) {
        // TODO: Implement this function
        logger.info("Calling getServeFoodEntity method");
        return null;
    }

    /**
     * This inserts a new <code>ServeFoodEntity</code> object.
     *
     * @param serveFoodEntity The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertServeFoodEntity")
    public ServeFoodEntity insertServeFoodEntity(ServeFoodEntity serveFoodEntity) {
        // TODO: Implement this function
        logger.info("Calling insertServeFoodEntity method for "+serveFoodEntity.getName());
        ofy().save().entity(serveFoodEntity).now();

        return serveFoodEntity;
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