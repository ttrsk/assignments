package tatarskiy.assignments.wipro.calculator.adjustments.repository;

import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PriceModifierRepository extends CrudRepository<ModifierRecord, Long> {

  @Cacheable(value = "priceModifierCache")
  Optional<ModifierRecord> findByName(String name);
}
