package repository;

import model.Canteiro;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CanteiroRepository extends MongoRepository<Canteiro, String> {
}