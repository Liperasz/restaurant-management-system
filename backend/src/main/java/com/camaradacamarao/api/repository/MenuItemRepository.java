package com.camaradacamarao.api.repository;

import com.camaradacamarao.api.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByActiveTrue();
    List<MenuItem> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}
