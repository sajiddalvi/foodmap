package com.tekdi.foodmap.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
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
import java.util.Date;
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
        name = "orderEntityApi",
        version = "v1",
        resource = "orderEntity",
        namespace = @ApiNamespace(
                ownerDomain = "backend.foodmap.tekdi.com",
                ownerName = "backend.foodmap.tekdi.com",
                packagePath = ""
        )
)
public class OrderEntityEndpoint {

    private static final Logger logger = Logger.getLogger(OrderEntityEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    private static final String API_KEY = "AIzaSyCOM1CLtGXykSpyPpqNzMJMeR5JSzSWrxI" ;//System.getProperty("gcm.api.key");


    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(OrderEntity.class);
    }

    /**
     * Returns the {@link OrderEntity} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code OrderEntity} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "orderEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public OrderEntity get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting OrderEntity with ID: " + id);
        OrderEntity orderEntity = ofy().load().type(OrderEntity.class).id(id).now();
        if (orderEntity == null) {
            throw new NotFoundException("Could not find OrderEntity with ID: " + id);
        }
        return orderEntity;
    }

    /**
     * Inserts a new {@code OrderEntity}.
     */
    @ApiMethod(
            name = "insert",
            path = "orderEntity",
            httpMethod = ApiMethod.HttpMethod.POST)
    public OrderEntity insert(OrderEntity orderEntity) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that orderEntity.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.

        orderEntity.setTimestamp(new Date());

        ofy().save().entity(orderEntity).now();
        logger.info("Created OrderEntity with ID: " + orderEntity.getId());

        ServeFoodEntity s = findServer(orderEntity.getServerId());
        logger.info("Send order to "+s.getServerRegId());

        Sender sender = new Sender(API_KEY);

        Message msg = new Message.Builder()
                .addData("myMessageType", "new_order_to_server")
                .addData("orderId", orderEntity.getId().toString())
                .addData("serverId", orderEntity.getServerId().toString())
                .addData("finder", orderEntity.getFinderDevRegId().toString())
                .build();


        //Message msg = new Message.Builder().addData("message", orderEntity.toString()).build();

        RegistrationRecord record =
                OfyService.ofy().load().type(RegistrationRecord.class).filter("regId",s.getServerRegId() ).first().now();

        try {
            Result result = sender.send(msg, record.getRegId(), 5);

            if (result.getMessageId() != null) {
                logger.info("Message sent to " + record.getRegId());
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    logger.warning("Registration Id " + record.getRegId() + " no longer registered with GCM, removing from datastore");
                } else {
                    logger.warning("Error when sending message : " + error);
                }
            }

        } catch (java.io.IOException e) {
            logger.info ("Could not send order ");

        }

        return ofy().load().entity(orderEntity).now();
    }

    /**
     * Updates an existing {@code OrderEntity}.
     *
     * @param id          the ID of the entity to be updated
     * @param orderEntity the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code OrderEntity}
     */
    @ApiMethod(
            name = "update",
            path = "orderEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public OrderEntity update(@Named("id") Long id, OrderEntity orderEntity) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);

        Sender sender = new Sender(API_KEY);

        Message msg = new Message.Builder()
                .addData("myMessageType", "order_update")
                .addData("orderId", orderEntity.getId().toString())
                .addData("serverId", orderEntity.getServerId().toString())
                .addData("finder", orderEntity.getFinderDevRegId().toString())
                .addData("orderState", orderEntity.getOrderState().toString())
                .build();

        try {
            Result result = sender.send(msg, orderEntity.getFinderDevRegId(), 5);

            if (result.getMessageId() != null) {
                logger.info("Message sent to " + orderEntity.getFinderDevRegId());
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    orderEntity.setOrderState(-1);
                    logger.warning("Registration Id " + orderEntity.getFinderDevRegId() + " no longer registered with GCM, removing from datastore");
                } else {
                    orderEntity.setOrderState(-2);
                    logger.warning("Error when sending message : " + error);
                }

            }

        } catch (java.io.IOException e) {
            orderEntity.setOrderState(-3);
            logger.info ("Could not send order ");

        }

        orderEntity.setTimestamp(new Date());

        ofy().save().entity(orderEntity).now();
        logger.info("Updated OrderEntity: " + orderEntity);

        return ofy().load().entity(orderEntity).now();
    }

    /**
     * Deletes the specified {@code OrderEntity}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code OrderEntity}
     */
    @ApiMethod(
            name = "remove",
            path = "orderEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(OrderEntity.class).id(id).now();
        logger.info("Deleted OrderEntity with ID: " + id);
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
            path = "orderEntity",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<OrderEntity> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<OrderEntity> query = ofy().load().type(OrderEntity.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<OrderEntity> queryIterator = query.iterator();
        List<OrderEntity> orderEntityList = new ArrayList<OrderEntity>(limit);
        while (queryIterator.hasNext()) {
            orderEntityList.add(queryIterator.next());
        }
        return CollectionResponse.<OrderEntity>builder().setItems(orderEntityList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(OrderEntity.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find OrderEntity with ID: " + id);
        }
    }

    private ServeFoodEntity findServer(Long serverId) {

            ServeFoodEntity s = ofy().load().type(ServeFoodEntity.class).id(serverId).now();
            return s;
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
            path = "orderEntityForServer/{serverId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<OrderEntity> listForServer(@Named("serverId") Long serverId,
                                                        @Nullable @Named("cursor") String cursor,
                                                        @Nullable @Named("limit") Integer limit)
            throws NotFoundException {

        logger.info("listforServer with ID: " + serverId);

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<OrderEntity> query = ofy().load().type(OrderEntity.class).filter("serverId", serverId).limit(limit);

        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
            logger.info("cursor not null");
        }
        QueryResultIterator<OrderEntity> queryIterator = query.iterator();
        List<OrderEntity> orderEntityList = new ArrayList<OrderEntity>(limit);
        while (queryIterator.hasNext()) {
            orderEntityList.add(queryIterator.next());
        }

        return CollectionResponse.<OrderEntity>builder().setItems(orderEntityList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    @ApiMethod(
            name = "listForFinder",
            path = "orderEntityForFinder/{finderDevRegId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<OrderEntity> listForFinder(@Named("finderDevRegId") String finderDevRegId,
                                                         @Nullable @Named("cursor") String cursor,
                                                         @Nullable @Named("limit") Integer limit)
            throws NotFoundException {

        logger.info("listforFinder with ID: " + finderDevRegId);

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<OrderEntity> query = ofy().load().type(OrderEntity.class).filter("finderDevRegId", finderDevRegId).limit(limit);

        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
            logger.info("cursor not null");
        }
        QueryResultIterator<OrderEntity> queryIterator = query.iterator();
        List<OrderEntity> orderEntityList = new ArrayList<OrderEntity>(limit);
        while (queryIterator.hasNext()) {
            orderEntityList.add(queryIterator.next());
        }

        return CollectionResponse.<OrderEntity>builder().setItems(orderEntityList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }
}