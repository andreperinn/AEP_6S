package repository;

import model.Escala;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EscalaRepository extends MongoRepository<Escala, String> {

    List<Escala> findByCanteiroId(String canteiroId);

    boolean existsByCanteiroId(String canteiroId);
}
