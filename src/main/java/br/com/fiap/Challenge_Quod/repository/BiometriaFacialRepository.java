package br.com.fiap.Challenge_Quod.repository;

import br.com.fiap.Challenge_Quod.model.BiometriaFacial;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BiometriaFacialRepository extends MongoRepository<BiometriaFacial, String> {

    Optional<BiometriaFacial> findByTransacaoId(UUID transacaoId);
}