package com.example.challenge.restapi.repository;

import com.example.challenge.restapi.model.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    boolean existsByMarcaAndModeloAndVersao(String marca, String modelo, String versao);

    Optional<Veiculo> findByMarcaAndModeloAndVersao(String marca, String modelo, String versao);

    Page<Veiculo> findByMarcaIgnoreCase(String marca, Pageable pageable);

    @Query("""
            SELECT v FROM Veiculo v
            WHERE (:marca IS NULL OR LOWER(v.marca) LIKE LOWER(CONCAT('%', :marca, '%')))
            AND (:modelo IS NULL OR LOWER(v.modelo) LIKE LOWER(CONCAT('%', :modelo, '%')))
            AND (:versao IS NULL OR LOWER(v.versao) LIKE LOWER(CONCAT('%', :versao, '%')))
            """)
    Page<Veiculo> search(
            @Param("marca") String marca,
            @Param("modelo") String modelo,
            @Param("versao") String versao,
            Pageable pageable
    );
}
