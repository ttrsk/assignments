package tatarskiy.assignments.wipro.calculator.adjustments.repository;

import java.util.Objects;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Table("INSTRUMENT_PRICE_MODIFIER")
public class ModifierRecord {

  final Long id;
  final String name;
  final double multiplier;

  @PersistenceCreator
  public ModifierRecord(Long id, String name, double multiplier) {
    this.id = id;
    this.name = name;
    this.multiplier = multiplier;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public double getMultiplier() {
    return multiplier;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModifierRecord that = (ModifierRecord) o;
    return Double.compare(that.multiplier, multiplier) == 0 && Objects.equals(id,
        that.id) && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, multiplier);
  }

}
