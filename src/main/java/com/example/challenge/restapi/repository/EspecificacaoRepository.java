package com.example.challenge.restapi.repository;

import com.example.challenge.restapi.model.Especificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspecificacaoRepository extends JpaRepository<Especificacao, Long> {
    List<Especificacao> findByVeiculoId(Long veiculoId);

    Optional<Especificacao> findByVeiculoIdAndAtributoIgnoreCase(Long veiculoId, String atributo);

    @Modifying
    @Query("DELETE FROM Especificacao e WHERE e.veiculo.id = :veiculoId")
    void deleteByVeiculoId(@Param("veiculoId") Long veiculoId);
}
