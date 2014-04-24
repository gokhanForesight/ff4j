package org.ff4j.web.resources;

/*
 * #%L
 * ff4j-web
 * %%
 * Copyright (C) 2013 - 2014 Ff4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.ff4j.core.Feature;
import org.ff4j.core.FeatureStore;
import org.ff4j.exception.FeatureNotFoundException;
import org.ff4j.utils.FeatureJsonMarshaller;

/**
 * Represent a feature as WebResource.
 * 
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class FeatureResource {

    /**
     * current uri of the resource
     */
    @Context
    private UriInfo uriInfo;

    /**
     * current http request
     */
    @Context
    private Request request;

    /** Reference to ff4j. */
    @Context
    private FeatureStore store;

    /** Current Feature identifier */
    private String id;

    /**
     * Defaut constructor.
     */
    public FeatureResource() {}

    /**
     * Constructor by Parent resource
     * 
     * @param uriInfo
     *            current uriInfo
     * @param request
     *            current request
     */
    public FeatureResource(UriInfo uriInfo, Request request, String id, FeatureStore pstore) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
        this.store = pstore;
    }

    /**
     * Allows to retrieve feature by its id.
     * 
     * @param featId
     *            target feature identifier
     * @return feature is exist
     */
    @GET
    public Response read() {
        // if cannot be null due to state diagram (will list)
        if (!getStore().exist(id)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new FeatureNotFoundException(id).getMessage()).build();
        } else {
            return Response.ok(FeatureJsonMarshaller.marshallFeature(getStore().read(id))).build();
        }
    }

    @POST
    @Path("enable")
    public Response operationEnable() {
        if (!getStore().exist(id)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new FeatureNotFoundException(id).getMessage()).build();
        }
        getStore().enable(id);
        return Response.noContent().build();
    }

    @POST
    @Path("disable")
    public Response operationDisable() {
        if (!getStore().exist(id)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new FeatureNotFoundException(id).getMessage()).build();
        }
        getStore().disable(id);
        return Response.noContent().build();
    }

    /*
     * @Override public void grantRoleOnFeature(String flipId, String roleName) { // TODO Auto-generated method stub
     * 
     * }
     * 
     * @Override public void removeRoleFromFeature(String flipId, String roleName) { // TODO Auto-generated method stub
     * 
     * }
     * 
     * @Override public void enableGroup(String groupName) { // TODO Auto-generated method stub
     * 
     * }
     * 
     * @Override public void disableGroup(String groupName) { // TODO Auto-generated method stub
     * 
     * }
     * 
     * @Override public boolean existGroup(String groupName) { // TODO Auto-generated method stub return false; }
     * 
     * @Override public Map<String, Feature> readGroup(String groupName) { // TODO Auto-generated method stub return null; }
     * 
     * @Override public void addToGroup(String featureId, String groupName) { }
     * 
     * @Override public void removeFromGroup(String featureId, String groupName) { // TODO Auto-generated method stub
     * 
     * }
     */

    /**
     * Create the feature if not exist or update it
     * 
     * @param headers
     *            current request header
     * @param data
     *            feature serialized as JSON
     * @return 204 or 201
     */
    @PUT
    public Response upsertFeature(@Context HttpHeaders headers, byte[] data) {
        Feature feat = FeatureJsonMarshaller.unMarshallFeature(new String(data));
        if (!getStore().exist(feat.getUid())) {
            getStore().create(feat);
            // No header location as PUT and not POST
            // header location
            return Response.status(Response.Status.CREATED).build();
        }
        getStore().update(feat);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * Delete feature by its id.
     * 
     * @return delete by its id.
     */
    @DELETE
    public Response deleteFeature() {
        if (id == null || "".equals(id)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid URL : Must be '/features/{id}' with {id} not null nor empty").build();
        }
        if (!getStore().exist(id)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new FeatureNotFoundException(id).getMessage()).build();
        } else {
            getStore().delete(id);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    /**
     * Getter accessor for attribute 'store'.
     * 
     * @return current value of 'store'
     */
    public FeatureStore getStore() {
        return store;
    }




}