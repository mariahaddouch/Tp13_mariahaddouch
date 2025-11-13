package ma.rest.jersey.resource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import ma.rest.entity.Item;
import ma.rest.jersey.config.JpaManager;

import java.net.URI;
import java.util.List;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private final EntityManagerFactory emf = JpaManager.getFactory();

    @GET
    public Response getItems(@QueryParam("page") @DefaultValue("0") int page,
                             @QueryParam("size") @DefaultValue("20") int size,
                             @QueryParam("categoryId") Long categoryId) {

        EntityManager em = emf.createEntityManager();
        try {
            List<Item> items;
            if (categoryId != null) {
                items = em.createQuery("SELECT i FROM Item i WHERE i.category.id = :categoryId", Item.class)
                        .setParameter("categoryId", categoryId)
                        .setFirstResult(page * size)
                        .setMaxResults(size)
                        .getResultList();
            } else {
                items = em.createQuery("SELECT i FROM Item i", Item.class)
                        .setFirstResult(page * size)
                        .setMaxResults(size)
                        .getResultList();
            }
            return Response.ok(items).build();
        } finally {
            em.close();
        }
    }

    @GET
    @Path("/{id}")
    public Response getItemById(@PathParam("id") Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Item item = em.find(Item.class, id);
            if (item == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(item).build();
        } finally {
            em.close();
        }
    }

    @POST
    public Response createItem(Item item, @Context UriInfo uriInfo) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (item.getCategory() != null && item.getCategory().getId() != null) {
                item.setCategory(em.getReference(item.getCategory().getClass(), item.getCategory().getId()));
            }
            em.persist(item);
            tx.commit();
            URI createdUri = uriInfo.getBaseUriBuilder().path("/items").path(item.getId().toString()).build();
            return Response.created(createdUri).entity(item).build();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        } finally {
            em.close();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateItem(@PathParam("id") Long id, Item itemDetails) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Item existingItem = em.find(Item.class, id);
            if (existingItem == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            existingItem.setName(itemDetails.getName());
            existingItem.setSku(itemDetails.getSku());
            existingItem.setPrice(itemDetails.getPrice());
            existingItem.setStock(itemDetails.getStock());
            existingItem.setUpdatedAt(java.time.Instant.now());

            if (itemDetails.getCategory() != null && itemDetails.getCategory().getId() != null) {
                existingItem.setCategory(em.getReference(itemDetails.getCategory().getClass(), itemDetails.getCategory().getId()));
            }

            Item updatedItem = em.merge(existingItem);
            tx.commit();
            return Response.ok(updatedItem).build();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        } finally {
            em.close();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteItem(@PathParam("id") Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Item item = em.find(Item.class, id);
            if (item == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            em.remove(item);
            tx.commit();
            return Response.noContent().build();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        } finally {
            em.close();
        }
    }
}