package ma.rest.jersey.resource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import ma.rest.entity.Category;
import ma.rest.entity.Item;
import ma.rest.jersey.config.JpaManager;

import java.net.URI;
import java.util.List;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    private final EntityManagerFactory emf = JpaManager.getFactory();

    @GET
    public Response getCategories(@QueryParam("page") @DefaultValue("0") int page,
                                  @QueryParam("size") @DefaultValue("20") int size) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Category> categories = em.createQuery("SELECT c FROM Category c", Category.class)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();

            return Response.ok(categories).build();
        } finally {
            em.close();
        }
    }

    @GET
    @Path("/{id}")
    public Response getCategoryById(@PathParam("id") Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Category category = em.find(Category.class, id);
            if (category == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(category).build();
        } finally {
            em.close();
        }
    }

    @POST
    public Response createCategory(Category category, @Context UriInfo uriInfo) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(category);
            tx.commit();
            URI createdUri = uriInfo.getBaseUriBuilder().path("/categories").path(category.getId().toString()).build();
            return Response.created(createdUri).entity(category).build();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        } finally {
            em.close();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateCategory(@PathParam("id") Long id, Category categoryDetails) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category existingCategory = em.find(Category.class, id);
            if (existingCategory == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            existingCategory.setName(categoryDetails.getName());
            existingCategory.setCode(categoryDetails.getCode());
            existingCategory.setUpdatedAt(java.time.Instant.now());
            Category updatedCategory = em.merge(existingCategory);
            tx.commit();
            return Response.ok(updatedCategory).build();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        } finally {
            em.close();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category category = em.find(Category.class, id);
            if (category == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            em.remove(category);
            tx.commit();
            return Response.noContent().build();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        } finally {
            em.close();
        }
    }

    @GET
    @Path("/{id}/items")
    public Response getItemsForCategory(@PathParam("id") Long id,
                                        @QueryParam("page") @DefaultValue("0") int page,
                                        @QueryParam("size") @DefaultValue("20") int size) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Item> items = em.createQuery("SELECT i FROM Item i WHERE i.category.id = :categoryId", Item.class)
                    .setParameter("categoryId", id)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
            return Response.ok(items).build();
        } finally {
            em.close();
        }
    }
}