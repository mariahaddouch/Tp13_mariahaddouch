package ma.rest.springmvc.repository;

import ma.rest.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("select i from Item i join fetch i.category c where c.id = :categoryId")
    Page<Item> findByCategoryIdWithFetch(@Param("categoryId") Long categoryId, Pageable pageable);
}