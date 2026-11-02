package repository;

import model.Canteiro;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CanteiroRepository extends MongoRepository<Canteiro, String> {

    List<Canteiro> findByHortaId(String hortaId);

    boolean existsByHortaId(String hortaId);
}
