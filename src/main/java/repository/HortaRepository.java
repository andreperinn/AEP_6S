package repository;

import model.Horta;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HortaRepository extends MongoRepository<Horta, String> {
}
